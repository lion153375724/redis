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
	
	/*public EmbeddedServletContainerCustomizer containerCustomizer() {
		return (container -> {
			ErrorPage page1 = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
			ErrorPage page2 = new ErrorPage(HttpStatus.EXPECTATION_FAILED, "/404.html");
			container.addErrorPages(page1,page2);
		});
	}*/
}
