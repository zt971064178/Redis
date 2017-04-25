package cn.itcast.zt;

import cn.itcast.zt.lock.RedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by zhangtian on 2017/4/25.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestLock {

    @Autowired
    private RedisTemplate redisTemplate ;

    @Test
    public void tetsLock() {
        RedisLock redisLock = new RedisLock(redisTemplate, "lock:_lock", 10000, 20000) ;
        try {
            if(redisLock.lock()) {
                // 需要加锁的代码
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 为了让分布式锁的算法更稳键些，持有锁的客户端在解锁之前应该再检查一次自己的锁是否已经超时，再去做DEL操作，因为可能客户端因为某个耗时的操作而挂起，
            // 操作完的时候锁因为超时已经被别人获得，这时就不必解锁了。 ————这里没有做

            // 可以增加判断检查一次自己的锁是否已经超时
            redisLock.unlock();
        }
    }
}
