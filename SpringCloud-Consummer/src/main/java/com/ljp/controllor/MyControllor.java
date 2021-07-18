package com.ljp.controllor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping(value="/web-a")
@CrossOrigin
public class MyControllor {
	
	//@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。
	@Bean
	@LoadBalanced
	public RestTemplate getResttemplate(){

		return new RestTemplate();

	}

	@Autowired
	private RestTemplate resttemplate;
	
	@RequestMapping(value= {"/","index"})
	@HystrixCommand(fallbackMethod = "err")
	public String index() {
		String url="http://PROVIDER-A/service-a/hello";
		//返回值类型和我们的业务返回值一致
		return resttemplate.getForObject(url, String.class);
	}
	
	public String err() {
		return "hello,sorry,err!";
	}
}
