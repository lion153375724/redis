package com.learn.redismybits.demo.dataredis;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.learn.redismybits.RedisMybitsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisMybitsApplication.class)
public class DataredisTest {
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	@Test
	public void cas() throws InterruptedException, ExecutionException {
		String key = "test-cas-1";
		ValueOperations<String, Object> strOps = redisTemplate.opsForValue();
		strOps.set(key, "hello");
		ExecutorService pool = Executors.newCachedThreadPool();
		List<Callable<Object>> tasks = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			final int idx = i;
			tasks.add(new Callable() {
				@Override
				public Object call() throws Exception {
					return redisTemplate.execute(new SessionCallback() {
						@Override
						public Object execute(RedisOperations operations)
								throws DataAccessException {
							operations.watch(key);
							String origin = (String) operations.opsForValue()
									.get(key);
							operations.multi();
							operations.opsForValue().set(key, origin + idx);
							Object rs = operations.exec();
							System.out.println("set:" + origin + idx + " rs:"
									+ rs);
							return rs;
						}
					});
				}
			});
		}
		
		/*String key = "test-cas-1";
		jodisTemplate.set(key, "hello");
		ExecutorService pool = Executors.newCachedThreadPool();
		List<Callable<Object>> tasks = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			final int idx = i;
			tasks.add(new Callable() {
				@Override
				public Object call() throws Exception {
					Transaction transaction  = jodisTemplate.multi();
					jodisTemplate.watch(key);
					String origin = jodisTemplate.get(key);
					jodisTemplate.set(key, origin + idx);
					List<Object> rs = transaction.exec();
					System.out.println("set:::::::::::::::::::::" + origin + idx + " rs:"
							+ rs);
					return rs;
				}
			});
		}
		*/
		List<Future<Object>> futures = pool.invokeAll(tasks);
		for (Future<Object> f : futures) {
			System.out.println(f.get());
		}
		pool.shutdown();
		pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
	}
}
