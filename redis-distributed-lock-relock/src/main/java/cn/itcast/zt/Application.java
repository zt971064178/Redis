package cn.itcast.zt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * http://blog.csdn.net/forezp/article/details/70305336
 * https://github.com/redisson/redisson/wiki/14.-Integration%20with%20frameworks#145-spring-session
 * http://www.cnblogs.com/zhongkaiuu/p/redisson.html
 * Created by zhangtian on 2017/5/2.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args) ;
    }
}
