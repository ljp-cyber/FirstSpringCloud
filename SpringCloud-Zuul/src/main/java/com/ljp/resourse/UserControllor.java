package com.ljp.resourse;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllor {
	
	//资源服务认证过程，从请求头部或者请求参数中提取key为“Authorization”的令牌码解析，没有认证则失败
	//发出用resttemplate发出userinfouri的请求，获得用户信息，和以上信息对比，相同则认证成功，否则认证失败
	OAuth2AuthenticationProcessingFilter filter;
	
	@Autowired
	OAuth2RestTemplate oAuth2RestTemplate;
	
	@Value("${security.oauth2.resource.user-info-uri}")
	private String userInfoUri;
	
	@GetMapping(value="/user")
    public String user(){
		 ResponseEntity<String> response=oAuth2RestTemplate.getForEntity(userInfoUri, String.class);
		 return response.getBody();
    }
	
    //@GetMapping(value="/user")
    public String test(@RequestHeader(name="Authorization")String token,HttpServletRequest httpServletRequest){
    	System.out.println(httpServletRequest.getSession().getId());
    	token=token.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
    	OAuth2AccessToken accessToken=new DefaultOAuth2AccessToken(token);
    	oAuth2RestTemplate.getOAuth2ClientContext().setAccessToken(accessToken);
    	ResponseEntity<String> forEntity = oAuth2RestTemplate.getForEntity(userInfoUri, String.class);
    	String body = forEntity.getBody();
        return body;
    }
}