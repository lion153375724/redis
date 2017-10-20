package com.learn.redismybits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan //druid等一些用到了servlet配置的需要配置此项
public class RedisMybitsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisMybitsApplication.class, args);
	}
}
