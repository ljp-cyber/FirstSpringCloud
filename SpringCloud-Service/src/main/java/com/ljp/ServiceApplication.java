package com.ljp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;

//@EnableDiscoveryClient适用与任何注册中心，@EnableEurekaClient只适用Eureka
@EnableEurekaClient
@SpringBootApplication
public class ServiceApplication {
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(ServiceApplication.class, args);
		System.in.read();
		SpringApplication.exit(run);
	}
}
