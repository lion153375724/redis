package com.learn.redismybits.common.redis;



/**
 * Created by Administrator on 2017/3/1 16:00.
 */
//@Service
public class RedisServiceImpl extends IRedisService<RedisModel> {
    private static final String REDIS_KEY = "TEST_REDIS_KEY";

    @Override
    protected String getRedisKey() {
        return this.REDIS_KEY;
    }
}
