package com.learn.redismybits.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/redis")
public class RedisTemplateController {
	
	@Resource(name="redisTemplate")
    protected ValueOperations<String, String> valueOper;
	
	@RequestMapping(value="/add")
	public String add(HttpServletRequest request){
		for(int i =0;i<12;i++){
			valueOper.set("redis_"+i, "redisTest"+i);
		}
		request.setAttribute("msg", "create redis success");
		return "redis";
	}
	
	@RequestMapping(value="/get/{key}")
	public String get(@PathVariable String key ,HttpServletRequest request){
		String value = valueOper.get(key).toString();
		System.out.println("redis value:" + value);
		request.setAttribute("msg", value);
		return "redis";
	}
}
