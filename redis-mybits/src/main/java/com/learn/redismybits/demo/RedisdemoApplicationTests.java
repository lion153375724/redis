package com.learn.redismybits.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.learn.redismybits.RedisMybitsApplication;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisMybitsApplication.class)
public class RedisdemoApplicationTests {

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void scanDbKeys(){
        template.execute(new RedisCallback<Iterable<byte[]>>() {
            @Override
            public Iterable<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {

                List<byte[]> binaryKeys = new ArrayList<byte[]>();

                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(5).build());
                while (cursor.hasNext()) {
                    byte[] key = cursor.next();
                    binaryKeys.add(key);
                    System.out.println(new String(key, StandardCharsets.UTF_8));
                }

                try {
                    cursor.close();
                } catch (IOException e) {
                    // do something meaningful
                }

                return binaryKeys;
            }
        });
    }

    /**
     * sadd myset a b c d e f g h i j k l m n
     */
    @Test
    public void scanSet(){
        Cursor<String> cursor = template.opsForSet().scan("scoreZSet",ScanOptions.NONE);
        while (cursor.hasNext()){
            System.out.println(cursor.next());
        }
    }

    /**
     * zadd sortset 89 tom 90 jim 100 david
     */
    @Test
    public void scanZSet(){
        Cursor<ZSetOperations.TypedTuple<String>> cursor = template.opsForZSet().scan("scoreZSet",ScanOptions.NONE);
        while (cursor.hasNext()){
            ZSetOperations.TypedTuple<String> item = cursor.next();
            System.out.println(item.getValue() + ":" + item.getScore());
        }
    }

    /**
     *  hset mymap name "patterncat"
     *  hset mymap email "pt@g.cn"
     *  hset mymap age 20
     *  hset mymap desc "hello"
     *  hset mymap sex "male"
     */
    @Test
    public void scanHash(){
        Cursor<Map.Entry<Object, Object>> curosr = template.opsForHash().scan("articleMap:1", ScanOptions.NONE);
        while(curosr.hasNext()){
            Map.Entry<Object, Object> entry = curosr.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    @Test
    public void pipeline(){
        List<Object> results = template.executePipelined(new RedisCallback<Object>() {
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
                        for(int i=0; i< 10; i++) {
                            stringRedisConn.lPush("myqueue","item"+i);
                        }
                        return null;
                    }
                });
        results.stream().forEach(System.out::println);
    }

    @Test
    public void set() {
        String key = "test-add";
        String value = "hello";
        template.opsForValue().set(key, value);
        assertEquals(value, template.opsForValue().get(key));
    }

    @Test
    public void incrInt(){
        String key = "test-incr3";
        redisTemplate.opsForValue().increment(key, 1);
        assertTrue((Integer) redisTemplate.opsForValue().get(key) == 2);
    }

    @Test
      public void cas() throws InterruptedException, ExecutionException {
        String key = "test-cas-1";
        ValueOperations<String, String> strOps = redisTemplate.opsForValue();
        strOps.set(key, "hello");
        ExecutorService pool  = Executors.newCachedThreadPool();
        List<Callable<Object>> tasks = new ArrayList<>();
        for(int i=0;i<5;i++){
            final int idx = i;
            tasks.add(new Callable() {
                @Override
                public Object call() throws Exception {
                    return redisTemplate.execute(new SessionCallback() {
                        @Override
                        public Object execute(RedisOperations operations) throws DataAccessException {
                            operations.watch(key);
                            String origin = (String) operations.opsForValue().get(key);
                            operations.multi();
                            operations.opsForValue().set(key, origin + idx);
                            Object rs = operations.exec();
                            System.out.println("set:"+origin+idx+" rs:"+rs);
                            return rs;
                        }
                    });
                }
            });
        }
        List<Future<Object>> futures = pool.invokeAll(tasks);
        for(Future<Object> f:futures){
            System.out.println(f.get());
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void wrong_cas_should_execute_in_session_callback() throws InterruptedException, ExecutionException {
        String key = "test-cas-wrong";
        ValueOperations<String, String> strOps = redisTemplate.opsForValue();
        strOps.set(key, "hello");
        ExecutorService pool  = Executors.newCachedThreadPool();
        List<Callable<Object>> tasks = new ArrayList<>();
        for(int i=0;i<5;i++){
            final int idx = i;
            tasks.add(new Callable() {
                @Override
                public Object call() throws Exception {
                    redisTemplate.watch(key);
                    String origin = (String) redisTemplate.opsForValue().get(key);
                    redisTemplate.multi();
                    redisTemplate.opsForValue().set(key, origin + idx);
                    Object rs = redisTemplate.exec();
                    System.out.println("set:"+origin+idx+" rs:"+rs);
                    return rs;
                }
            });
        }
        List<Future<Object>> futures = pool.invokeAll(tasks);
        for(Future<Object> f:futures){
            System.out.println(f.get());
        }
        pool.shutdown();
        pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 处理非string的情况
     * 更加灵活
     */
    @Test
    public void fixType(){
//        ValueOperations<String, Integer> intOps = redisTemplate.opsForValue();
//        intOps.increment("hello",20);
//        assertTrue(intOps.get("hello") == 20);

        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        hashOps.putIfAbsent("test-hash-user-score","patterncat",100);
        hashOps.putIfAbsent("test-hash-user-score","xixicat",90);
        assertTrue(hashOps.get("test-hash-user-score", "patterncat") == 100);
        assertTrue(hashOps.get("test-hash-user-score", "xixicat") == 90);

        ListOperations<String, Integer> listOps = redisTemplate.opsForList();
        SetOperations<String, Integer> setOps = redisTemplate.opsForSet();
        ZSetOperations<String, Integer> zsetOps = redisTemplate.opsForZSet();

    }

    @Test
    public void incr() {
        String key = "test-incr1";
        template.opsForValue().increment(key, 1);
        assertEquals(template.opsForValue().get(key), "1");
    }

    @Test
    public void hset() {
        String key = "userHit";
        int uid = 243546657;
        template.boundHashOps(key).putIfAbsent(uid, 1);
        System.out.println(template.boundHashOps(key).get(uid));
    }

    @Test
    public void should_in_transaction() {
        //execute a transaction
        List<Object> txResults = template.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForSet().add("k1", "value1");
                operations.opsForSet().add("k2", "value2");
                operations.discard();
                // This will contain the results of all ops in the transaction
                return operations.exec();
            }
        });
        System.out.println("Number of items added to set: " + Arrays.deepToString(txResults.toArray()));
    }

}
