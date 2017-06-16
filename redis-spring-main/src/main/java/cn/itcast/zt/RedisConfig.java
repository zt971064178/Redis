package cn.itcast.zt;

import cn.itcast.zt.serializer.FastSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by zhangtian on 2017/4/25.
 */
@Configuration
public class RedisConfig {

    /**
     * 注入 RedisConnectionFactory
     */
    @Autowired
    private RedisConnectionFactory redisConnectionFactory ;

    /**
     * 实例化 RedisTemplate 对象
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> functionDomainRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>() ;
        initDomainRedisTemplate(redisTemplate, redisConnectionFactory);

        return  redisTemplate ;
    }

    /**
     * 设置数据存入 redis 的序列化方式
     * @param redisTemplate
     * @param factory
     */
    private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        /*
        // 使用jdk原生序列化
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        // 使用自定义的对象序列化
        redisTemplate.setHashValueSerializer(new RedisObjectSerializer());
        redisTemplate.setValueSerializer(new RedisObjectSerializer());
        */
        // 使用fst快速序列化对象到redis中，对象必须实现序列化接口
        redisTemplate.setHashValueSerializer(new FastSerializer());
        redisTemplate.setValueSerializer(new FastSerializer());

        /*//定义key序列化方式
        //RedisSerializer<String> redisSerializer = new StringRedisSerializer();//Long类型会出现异常信息;需要我们上面的自定义key生成策略，一般没必要
        //定义value的序列化方式
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // template.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();*/

        redisTemplate.setConnectionFactory(factory);
    }

    /**
     * 实例化 HashOperations 对象,可以使用 Hash 类型操作
     * @param redisTemplate
     * @return
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash() ;
    }

    /**
     * 实例化 ValueOperations 对象,可以使用 String 操作
     * @param redisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return  redisTemplate.opsForValue() ;
    }

    /**
     * 实例化 ListOperations 对象,可以使用 List 操作
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList() ;
    }

    /**
     * 实例化 SetOperations 对象,可以使用 Set 操作
     * @param redisTemplate
     * @return
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet() ;
    }

    /**
     * 实例化 ZSetOperations 对象,可以使用 ZSet 操作
     * @param redisTemplate
     * @return
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet() ;
    }

    // pipeLined等操作均在redisTemplate中封装
}
