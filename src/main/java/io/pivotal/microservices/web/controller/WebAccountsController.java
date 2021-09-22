/*
 * File: WebAccountsController.java
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
package io.pivotal.microservices.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;

import io.pivotal.microservices.web.dto.AccountRecord;
import io.pivotal.microservices.web.service.WebAccountsService;
import io.pivotal.microservices.web.util.SearchCriteria;

/**
 * Client controller, fetches Account info from the microservice via
 * {@link WebAccountsService}.
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
@Controller
public class WebAccountsController {

	private final static Logger logger = LoggerFactory.getLogger(WebAccountsController.class);
	// @formatter:off
 	@Autowired protected WebAccountsService webAccountsService;
    // @formatter:on

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAllowedFields("accountNumber", "searchText");
	}

	@GetMapping("/accounts")
	public String goHome() {
		return "index";
	}

	@GetMapping("/evict/{cacheKey}")
	public String evict(String cacheKey) {
		webAccountsService.evictSingleCacheValue(cacheKey);
		return "index";
	}

	@GetMapping("/purge")
	public String purge() {
		webAccountsService.evictAllCacheValues();
		return "index";
	}

	@GetMapping("/accounts/number/{accountNumber}")
	public String findByNumber(Model model, @PathVariable("accountNumber") String accountNumber) {
		logger.debug("web-service findByNumber() invoked: {}", accountNumber);
		AccountRecord account = webAccountsService.findByNumber(accountNumber);
		if (account == null) { // no such account
			model.addAttribute("number", accountNumber);
			return "account";
		}
		logger.debug("web-service findByNumber() found: {}", account);
		model.addAttribute("account", account);
		return "account";
	}

	@GetMapping("/accounts/id/{accountId}")
	public String findById(Model model, @PathVariable("accountId") Long accountId) {
		logger.debug("web-service findById() invoked: {}", accountId);
		AccountRecord account = webAccountsService.findById(accountId);
		if (account == null) { // no such account
			model.addAttribute("id", accountId);
			return "account";
		}
		logger.debug("web-service findById() found: {}", account);
		model.addAttribute("account", account);
		return "account";
	}

	@GetMapping("/accounts/owner/{text}")
	public String findByOwner(Model model, @PathVariable("text") String name) {
		logger.debug("web-service findByOwner() invoked: {}", name);
		List<AccountRecord> accounts = new ArrayList<>();
		try {
			accounts = webAccountsService.findByOwner(name);
			logger.debug("web-service findByOwner() found: {} accounts", (accounts == null ? 0 : accounts.size()));
			model.addAttribute("search", name);
		} catch (Exception ex) {
			logger.warn("Exception: ", ex);
		}
		if (accounts != null)
			model.addAttribute("accounts", accounts);
		return "accounts";
	}

	@GetMapping(path = "/accounts/search")
	public String searchForm(Model model) {
		model.addAttribute("searchCriteria", new SearchCriteria());
		return "search";
	}

	@GetMapping(path = "/accounts/dosearch")
	public String doSearch(Model model, SearchCriteria criteria, BindingResult result) {
		logger.debug("web-service doSearch() invoked: {}", criteria);
		criteria.validate(result);
		if (result.hasErrors())
			return "search";
		String accountNumber = criteria.getAccountNumber();
		if (StringUtils.hasText(accountNumber))
			return findByNumber(model, accountNumber);
		else {
			String searchText = criteria.getSearchText();
			return findByOwner(model, searchText);
		}
	}

}
