/*
 * File: AccountsSecurityConfig.java
 * Creation Date: 12 Aug 2021
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
package io.pivotal.microservices.accounts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * Security configurations for the account service
 *
 * @author T.N.Silverman
 */
@Configuration
@EnableWebSecurity
@PropertySource({ "classpath:filtered.properties", "classpath:accounts-server.properties"})
@Order(2)
public class AccountsSecurityConfig extends WebSecurityConfigurerAdapter {

	// @formatter:off
	@Value("${spring.security.user.name:client}") protected String username;
	@Value("${spring.security.user.password:password}") protected String password;
	@Value("${spring.security.user.roles:CLIENT}") protected String[] roles;
    // @formatter:on

	/*
	 * Security Configurations
	 */

	@Bean
	PasswordEncoder accountsPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
        auth.inMemoryAuthentication()
	    		.withUser(username)
	    		.password(accountsPasswordEncoder().encode(password))
	    		.roles(roles);
        // @formatter:on
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/favicon.ico", "/h2-console/**", "/resources/**", "/error/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
    	http.csrf()
    	 		.disable()
    	    	.authorizeRequests()
    	    	    .antMatchers("/login**").permitAll()
    	    		.antMatchers("/", "/actuator/**", "/index").hasAnyRole(roles)
    	    		.antMatchers("/profile/**", "/account/**", "/accounts/**", "/search/**").hasAnyRole(roles)
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
		// @formatter:on
	}

}
