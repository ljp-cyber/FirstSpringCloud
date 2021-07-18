package com.ljp.resourse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/**
	 * 配置http请求过滤策略
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(new RememberOauth2Filter(), WebAsyncManagerIntegrationFilter.class);
		http.antMatcher("/user")//这里同时配置了资源服务器和第三方客户端（sso），只有/user为需要保护资源，其他路径交给sso端处理
			.authorizeRequests()//用户登陆过滤
		    .anyRequest().authenticated()//所有路径，另外可以通过antMatchers或者regexMatchers方法匹配路径，authenticated表示需要认证
		    ;
	}

	/**
	 * 配置资源服务器安全策略
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		super.configure(resources);//使用默认配置
	}

}
