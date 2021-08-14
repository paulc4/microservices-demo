/*
 * File: AccountRepositoryIT.java
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.pivotal.microservices.accounts.AccountsConfig;
import io.pivotal.microservices.accounts.model.Account;
import io.pivotal.microservices.accounts.repository.AccountRepository;

/**
 * test case for {@link AccountRepository}.
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AccountsConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
@Transactional
class AccountRepositoryIT {

	private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryIT.class);
	private static final int numOfAccounts = 21;

	// @formatter:off
	@Autowired private AccountRepository accountRepository;
	// @formatter:on

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering {} with profile/s {}", info.getDisplayName(), Arrays.toString(profiles));
	}

	@ParameterizedTest
	@DisplayName("test find by account number")
	// @formatter:off
	@CsvSource({"123456001,Dollie R. Schnidt",
		        "123456002,Cornelia J. LeClerc",
		        "123456020,Maria J. Angelo"})
	// @formatter:on
	public void whenFindByAccountNumber_ThenFound(String accountNumber, String expected) throws Exception {
		Optional<Account> actual = accountRepository.byNumber(accountNumber);
		// @formatter:off
		assertAll(() -> assertFalse(actual.isEmpty()),
				  () -> assertEquals(expected, actual.get().getOwner()));
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test find by bogus account number")
	// @formatter:off
	@CsvSource({"987654321"})
	// @formatter:on
	public void whenFindByNoneExistingAccountNumber_ThenEmpty(String accountNumber) throws Exception {
		Optional<Account> actual = accountRepository.byNumber(accountNumber);
		assertTrue(actual.isEmpty());
	}

	@Test
	@DisplayName("test find by null account number")
	public void whenFindByNullNumber_ThenEmpty() throws Exception {
		assertTrue(accountRepository.byNumber(null).isEmpty());
	}

	@ParameterizedTest
	@DisplayName("test find by owner")
	// @formatter:off
	@CsvSource({"123456001,Dollie R. Schnidt",
		        "123456002,Cornelia J. LeClerc",
		        "123456020,Maria J. Angelo"})
	// @formatter:on
	public void whenFindByExistingOwner_ThenFound(String expected, String owner) throws Exception {
		List<Account> actual = accountRepository.byOwner(owner);
		// @formatter:off
		assertAll(() -> assertNotNull(actual),
				  () -> assertFalse(actual.isEmpty()),
				  () -> assertEquals(1, actual.size()),
				  () -> assertEquals(expected, actual.get(0).getNumber()));
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test find by bogus owner")
	@CsvSource({ "Tester", "Bugsy" })
	public void whenFindByNoneExistingOwner_ThenEmpty(String owner) throws Exception {
		List<Account> actual = accountRepository.byOwner(owner);
		// @formatter:off
		assertAll(() -> assertNotNull(actual),
				  () -> assertTrue(actual.isEmpty()));
		// @formatter:on
	}

	@Test
	@DisplayName("test find all by owner")
	public void whenFindByEmptyOwner_ThenAllFound() throws Exception {
		List<Account> actual = accountRepository.byOwner("");
		// @formatter:off
		assertAll(() -> assertNotNull(actual),
 		          () -> assertFalse(actual.isEmpty()),
		          () -> assertEquals(numOfAccounts, actual.size()));
		// @formatter:on
	}

	@Test
	@DisplayName("test find by null owner")
	public void whenFindAccountByNullOwner_ThenEmpty() throws Exception {
		assertEquals(numOfAccounts, accountRepository.byOwner(null).size());
	}

	/**
	 * CRUD Tests
	 */

	@Test
	@DisplayName("test save account")
	public void whenSaveNewAccount_ThenHasId() throws Exception {
		Account actual = new Account("546732813", "Test Account", new BigDecimal("1000.00"));
		accountRepository.save(actual);
		assertNotNull(actual.getId());
	}

	@Test
	@DisplayName("test save and update account")
	public void whenUpdateAccount_ThenUpdated() throws Exception {
		Account actual = new Account("546732813", "Test Account", new BigDecimal("1000.00"));
		accountRepository.save(actual);
		actual.withdraw(new BigDecimal("500.00"));
		Account expected = accountRepository.save(actual);
		assertNotNull(expected);
		assertNotNull(expected.getId());
		assertEquals(new BigDecimal("500.00"), expected.getBalance());
	}

	@Test
	@DisplayName("test delete single account")
	public void whenDeleteAccount_ThenNotFound() throws Exception {
		Account actual = new Account("546732813", "Test Account", new BigDecimal("1000.00"));
		accountRepository.save(actual);
		actual.withdraw(new BigDecimal("500.00"));
		assertNotNull(actual);
		assertNotNull(actual.getId());
		Long id = actual.getId();
		accountRepository.delete(actual);
		Optional<Account> expected = accountRepository.findById(id);
		assertTrue(expected.isEmpty());
	}

	@Test
	@DisplayName("test delete all accounts")
	public void whenDeleteAll_ThenCountIsZero() throws Exception {
		accountRepository.deleteAll();
		assertEquals(0, accountRepository.count());
	}

	@Test
	@DisplayName("test get all account")
	public void whenFindAll_ThenAllFound() throws Exception {
		assertEquals(numOfAccounts, accountRepository.findAll().spliterator().getExactSizeIfKnown());
	}

	@Test
	@DisplayName("test count")
	public void whenCount_ThenCorrect() throws Exception {
		assertEquals(numOfAccounts, accountRepository.count());
	}

}
