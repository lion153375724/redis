package com.learn.redismybits.demo.mq;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.learn.redismybits.RedisMybitsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisMybitsApplication.class)
public class mqTest {
	
	@Autowired
	StringRedisTemplate template;
	@Autowired
	CountDownLatch latch;
	
	/*@Test
	public void testPublisher(){
		MyPublisher publisher = new MyPublisher("testTopic");
		publisher.publish("messageTest");
	}*/
	
	@Test
	public void testMessageListener() throws InterruptedException{
		System.out.println("Sending message...");
		template.convertAndSend("chat", "Hello from Redis!");

		latch.await();

		//System.exit(0);
	}
}
