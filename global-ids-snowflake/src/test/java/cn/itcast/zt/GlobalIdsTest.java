package cn.itcast.zt;

/**
 * http://www.cnblogs.com/relucent/p/4955340.html
 * 基于redis的分布式ID生成器博客地址：
 * http://blog.csdn.net/hengyunabc/article/details/44244951
 * Created by Administrator on 2017/5/3/003.
 */
public class GlobalIdsTest {
    //==============================Test=============================================
    /** 测试 */
    public static void main(String[] args) {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
