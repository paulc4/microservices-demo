/*
 * File: WebAccountsService.java
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
package io.pivotal.microservices.web.service;

import static org.springframework.http.HttpMethod.GET;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.microservices.web.dto.AccountRecord;

/**
 * Hide the access to the microservice inside this local service.
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
@Service
public class WebAccountsService {

	private final static Logger logger = LoggerFactory.getLogger(WebAccountsService.class);
	// @formatter:off
    @Autowired @LoadBalanced protected RestTemplate restTemplate;
    @Autowired protected ObjectMapper mapper;
    @Autowired protected HttpHeaders authHeaders;
	@Value("${accounts.service.url}") protected String serviceUrl;
    // @formatter:on

	/**
	 * The RestTemplate works because it uses a custom request-factory that uses
	 * Ribbon to look up the service to use. This method simply exists to show this.
	 */
	@PostConstruct
	public void init() {
		this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
		logger.debug("service url: {}", serviceUrl);
	}

	@Cacheable(cacheNames = "accounts", key = "#accountNumber", unless = "#result == null")
	public AccountRecord findByNumber(String accountNumber) {
		logger.debug("findByNumber() invoked: for {}", accountNumber);
		try {
			String url = serviceUrl + "/accounts/search/byNumber?number={accountNumber}";
			HttpEntity<String> request = new HttpEntity<>(authHeaders);
			ResponseEntity<AccountRecord> response = restTemplate.exchange(url, GET, request, AccountRecord.class,
					accountNumber);
			AccountRecord account = response.getBody();
			logger.debug("found {}", account);
			return account;
		} catch (Exception ex) {
			logger.warn("Error: ", ex);
			return null;
		}

	}

	@Cacheable(cacheNames = "accounts", key = "#accountId", unless = "#result == null")
	public AccountRecord findById(Long accountId) {
		logger.debug("findById() invoked: for {}", accountId);
		try {
			HttpEntity<String> request = new HttpEntity<>(authHeaders);
			ResponseEntity<AccountRecord> response = restTemplate.exchange(serviceUrl + "/accounts/{accountId}", GET,
					request, AccountRecord.class, accountId);
			AccountRecord account = response.getBody();
			logger.debug("found {}", account);
			return account;
		} catch (Exception e) {
			logger.warn("Error: {} -> {}", e.getClass(), e.getLocalizedMessage());
			return null;
		}
	}

	/* "#root.args[0]" or "#a0" or "#p0 */
	@Cacheable(cacheNames = "accounts", key = "#name", unless = "#result == null or #result.isEmpty()")
	public List<AccountRecord> findByOwner(String name) {
		logger.debug("byOwner() invoked:  for {}", name);
		List<AccountRecord> accounts = null;
		try { // exchange for list logic
			String url = serviceUrl + "/accounts/search/byOwner?owner={name}";
			HttpEntity<String> request = new HttpEntity<>(authHeaders);
			ResponseEntity<String> response = restTemplate.exchange(url, GET, request, String.class, name);
			JsonNode jsonNode = mapper.readTree(response.getBody());
			String json = jsonNode.at("/_embedded/accounts").toString();
			accounts = mapper.readValue(json, new TypeReference<>() {
			});
		} catch (Exception ex) { // 404
			logger.warn("Exception: ", ex);
		}
		if (null == accounts || accounts.isEmpty()) {
			logger.debug("accounts not found");
			return null;
		} else {
			logger.debug("found {} matching accounts", accounts.size());
			return accounts;
		}
	}

	/**
	 * evict the accounts cache from entries under the given {@code cacheKey} i.e.
	 * {@code accountNumber || accountId || name}
	 *
	 * @param cacheKey the cacheKey
	 */
	@CacheEvict(value = "accounts", key = "{#cacheKey}")
	public void evictSingleCacheValue(String cacheKey) {
		logger.debug("evicted accounts cache entries for '{}'", cacheKey);
	}

	/**
	 * evict the cache
	 */
	@CacheEvict(value = "accounts", allEntries = true)
	public void evictAllCacheValues() {
		logger.debug("evicted all accounts cache entries");
	}
}
