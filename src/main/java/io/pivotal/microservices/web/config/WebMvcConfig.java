/*
 * File: WebMvcConfig.java
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Web Mvc Configurations for the Web Service module.
 * This configuration is discovered by {@code @ComponentScan}
 * of the {@code WebConfig} configuration class at the root package
 * of the module
 *
 * @author T.N.Silverman
 *
 */
@Configuration
@EnableCaching
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("classpath:static/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/index").setViewName("index");
		registry.addRedirectViewController("/", "/index");
	}

	// CACHE CONFIGURATIONS:

	/**
	 * This is the main configuration that will control caching behavior such as
	 * expiration, cache size limits, etc...
	 */

	@Bean
	public Caffeine<Object, Object> caffeineConfig() {
		return Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES);
	}

	@Bean
	public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(caffeine);
		Objects.requireNonNull(caffeineCacheManager.getCache("accounts")).clear();
		return caffeineCacheManager;
	}

	/**
	 * @return customized load balanced RestTemplate
	 */
	// @formatter:off
	@LoadBalanced @Bean
	RestTemplate restTemplate() {
    	RestTemplate restTemplate = new RestTemplate();
    	restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    	return restTemplate;
	}
	// @formatter:on
}
