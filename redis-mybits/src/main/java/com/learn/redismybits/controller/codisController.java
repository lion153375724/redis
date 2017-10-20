package com.learn.redismybits.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.learn.redismybits.common.codis.JodisTemplate;

@Controller
@RequestMapping("/codis")
public class codisController {
	
	@Autowired
	private JodisTemplate jodisTemplate;
	
	@RequestMapping(value="/add")
	public String add(HttpServletRequest request){
		for(int i =0;i<12;i++){
			jodisTemplate.set("codis_"+i, "codisTest"+i);
		}
		request.setAttribute("msg", "create redis success");
		return "redis";
	}
	
	@RequestMapping(value="/get/{key}")
	public String get(@PathVariable String key ,HttpServletRequest request){
		String value = jodisTemplate.get(key);
		System.out.println("redis value:" + value);
		request.setAttribute("msg", value);
		return "redis";
	}
}
