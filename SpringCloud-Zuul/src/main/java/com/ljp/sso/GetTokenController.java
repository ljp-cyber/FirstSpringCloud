package com.ljp.sso;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;;

@RestController
public class GetTokenController {
	
	AuthorizationCodeAccessTokenProvider p;

	@Value("${security.oauth2.client.access-token-uri}")
	private String accessTokenUri;
	
	@Value("${security.oauth2.client.pre-established-redirect-uri}")
	private String redirectUri;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String secret;

	@Autowired
	OAuth2RestTemplate restTemplate;
	
	@GetMapping(value="/token")
	public OAuth2AccessToken token(HttpServletResponse httpServletResponse) {
		OAuth2AccessToken accessToken = restTemplate.getAccessToken();
		System.out.println(accessToken.getValue());
		Cookie cookie=new Cookie("access_token", "Bearer"+
				accessToken.getValue());
		cookie.setMaxAge(60*60*24*7);//设置cookie的生命周期
		httpServletResponse.addCookie(cookie);
		return accessToken;
	}

//	@GetMapping(value = "/getToken")
	public void getToken(@RequestParam String code, @RequestParam String state,
			HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest) throws ServletException, IOException {
		System.out.println(httpServletRequest.getSession().getId());
		System.out.println(code);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("code", code);
		params.add("client_id", "service-zuul");
		params.add("client_secret", "secret");
		params.add("redirect_uri", redirectUri);
		params.add("state", state);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(accessTokenUri, requestEntity, String.class);
		String token = response.getBody();
		if(token!=null) {
			Cookie cookie=new Cookie("access_token", "Bearer"+token.split("\"")[3]);
			cookie.setMaxAge(60*60*24*7);//设置cookie的生命周期
			httpServletResponse.addCookie(cookie);
		}
		httpServletResponse.sendRedirect("/user");
	}
}
