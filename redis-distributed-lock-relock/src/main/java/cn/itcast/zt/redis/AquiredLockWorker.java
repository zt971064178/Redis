package cn.itcast.zt.redis;

/**
 * 主要是用于获取锁后需要处理的逻辑：
 * Created by zhangtian on 2017/5/2.
 */
public interface AquiredLockWorker<T> {
    /**
     * 获取锁后需要处理的逻辑
     * @return
     * @throws Exception
     */
    T invokeAfterLockAquire() throws Exception ;
}
