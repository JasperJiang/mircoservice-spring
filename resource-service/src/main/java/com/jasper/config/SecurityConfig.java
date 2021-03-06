package com.jasper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Created by jjiang153 on 2017/4/25.
 */
@Configuration
@EnableResourceServer
//@EnableWebSecurity
public class SecurityConfig extends ResourceServerConfigurerAdapter {

    @Value("${jwtKeyValue}")
    private String jwtKeyValue;

    @Value("${oauth2.resourceId}")
    private String resourceId;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(this.jwtKeyValue);
        return converter;
    }

    @Bean
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices()).resourceId(this.resourceId);
    }

    private static final String[] NON_SECURED_URLS = { "/swagger-resources/**", "/v2/api-docs", "/webjars/**",
            "/swagger-ui.html", "/index.html","/images/**" };


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(NON_SECURED_URLS).permitAll();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/index").access("#oauth2.hasScope('operation')");
    }

}