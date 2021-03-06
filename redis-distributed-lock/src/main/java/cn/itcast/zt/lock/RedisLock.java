package cn.itcast.zt.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by zhangtian on 2017/4/25.
 */
public class RedisLock {

    private static Logger logger = LoggerFactory.getLogger(RedisLock.class) ;
    private RedisTemplate redisTemplate ;
    private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;
    private String lockKey ;// Lock key path

    // 锁超时时间，防止线程在入锁以后，无线的执行等待
    private int expireMsecs = 60 * 1000 ;
    // 锁等待时间，防止线程饥饿
    private int timeoutMsecs = 10 * 1000 ;
    private volatile boolean locked = false;

    /**
     * Detailed constructor with default acquire timeout 10000 msecs and lock expiration of 60000 msecs.
     * @param redisTemplate
     * @param lockKey lock key (ex. account:1, ...)
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey ;
    }

    /**
     * Detailed constructor with default lock expiration of 60000 msecs.
     * @param redisTemplate
     * @param lockKey
     * @param timeoutMsecs
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs) {
        this(redisTemplate, lockKey) ;
        this.timeoutMsecs = timeoutMsecs ;
    }

    /**
     * Detailed constructor.
     * @param redisTemplate
     * @param lockKey
     * @param timeoutMsecs
     * @param expireMsecs
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisTemplate, lockKey, timeoutMsecs) ;
        this.expireMsecs = expireMsecs ;
    }

    public String getLockKey() {
        return lockKey;
    }

    private String get(final String key) {
        Object obj = null ;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer() ;
                    byte[] data = connection.get(serializer.serialize(key)) ;
                    connection.close();

                    if(data == null) {
                        return null ;
                    }
                    return serializer.deserialize(data);
                }
            }) ;
        }catch (Exception e) {
            logger.error("get redis error, key : {}", key);
        }
        return obj != null ? obj.toString() : null ;
    }

    private boolean setNX(final String key, final String value) {
        Object obj = null ;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer() ;
                    Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value)) ;
                    connection.close();
                    return success;
                }
            }) ;
        }catch (Exception e){
            logger.error("setNX redis error, key : {}", key);
        }

        return obj != null ? (Boolean) obj : false ;
    }

    private String getSet(final String key, final String value) {
        Object obj = null ;
        try {
            obj = redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer() ;
                    byte[] ret = connection.getSet(serializer.serialize(key), serializer.serialize(value)) ;
                    connection.close();
                    return serializer.deserialize(ret);
                }
            }) ;
        }catch (Exception e){
            logger.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (String) obj : null;
    }

    /**
     * 获得 lock.
     * 实现思路: 主要是使用了redis 的setnx命令,缓存了锁.
     * reids缓存的key是锁的key,所有的共享, value是锁的到期时间(注意:这里把过期时间放在value了,没有时间上设置其超时时间)
     * 执行过程:
     * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
     * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
     * @return
     * @throws InterruptedException
     */
    public synchronized boolean lock() throws InterruptedException {
        int timeout = timeoutMsecs ;

        while(timeout >= 0){
            long expires = System.currentTimeMillis() + expireMsecs + 1 ;
            String expiresStr = String.valueOf(expires) ;// 锁到期时间

            if(this.setNX(lockKey, expiresStr)) {
                // lock acquired
                locked = true ;
                return true ;
            }

            // 如果没有设置成功（说明该键值已经被设置过了），继续下一步，判断时间是否过期，竞争锁
            String currentValueStr = this.get(lockKey) ;// redis里的时间
            if(currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {// 时间过期了,开始竞争
                // 判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                // lock is expired
                String oldValueStr = this.getSet(lockKey, expiresStr) ;
                // 获取上一个锁到期时间，并设置现在的锁到期时间，
                // 只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                if(oldValueStr != null && oldValueStr.equals(currentValueStr)) {// 判断redis中set之前获取的时间值与返回的旧值是否一致，一致则竞争所成功，否则被其他线程抢前先一步了
                    // 竞争成功
                    // 防止误删（覆盖，因为key是相同的）了他人的锁——这里达不到效果，这里值会被覆盖，但是因为相差了很少的时间，所以可以接受

                    //[分布式的情况下]:如果这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    // lock acquired
                    locked = true ;
                    return locked ;
                }
            }
            timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS ;
            /*
                延迟100 毫秒,  这里使用随机时间可能会好一点,可以防止饥饿线程的出现,即,当同时到达多个线程,
                只会有一个线程获得锁,其他的都用同样的频率进行尝试,后面有来了一些线程,也以同样的频率申请锁,这将可能导致前面来的锁得不到满足.
                使用随机的等待时间可以一定程度上保证公平性
             */
            Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
        }
        return false ;
    }

    /**
     * Acqurired lock release.
     */
    public synchronized void unlock(){
        if(locked) {
            redisTemplate.delete(lockKey);
            locked = false ;
        }
    }
}
