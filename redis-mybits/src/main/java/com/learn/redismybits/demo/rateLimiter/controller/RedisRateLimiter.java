package com.learn.redismybits.demo.rateLimiter.controller;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

import java.util.List;
import java.util.UUID;

/**
 * 
 * @author jason
 * @createTime 2017年12月7日下午3:11:36
 */
public class RedisRateLimiter {
    private static final String BUCKET = "BUCKET";
    private static final String BUCKET_COUNT = "BUCKET_COUNT";
    private static final String BUCKET_MONITOR = "BUCKET_MONITOR";

    public static String acquireTokenFromBucket(
            Jedis jedis, int limit, long timeout) {
        String identifier = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + "now:"+now);
        Transaction transaction = jedis.multi();

        //删除信号量
        transaction.zremrangeByScore(BUCKET_MONITOR.getBytes(), "-inf".getBytes(), String.valueOf(now - timeout).getBytes());
        //transaction.zremrangeByScore(BUCKET_MONITOR.getBytes(), 0, now - timeout);
        ZParams params = new ZParams();
        params.weightsByDouble(1.0,0.0);
        transaction.zinterstore(BUCKET, params, BUCKET, BUCKET_MONITOR);

        //计数器自增
        transaction.incr(BUCKET_COUNT);
        List<Object> results = transaction.exec();
        long counter = (Long) results.get(results.size() - 1);
        System.out.println("bucket:"+jedis.zrange(BUCKET, 0, -1));
        
        transaction = jedis.multi();
        transaction.zadd(BUCKET_MONITOR, now, identifier);
        transaction.zadd(BUCKET, counter, identifier);
        System.out.println(Thread.currentThread().getName() + "counter:"+counter);
        transaction.zrank(BUCKET, identifier);
        results = transaction.exec();
        //获取排名，判断请求是否取得了信号量
        long rank = (Long) results.get(results.size() - 1);
        System.out.println(Thread.currentThread().getName() + "rank:"+rank);
        if (rank < limit) {
            return identifier;
        } else {//没有获取到信号量，清理之前放入redis 中垃圾数据
            transaction = jedis.multi();
            transaction.zrem(BUCKET_MONITOR, identifier);
            transaction.zrem(BUCKET, identifier);
            transaction.exec();
        }
        return null;
    }
}

