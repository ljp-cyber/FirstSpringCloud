package com.ljp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@EnableEurekaClient
@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
public class ConsummerApplication {
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(ConsummerApplication.class, args);
		System.in.read();
		SpringApplication.exit(run);
	}
	
	//@EnableHystrixDashboard需要此bean支持
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean    
	public ServletRegistrationBean getServlet(){        
		HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();        
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);        
		registrationBean.setLoadOnStartup(1);        
		registrationBean.addUrlMappings("/actuator/hystrix.stream");        
		registrationBean.setName("HystrixMetricsStreamServlet");        
		return registrationBean;  
	}

}
