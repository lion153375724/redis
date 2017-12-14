package com.learn.redismybits.demo.rateLimiter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.redismybits.demo.rateLimiter.config.RateLimiter;

@RestController
public class RateLimiterController {

	@RequestMapping("/rateLimiter/index")
	@RateLimiter(limit = 2,timeout = 1000)
	public String index(){
		return "success";
	}
}
