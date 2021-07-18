package com.ljp.sso;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.ljp.unUse.MyOAuth2ClientAuthenticationProcessingFilter;

@Configuration
//Bean的加载顺序有时会影响到容器的初始化
// @Order(101)
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	/*
	 * 貌似只要提供继承与某filter的Filter，springsecurity就会默认用你的filter代替原来的filter
	 * OAuth2ClientAuthenticationProcessingFilter会捕获"/login"登录请求，
	 * 记录原来的请求（用于认证成功后返回原请求），然后到认证服务器申请令牌
	 * 授权码模式需要两步登录（获取授权码，获取令牌），第一步需要和用户交互（点击允许授权）
	 * 在重定向地址设置为"/login"时，用户同意授权后又会重定向到"/login"，被
	 * OAuth2ClientAuthenticationProcessingFilter捕获，继续执行获取令牌请求直至认证成功
	 * #认证成功会把令牌保存到session中，所以下次携带sessionid访问就不要授权直接通过了
	 * #认证成功会发布认证成功事件，ApplicationListener 可以处理，
	 * 		但只携带认证信息，没有携带request（不能对请求进行处理例如重定向）
	 * #认证成功会把重定向到原来的申请（通过 RequestCache类存原请求到session）
	 */
	//@Bean
	public OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter(OAuth2RestTemplate OAuth2RestTemplate) {
		MyOAuth2ClientAuthenticationProcessingFilter filter = new MyOAuth2ClientAuthenticationProcessingFilter("/login");
		filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				System.out.println("AuthenticationSuccessHandler.success:"+authentication.getCredentials());
				//我想把token存到cookie，但是这里是/login，等下会重定向到登录前的url
			}
		});
		filter.setRestTemplate(OAuth2RestTemplate);
		return filter;
	}
	
	//@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*
	 * 只要声明@Bean，spring会自动处理，添加到事件处理器中
	 */
	@Bean
	public ApplicationListener<AuthenticationSuccessEvent> applicationListener(){
		return new ApplicationListener<AuthenticationSuccessEvent>() {
			@Override
			public void onApplicationEvent(AuthenticationSuccessEvent event) {
				System.out.println("AuthenticationSuccessEvent.success");
			}
		};
	}

	/**
	 * 配置用户信息，这里是sso客户端，客户信息应该都保存到认证服务器中了，这里应该不用配置的
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {}
	
	/**
	 * http请求过滤策略
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//这个filter主要时把cookie里的令牌加入到头中，这样就需要修改头，默认的HttpServletRequest是不许允许修改头的需要重写HttpServletRequest
		//http.addFilterBefore(new RememberOauth2Filter(), WebAsyncManagerIntegrationFilter.class);
		http.authorizeRequests()// 需要授权的url
				.antMatchers("/login", "/error", "/token").permitAll()//antMatchers匹配些路徑，permitAll表示放行
				//anyRequest表示所有请求，authenticated表示允许认证的用户访问fullyAuthenticated()不允许anonymous和remember-me用户
				.anyRequest().authenticated()//
				.and().httpBasic()//弹窗认证
				.and().csrf().disable()// 关闭跨域
				;
	}

	/**
	 * 配置网站安全策略，可以再这里忽略一些静态资源的过滤
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		System.out.println("SecurityConfig.WebSecurity");
		super.configure(web);
	}

}
