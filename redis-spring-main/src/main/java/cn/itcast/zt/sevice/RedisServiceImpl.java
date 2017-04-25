package cn.itcast.zt.sevice;

import org.springframework.stereotype.Service;

/**
 * Created by zhangtian on 2017/4/25.
 */
@Service
public class RedisServiceImpl extends IRedisService {
    private static final String REDIS_KEY = "TEST_REDIS_KEY";

    @Override
    protected String getRedisKey() {
        return this.REDIS_KEY;
    }
}
