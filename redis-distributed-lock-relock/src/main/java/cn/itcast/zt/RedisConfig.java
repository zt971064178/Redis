package cn.itcast.zt;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by zhangtian on 2017/5/2.
 */
@Configuration
public class RedisConfig {
    /*
    https://github.com/redisson/redisson/wiki/14.-Integration%20with%20frameworks#145-spring-session
    http://www.cnblogs.com/zhongkaiuu/p/redisson.html
     */
    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("127.0.0.1:6379")
                .setDatabase(0)
                .setPingTimeout(1000)
                .setConnectionPoolSize(50) ;
        return Redisson.create(config) ;
    }
}
