package cn.itcast.zt.redis.lock;

import cn.itcast.zt.exception.UnableToAquireLockException;
import cn.itcast.zt.redis.AquiredLockWorker;
import cn.itcast.zt.redis.DistributedLocker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangtian on 2017/5/2.
 */
@Component
public class RedisLocker implements DistributedLocker {

    private static final String LOCKER_PREFIX = "lock:" ;
    @Autowired
    private RedissonClient redissonClient ;

    @Override
    public <T> T lock(String resourceName, AquiredLockWorker<T> worker) throws UnableToAquireLockException, Exception {
        return lock(resourceName, worker,100);
    }

    @Override
    public <T> T lock(String resourceName, AquiredLockWorker<T> worker, int lockTime) throws UnableToAquireLockException, Exception {
        RLock lock = redissonClient.getLock(LOCKER_PREFIX +resourceName) ;
        boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS) ;

        if(success) {
            try {
                return worker.invokeAfterLockAquire() ;
            }finally {
                lock.unlock();
            }
        }
        throw new UnableToAquireLockException();
    }
}
