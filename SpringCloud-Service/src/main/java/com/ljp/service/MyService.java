package com.ljp.service;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/service-a")
@CrossOrigin
public class MyService {
	
	@RequestMapping(value="/hello")
	public String getContent() {
		return "hello,spring Cloud";
	}
	
}
