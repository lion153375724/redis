package com.learn.redismybits.demo.top;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.learn.redismybits.RedisMybitsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisMybitsApplication.class)
public class TopServiceTest {
	@Autowired
	TopService_redis topService;
	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void initELKDate(){
		MemberBean memberBean = null;
		Random r = new Random();
		for(int i=0; i<300; i++){
			memberBean = new MemberBean("Second"+i,40+r.nextInt(10),r.nextInt(100)*10,"three");
			try {
				ListOperations ops = redisTemplate.opsForList();
				Gson gson = new Gson();
				System.out.println(gson.toJson(memberBean));
				ops.leftPush("logstash", gson.toJson(memberBean));
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test
	public void init(){
		MemberBean memberBean = null;
		Random r = new Random();
		for(int i=0; i<1000; i++){
			memberBean = new MemberBean("Second"+i,20+r.nextInt(10),r.nextInt(100)*10,"Second");
			try {
				topService.addMember(memberBean);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void addScore(){
		topService.addTop("13", "11", 200);
	}
	
	@Test
	public void addTag(){
		topService.addTag("32", "First");
	}
	
	@Test
	public void listMemberByTag(){
		List<MemberBean> list = topService.listMemberByTag(1, 10, "First");
		for(MemberBean member : list){
			System.out.println(member.getId() + ":" + member.getName() +":" + member.getTag() + ":" + member.getScore());
		}
	}
	
	@Test
	public void month_top_score(){
		List<MemberBean> list = topService.month_top_score(1, 10, new Date());
		for(MemberBean member : list){
			System.out.println(member.getId() + ":" + member.getName() +":" + member.getTag() + ":" + member.getScore());
		}
	}
	
	@Test
	public void week_top_score(){
		List<MemberBean> list = topService.week_top_score(1, 10, new Date());
		for(MemberBean member : list){
			System.out.println(member.getId() + ":" + member.getName() +":" + member.getTag() + ":" + member.getScore());
		}
	}
	
}
