package com.ljp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
public class AuthorServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private TokenStore tokenStore;

	@Bean
	public TokenStore tokenStore() {
		// 这里为了简单达到目的，直接使用内存存储Token和用户信息。
		return new InMemoryTokenStore();
	}

	// 配置认证相关属性
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		System.out.println("AuthorizationServerEndpointsConfigurer");
		endpoints.tokenStore(tokenStore);
	}

	// 配置认证服务的端点Endpoints相关属性
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		System.out.println("AuthorizationServerSecurityConfigurer");
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		// 允许表单认证
		security.allowFormAuthenticationForClients();
        // 允许check_token访问
		//security.checkTokenAccess("permitAll()");
	}

	// 配置客户端(即是认证入口，在这里是指zuul模块的sso客户端)详情
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("service-zuul")
				 .secret(passwordEncoder.encode("secret"))
				 //.authorizedGrantTypes("authorization_code","password","client_credentials","refresh_token","implicit")
				.authorizedGrantTypes("authorization_code")// 该client允许的授权类型
				.redirectUris("http://localhost:8769/login")
				.scopes("read","write");
	}
	
}
