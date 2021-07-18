package com.ljp.resourse;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RememberOauth2Filter implements Filter{
	
	private final static Log logger = LogFactory.getLog(RememberOauth2Filter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//这个是oauth2 从请求中提取Token的方法
		//先从header中提取名为“Authorization”的头，没有的话
		//再从参数中提取名为“access_token”的参数
		//BearerTokenExtractor b;
		System.out.println(request.getClass());
		ModifyHttpServletRequest modifyHttpServletRequest=new ModifyHttpServletRequest((HttpServletRequest)request);
		String token=null;
		token = getTokenByCookie(modifyHttpServletRequest);
		if(token!=null)modifyHttpServletRequest.putHeader("Authorization", token);
//		request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
//		cookie.getValue().substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
		chain.doFilter(modifyHttpServletRequest, response);
	}

	private String getTokenByCookie(ModifyHttpServletRequest modifyHttpServletRequest) {
		final boolean debug = logger.isDebugEnabled();
		Cookie[] cookies = modifyHttpServletRequest.getCookies();
		String result=null;
		if(debug) {
			logger.debug("-------cookies print:--------");
		}
		if(cookies==null)return result;
		for(Cookie cookie : cookies) {
			if(debug) {
				logger.debug(cookie.getName()+":"+cookie.getValue());
			}
			if(cookie.getName().equals("access_token")) {
				result = cookie.getValue();
				break;
			}
		}
		if(debug) {
			logger.debug("--------header print:--------");
			Enumeration<String> headerNames = modifyHttpServletRequest.getHeaderNames();
			while(headerNames.hasMoreElements()) {
				String name=headerNames.nextElement();
				String value=modifyHttpServletRequest.getHeader(name);
				logger.debug(name+":"+value);
			}
			logger.debug("--------Authorization headers print:--------");
			Enumeration<String> headers = modifyHttpServletRequest.getHeaders("Authorization");
			while(headers.hasMoreElements()) {
				String value=headers.nextElement();
				logger.debug(value);
			}
		}
		return result;
	}

}
