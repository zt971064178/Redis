package cn.itcast.zt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Created by zhangtian on 2017/4/25.
 */
@Configuration
public class RedisConfig {

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(){
        return new JedisConnectionFactory() ;
    }
}
