package com.learn.redismybits.demo.rateLimiter.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * @author jason
 * @createTime 2017年12月7日上午11:37:30
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
	int limit() default 5;
	int timeout() default 1000;
}
