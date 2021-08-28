/*
 * File: WebSecurityConfig.java
 * Creation Date: 14 Aug 2021
 *
 * Copyright (c) 2021 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses  this file to you under the Apache License, Version
 * 2.0 (the "License");  you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.microservices.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Security Configurations of the Web Service module.
 * This configuration is discovered by {@code @ComponentScan}
 * of the {@code WebConfig} configuration class at the root package
 * of the module
 *
 * @author T.N.Silverman
 *
 */
@Configuration
@EnableWebSecurity
@Order(3)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	// @formatter:off
	@Value("${spring.security.user.name:client}") protected String username;
	@Value("${spring.security.user.password:password}") protected String password;
	@Value("${spring.security.user.roles:CLIENT}") protected String[] roles;
    // @formatter:on

	/**
	 * used in rest template exchange calls
	 *
	 * @return http headers map with
	 *         {@code 'Authorization: Basic Y2xpZW50OnBhc3N3b3Jk'} entry
	 */
	@Bean
	public HttpHeaders authHeaders() {
		String auth = username + ":" + password;
		byte[] encoded = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
		String header = "Basic " + new String(encoded);
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, header);
		return headers;
	}

	@Bean
	protected PasswordEncoder webPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
        auth.inMemoryAuthentication()
	    		.withUser(username)
	    		.password(webPasswordEncoder().encode(password))
	    		.roles(roles);
        // @formatter:on
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/favicon.ico", "/resources/**", "/error/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
    	http.csrf()
    	 		.disable()
    	    	.authorizeRequests()
    	    	    .antMatchers("/login**").permitAll()
    	    		.antMatchers("/", "/actuator/**").hasAnyRole(roles)
    	    		.antMatchers("/profile/**", "/account/**", "/accounts/**",  "/evict/**",
    	    				     "/index", "/purge", "/search/**").hasAnyRole(roles)
    	    		.antMatchers("/eureka/**").hasAnyRole(roles)
    	    		.anyRequest().denyAll()
    	    .and()
	    	    .formLogin()
	            .defaultSuccessUrl("/index")
	            .failureUrl("/login?error=true")
	            .and()
	            .requestCache()
	            .requestCache(new HttpSessionRequestCache())
	        .and()
	            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    	    .and()
    	    	.httpBasic();
	}

}
