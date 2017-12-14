package com.learn.redismybits.demo.penetration;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.learn.redismybits.RedisMybitsApplication;
import com.learn.redismybits.dto.Order;
import com.learn.redismybits.service.OrderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisMybitsApplication.class)
public class CacheTest {

	@Autowired
	public OrderService orderService;	
	
	@Test
	public void save(){
		for(int i=1;i<100000;i++){
			Order order = new Order();
			order.setUid(1);
			order.setPrice(Double.valueOf(i));
			order.setState("1");
			orderService.save(order);
		}
	}
	
	@Test
	public void countPrice(){
		System.out.println(orderService.countPriceByUid("1"));
	}
	
	private static final int THREAD_COUNT = 10;
	private CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
	
	//多线程插入
	@Test
	public void multiThreadSave() throws InterruptedException{
		new Thread(new orderSave()).start();
		new Thread(new orderSave()).start();
		new Thread(new orderSave()).start();
		new Thread(new orderSave()).start();
		new Thread(new orderSave()).start();
		Thread.currentThread().join();
	}
	
	@Test
	public void countPriceByUid() throws InterruptedException{
		for(int i=0;i<THREAD_COUNT;i++){
			new Thread(new orderRequest()).start();
			countDownLatch.countDown();
		}
		
		Thread.currentThread().join();
	}
	
	private class orderRequest implements Runnable{

		@Override
		public void run() {
			
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("price:"+orderService.countPriceByUid("1"));
		}
	}
	
	private class orderSave implements Runnable{

		@Override
		public void run() {
			for(int i=1;i<100000;i++){
				Order order = new Order();
				order.setUid(1);
				order.setPrice(Double.valueOf(i));
				order.setState("1");
				orderService.save(order);
				System.out.println("################"+i);
			}
		}
		
	}
	
	public static void main(String[] args) {
		
	}
}
