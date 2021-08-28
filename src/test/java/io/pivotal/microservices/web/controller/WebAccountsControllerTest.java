/*
 * File: WebAccountsControllerTest.java
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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.microservices.web.WebConfig;
import io.pivotal.microservices.web.dto.AccountRecord;
import io.pivotal.microservices.web.service.WebAccountsService;
import io.pivotal.microservices.web.util.SearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * this test mocks the behavior of the {@code WebAccountsService} and tests the {@code WebAccountsController}
 * functionality. It issues MockMvc http method calls to the {@code WebAccountsController} and compares the
 * responses to the mocked expected results. This is not an integration test and there's no real live data
 * involved (not even a repository, let alone a database).
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = { WebAccountsController.class }, properties = { "eureka.client.enabled=false" })
@ContextConfiguration(classes = { WebConfig.class })
@AutoConfigureWebClient // in charge of preparing RestTemplateBuilder
@ActiveProfiles({ "test" })
@WithMockUser(roles = {"CLIENT"})
class WebAccountsControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(WebAccountsControllerTest.class);
	private static final String restApiEndpoint = "/accounts";
	// @formatter:off
	@Autowired MockMvc mockMvc;
	@MockBean WebAccountsService webAccountsService;
	@Autowired ObjectMapper mapper;
	// @formatter:on

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering '{}' with profile/s '{}'", info.getDisplayName(), Arrays.toString(profiles));
	}

	@Test
	@DisplayName("test get index view")
	// @Disabled
	void whenGetHomePage_ThenReturnIndexMav() throws Exception {
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint))
			   /*.andDo(print())*/
			   .andExpect(content().contentType(TEXT_HTML_VALUE+ ";charset=UTF-8"))
			   .andExpect(view().name("index"))
			   .andExpect(status().isOk());
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test by number")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044",
		         "3,123456002,Cornelia J. LeClerc,14400",
			     "21,123456020,Maria J. Angelo,15380"})
	// @formatter:on
	// @Disabled
	void whenGetByNumber_ThenFound(Long id, String accountNumber, String owner, BigDecimal balance) throws Exception {
		AccountRecord account = new AccountRecord(id, accountNumber, owner, balance);
		Mockito.when(webAccountsService.findByNumber(accountNumber)).thenReturn(account);
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/number/{accountNumber}", accountNumber))
			   /*.andDo(print())*/
			   .andExpect(status().isOk())
			   .andExpect(view().name("account"))
			   .andExpect(model().attributeExists("account"))
			   .andExpect(model().attribute("account", is(account)));
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test by id")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044",
		         "3,123456002,Cornelia J. LeClerc,14400",
			     "21,123456020,Maria J. Angelo,15380"})
	// @formatter:on
	// @Disabled
	void whenGetById_ThenFound(Long accountId, String accountNumber, String owner, BigDecimal balance)
			throws Exception {
		AccountRecord account = new AccountRecord(accountId, accountNumber, owner, balance);
		Mockito.when(webAccountsService.findById(accountId)).thenReturn(account);
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/id/{accountId}", accountId))
			   /*.andDo(print())*/
			   .andExpect(status().isOk())
			   .andExpect(view().name("account"))
			   .andExpect(model().attributeExists("account"))
			   .andExpect(model().attribute("account", is(account)));
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test owner search")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044",
		         "3,123456002,Cornelia J. LeClerc,14400",
			     "21,123456020,Maria J. Angelo,15380"})
	// @formatter:on
	// @Disabled
	void whenOwnerSearch_ThenFound(Long accountId, String accountNumber, String owner, BigDecimal balance)
			throws Exception {
		List<AccountRecord> accounts = List.of(new AccountRecord(accountId, accountNumber, owner, balance));
		Mockito.when(webAccountsService.findByOwner(owner)).thenReturn(accounts);
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/owner/{text}", owner))
			   /*.andDo(print())*/
			   .andExpect(status().isOk())
			   .andExpect(view().name("accounts"))
			   .andExpect(model().attributeExists("search"))
			   .andExpect(model().attribute("search", is(owner)))
		       .andExpect(model().attributeExists("accounts"))
			   .andExpect(model().attribute("accounts", is(accounts)));
		// @formatter:on
	}

	@Test
	@DisplayName("test get search form")
	// @Disabled
	void whenGetSearchForm_ThenReturnIndexMav() throws Exception {
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/search"))
			   /*.andDo(print())*/
			   .andExpect(content().contentType(TEXT_HTML_VALUE+ ";charset=UTF-8"))
			   .andExpect(view().name("search"))
			   .andExpect(status().isOk());
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test do search by owner")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044",
		         "3,123456002,Cornelia J. LeClerc,14400",
			     "21,123456020,Maria J. Angelo,15380"})
	// @formatter:on
	// @Disabled
	void whenDoSearchByOwner_ThenFound(Long accountId, String accountNumber, String owner, BigDecimal balance)
			throws Exception {
		List<AccountRecord> accounts = List.of(new AccountRecord(accountId, accountNumber, owner, balance));
		Mockito.when(webAccountsService.findByOwner(owner)).thenReturn(accounts);
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setSearchText(owner);
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/dosearch")
					.param("searchText", owner))
			   /*.andDo(print())*/
			   .andExpect(status().isOk())
			   .andExpect(view().name("accounts"))
			   .andExpect(model().attributeExists("searchCriteria"))
			   .andExpect(model().attribute("searchCriteria", is(searchCriteria)))
		       .andExpect(model().attributeExists("search"))
			   .andExpect(model().attribute("search", is(owner)))
			   .andExpect(model().attributeExists("accounts"))
			   .andExpect(model().attribute("accounts", is(accounts)));
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test do search by number")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044",
		         "3,123456002,Cornelia J. LeClerc,14400",
			     "21,123456020,Maria J. Angelo,15380"})
	// @formatter:on
	// @Disabled
	void whenDoSearchByNumber_ThenFound(Long accountId, String accountNumber, String owner, BigDecimal balance)
			throws Exception {
		AccountRecord account = new AccountRecord(accountId, accountNumber, owner, balance);
		Mockito.when(webAccountsService.findByNumber(accountNumber)).thenReturn(account);
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setAccountNumber(accountNumber);
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/dosearch")
					.param("accountNumber", accountNumber))
			   /*.andDo(print())*/
			   .andExpect(status().isOk())
			   .andExpect(view().name("account"))
			   .andExpect(model().attributeExists("searchCriteria"))
			   .andExpect(model().attribute("searchCriteria", is(searchCriteria)))
			   .andExpect(model().attributeExists("account"))
			   .andExpect(model().attribute("account", is(account)));
		// @formatter:on
	}

}
