package com.ljp.config;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	/*
	 * 资源服务器认证流程：
	 * 1、BearerTokenExtractor提取request携带的token
	 * 2、通过认证管理器调用RemoteTokenServices访问check_token获得用户信息明细
	 * 3、认证管理器继续对比token的客户端信息
	 * 4、以上都通过则填入认证结果，发布认证成功事件
	 */
	ApplicationContext appCon;
	OAuth2AuthenticationProcessingFilter a;
	TokenEndpoint b;
	AuthorizationEndpoint c;
	CheckTokenEndpoint d;
	RemoteTokenServices e;
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				//.passwordEncoder(passwordEncoder())
				.withUser("admin").password(passwordEncoder().encode("1234")).roles("ADMIN")
				.and()
				.withUser("user").password(passwordEncoder().encode("1234")).roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(new Filter() {
			
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				HttpServletRequest httpRequest=(HttpServletRequest)request;
				Enumeration<String> headerNames = httpRequest.getHeaderNames();
				System.out.println("sessionid="+httpRequest.getRequestedSessionId());
				while(headerNames.hasMoreElements()) {
					String name=headerNames.nextElement();
					String value=httpRequest.getHeader(name);
					System.out.println(name+":"+value);
				}
				chain.doFilter(request, response);
			}
		}, WebAsyncManagerIntegrationFilter.class);
		System.out.println("HttpSecurity");
		http.authorizeRequests()// 用户登陆过滤
				.antMatchers("/login", "/error","/oauth/**").permitAll()// 除了指定的其他的都要认证，还可以regexMatchers方法匹配路径
				.anyRequest()// 所有路径都过滤，
				.authenticated()// 允许认证的用户访问
				// --------------登陆设置------------
				.and()// 返回HttpSecurity进行配置连接
				.formLogin()// 启用默认登陆页面
				.and()
				.httpBasic()//启用httpBasic认证，对话框认证？
				// --------------跨域防护配置------------
				.and()
				.csrf().disable();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		super.configure(web);
	}

}
