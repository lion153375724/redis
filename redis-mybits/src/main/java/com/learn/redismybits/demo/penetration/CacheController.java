package com.learn.redismybits.demo.penetration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.redismybits.demo.rateLimiter.config.RateLimiter;
import com.learn.redismybits.service.OrderService;

@RestController
public class CacheController {

	@Autowired
	public OrderService orderService;	
	
	@RequestMapping("/cache/index")
	public String index(){
		System.out.println(orderService.countPriceByUid("1"));
		return "success";
	}
}
