package com.ljp.unUse;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

public class MyOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter{

    private RequestCache requestCache = new HttpSessionRequestCache();

    public MyOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
    	System.out.println("MyOAuth2ClientAuthenticationProcessingFilter.dofilter");
		super.doFilter(req, res, chain);
	}

	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
    		throws AuthenticationException, IOException, ServletException {
        requestCache.saveRequest(request, response);
        return super.attemptAuthentication(request, response);
    }
}
