/*
 * File: TestConfig.java
 * Creation Date: Aug 11, 2021
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
package io.pivotal.microservices.config;

import java.time.Duration;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;

/**
 * The class TestConfig defines beans for testing. It's prime goal is to
 * avoid I/O errors on http PATCH requests such as "Invalid HTTP
 * method: PATCH; nested exception is java.net.ProtocolException".
 *
 * The errors happen because of the {@code HttpURLConnection} used by default
 * in Spring Boot {@code RestTemplate} which is provided by the standard
 * JDK HTTP library.
 *
 * The issue can be resolved by adding a new {@code HttpRequestFactory} to
 * the RestTemplate. This solution requires Apache HttpClient library on
 * the class path
 *
 * @author T.N.Silverman
 */
@Configuration
@Profile("test")
public class TestConfig {

	@Bean
	public TestRestTemplate testRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        restTemplateBuilder.additionalMessageConverters(List.of(
            new ByteArrayHttpMessageConverter(),
            new StringHttpMessageConverter(),
            new ResourceHttpMessageConverter(),
            new AllEncompassingFormHttpMessageConverter(),
            new MappingJackson2HttpMessageConverter()));
		restTemplateBuilder.setConnectTimeout(Duration.ofMillis(50000));
		HttpClient httpClient = HttpClientBuilder.create().build();
		final var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		restTemplateBuilder.additionalCustomizers(template -> template.setRequestFactory(requestFactory));
		return new TestRestTemplate(restTemplateBuilder);
	}
}
