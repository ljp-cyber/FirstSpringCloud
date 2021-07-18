package com.ljp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication
@EnableEurekaClient
@EnableAuthorizationServer
public class OauthApplication {
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(OauthApplication.class, args);
		System.in.read();
		SpringApplication.exit(run);
	}
	
}
