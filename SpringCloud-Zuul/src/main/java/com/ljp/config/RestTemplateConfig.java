package com.ljp.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2RestOperationsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	
	@Autowired
	private Environment environment;
	
	@Bean
	@Primary
	public RestTemplate restTemplate() {
		String property = environment.getProperty("zuul.ignoreSecurityHeaders");
		System.out.println("asdf"+property);
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	//里面配置AuthorizationCodeResourceDetails
	//@EnableConfigurationProperties(xxx.class)启用ConfigurationProperties注解
	//@ConfigurationProperties(prefix = "security.oauth2.client")
	//这个注解可以把prefix里的信息绑定到@Component类或@Bean里相对应的字段，绑定比较宽松（uri—info -》 uriInfo）
	OAuth2RestOperationsConfiguration a;
	
	/*
	 * 角色 A：用户 B：第三方应用（QQ农场，对于QQ来说是第三方应用） C：QQ
	 * 正常情况下QQ农场想要访问QQ，要知道用户账号密码才行
	 * 但是用户不想把QQ密码告诉QQ农场
	 * 
	 * 于是QQ和QQ农场合作，告诉用户你想让QQ农场不知道你的密码，但是允许他访问QQ
	 * 现在我和QQ合作，你可以把“QQ农场在我这里登记的id和密码”和“用户自己的账号密码”告诉我
	 * QQ就知道是用户允许QQ农场访问QQ了，QQ给个相关的授权码用户，用户给QQ农场，QQ农场带着这个授权码我就知道怎么回事了
	 * 
	 * 2、简化模式：用户直接向QQ拿令牌，后直接把令牌给QQ农场
	 * 
	 * 3、用户名密码模式：把密码告诉QQ农场，让农场自己搞定
	 * 
	 * 4、客户端模式：QQ农场自己向QQ注册一个账号和用户账号绑定
	 * 
	 * RestTemplate只有一些http请求的方法
	 * OAuth2RestTemplate继承与RestTemplate，主要添加了一些用于申请token的方法
	 * 构造OAuth2RestTemplate需要客户端信息（resource）和一个客户的OAuth2认证信息的上下文（OAuth2ClientContext)
	 * 所以OAuth2ClientContext明细是每个session一个的，里面主要保存了token，http请求信息等
	 * OAuth2ClientContext构造需要AccessTokenRequest（每个request一个），
	 * 记录每次请求需要的头、cookie、请求url、状态码、授权码等，用于生成一个申请token的请求
	 * ****************spring不允许把session的bean注入单例的bean中，spring 用代理的方式解决这个问题**************************
	 * 申请token流程：
	 * 一、尝试从OAuth2ClientContext读取token，成功就return
	 * 二、失败就从服务器请求token
	 *   1、从 OAuth2ClientContext中获取AccessTokenRequest，把Context中PreservedState放入accessTokenRequest
	 *   2、把任务交给AccessTokenProvider.obtainAccessToken(resource,accessTokenRequest)执行
	 *     a、从accessTokenRequest读取授权码，成功进入第3步
	 *       -》失败若存在StateKey则执行重定向异常，置空Context中token，设置状态码PreservedState，结束
	 *     b、StateKey则用RestTemplate从认证服务器获取，把code和prestate(redirectURL)放进accessTokenRequest？？
	 *     c、获取到code后向服务器申请token（貌似会setCookie）
	 *   3、获得token后发布认证事件，session中保存认证信息，用于下次直接校验
	 */
	@Bean
	public OAuth2RestTemplate oAuth2Resttemplate(OAuth2ClientContext oAuth2ClientContext,
			AuthorizationCodeResourceDetails resourceDetails) {
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, oAuth2ClientContext);
		return restTemplate;
	}
	
	public static void setClientHttpRequestFactory(RestTemplate restTemplate) {
		final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectionRequestTimeout(2000);		
		factory.setConnectTimeout(10000);
		factory.setReadTimeout(72000);
		final HttpClient httpClient = HttpClientBuilder.create()
		        .setRedirectStrategy(new DefaultRedirectStrategy())
		        .build();
		factory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(factory);
	}
}
