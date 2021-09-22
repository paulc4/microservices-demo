/*
 * File: RestApiIT.java
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
package io.pivotal.microservices.accounts.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.microservices.accounts.AccountsConfig;
import io.pivotal.microservices.accounts.model.Account;
import io.pivotal.microservices.config.TestConfig;

/**
 * The `RestApiIT` integration test is a MockMvc test for testing the new REST
 * API of the account module. This isn't a mock test. MockMvc just mocks the
 * web server but the rest of it is very real. Hence, another integration test.
 *
 * @author T.N.Silverman
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { TestConfig.class, AccountsConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
@Transactional
@DirtiesContext
class RestApiIT {

	private static final Logger logger = LoggerFactory.getLogger(RestApiIT.class);
	private static final String restApiEndpoint = "/accounts";
	private static final int numOfAccounts = 21;
	// @formatter:off
	@Autowired ObjectMapper mapper; // serializes and deserializes json strings and accounts
    @Autowired private WebApplicationContext webAppContext;
	// @formatter:on

	private MockMvc mockMvc;

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		this.mockMvc = webAppContextSetup(webAppContext).build();
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering '{}' with profile/s '{}'", info.getDisplayName(), Arrays.toString(profiles));
	}

	@ParameterizedTest
	@DisplayName("test get id")
	@CsvSource({ "2,123456001,Dollie R. Schnidt", "3,123456002,Cornelia J. LeClerc", "21,123456020,Maria J. Angelo" })
	// @Disabled
	void whenGetById_ThenFound(Long id, String accountNumber, String owner) throws Exception {
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/" + id))
			   /*.andDo(print())*/
			   .andExpect(content().contentType(HAL_JSON_VALUE))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.number", is(accountNumber)))
			   .andExpect(jsonPath("$.owner", is(owner)));
		// @formatter:on
	}

	@Test
	@DisplayName("test get bogus id")
	// @Disabled
	void whenGetByBogusId_ThenNotFound() throws Exception {
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint + "/0"))
			   /*.andDo(print())*/
			   .andExpect(status().isNotFound());
		// @formatter:on
	}

	@Test
	@DisplayName("test get all accounts")
	// @Disabled
	void whenGetAllAccounts_ThenAllFound() throws Exception {
		// @formatter:off
		mockMvc.perform(get(restApiEndpoint))
		   /*.andDo(print())*/
		   .andExpect(content().contentType(HAL_JSON_VALUE))
		   .andExpect(status().isOk())
		   .andExpect(jsonPath("_embedded.accounts", hasSize(numOfAccounts)));
		// @formatter:on
	}

	@ParameterizedTest(name = "test post account")
	@DisplayName("test post account")
	@CsvSource({ "987654321,Test,999.99", "678954321,Tset,99.999", "243325790,'Tom Silverman',555.55" })
	// @Disabled
	public void whenPostAccount_ThenSaved(String number, String owner, BigDecimal balance) throws Exception {
		// @formatter:off
		Account account = new Account(number, owner,  balance);
		String json = mapper.writeValueAsString(account);
        mockMvc.perform(post(restApiEndpoint).contentType(APPLICATION_JSON_VALUE).content(json))
               /*.andDo(print())*/
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.number", is(number)))
               .andExpect(jsonPath("$.owner", is(owner)))
               .andExpect(jsonPath("_links.self.href", notNullValue()));
        // @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test patch account")
	@CsvSource({ "975233420,'Tom Silverman', 1000.00" })
	// @WithMockUser(username = "client", password = "password", roles = { "CLIENT",
	// "ADMIN" })
	// @Disabled
	public void whenPatchAccount_ThenUpdated(String number, String owner, BigDecimal balance) throws Exception {
		// @formatter:off
		Account account = new Account(number, owner,  balance);
		String json = mapper.writeValueAsString(account);
        MvcResult result = mockMvc.perform(post(restApiEndpoint).contentType(APPLICATION_JSON_VALUE).content(json))
               /*.andDo(print())*/
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.number", is(number)))
               .andExpect(jsonPath("$.owner", is(owner)))
               .andExpect(jsonPath("_links.self.href", notNullValue()))
               .andReturn();
        json = result.getResponse().getContentAsString();
        account = mapper.readValue(json, Account.class);
        Long id = account.getId();
		// CHANGE BALANCE
        account.withdraw(new BigDecimal(500));
		// UPDATE (account must have id)
        mockMvc.perform(patch(restApiEndpoint + "/" + id)
                    .contentType(APPLICATION_JSON_VALUE)
		            .content(mapper.writeValueAsString(account)))
               /*.andDo(print())*/
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.number", is(number)))
               .andExpect(jsonPath("$.owner", is(owner)))
               .andExpect(jsonPath("$.balance", is(500.00D)));
        // @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test put account")
	@CsvSource({ "243325790,'Tom Silverman', 1000.00" })
	// @WithMockUser(username = "client", password = "password", roles = { "CLIENT",
	// "ADMIN" })
	// @Disabled
	public void whenPutAccount_ThenUpdated(String number, String owner, BigDecimal balance) throws Exception {
		// @formatter:off
		Account account = new Account(number, owner,  balance);
		String json = mapper.writeValueAsString(account);
        MvcResult result = mockMvc.perform(post(restApiEndpoint).contentType(APPLICATION_JSON_VALUE).content(json))
               /*.andDo(print())*/
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.number", is(number)))
               .andExpect(jsonPath("$.owner", is(owner)))
               .andExpect(jsonPath("_links.self.href", notNullValue()))
               .andReturn();
        json = result.getResponse().getContentAsString();
        account = mapper.readValue(json, Account.class);
        Long id = account.getId();
		// CHANGE BALANCE
        account = new Account(number,owner,balance);
        account.withdraw(new BigDecimal(500));
		// UPDATE (account does not have id)
        mockMvc.perform(put(restApiEndpoint + "/" + id)
                    .contentType(APPLICATION_JSON_VALUE)
		            .content(mapper.writeValueAsString(account)))
               /*.andDo(print())*/
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.number", is(number)))
               .andExpect(jsonPath("$.owner", is(owner)))
               .andExpect(jsonPath("$.balance", is(500.00D)));
        // @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test delete account")
	@CsvSource({ "579024332,'Tom Silverman', 1000.00" })
	// @WithMockUser(username = "client", password = "password", roles = { "CLIENT",
	// "ADMIN" })
	// @Disabled
	public void whenDeleteAccount_ThenNotFound(String number, String owner, BigDecimal balance) throws Exception {
		// @formatter:off
		Account account = new Account(number, owner,  balance);
		String json = mapper.writeValueAsString(account);
		// POST
        MvcResult result = mockMvc.perform(post(restApiEndpoint).contentType(APPLICATION_JSON_VALUE).content(json))
               /*.andDo(print())*/
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.number", is(number)))
               .andExpect(jsonPath("$.owner", is(owner)))
               .andExpect(jsonPath("_links.self.href", notNullValue()))
               .andReturn();
        json = result.getResponse().getContentAsString();
        account = mapper.readValue(json, Account.class);
        Long id = account.getId();
		// DELETE
        mockMvc.perform(delete(restApiEndpoint + "/" + id))
               /*.andDo(print())*/
               .andExpect(status().isNoContent());
        // GET
        mockMvc.perform(get(restApiEndpoint + "/" + id))
        	   /*.andDo(print())*/
               .andExpect(status().isNotFound());
        // @formatter:on
	}

}
