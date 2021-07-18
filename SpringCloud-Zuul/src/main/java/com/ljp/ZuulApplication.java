package com.ljp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;


//zuul应该没有集成了eureka，如果不注册到eureka则路由规则必须用url转发，不可以用serviceiD转发
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class ZuulApplication {
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(ZuulApplication.class, args);
		System.in.read();
		SpringApplication.exit(run);
	}
	
}
