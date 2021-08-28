/*
 * File: AccountTest.java
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
package io.pivotal.microservices.accounts.model;

import io.pivotal.microservices.accounts.AccountsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The test case AccountTest tests the basic constructs of domain object
 * {@link Account}. It is about as important as tits on a bull but the fact is -
 * it helped me find a bug with the deposit and withdraw methods.
 *
 * It also helps me get some extra credit with Jacoco coverage.
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AccountsConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
public class AccountTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountTest.class);

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering '{}' with profile/s '{}'", info.getDisplayName(), Arrays.toString(profiles));
	}

	@Test
	@DisplayName("test construct account with number and owner")
	public void whenCustomConstructed1_ThenAssigned() {
		String number = "975313579";
		String owner = "Test Account Owner";
		Account actual = new Account(number, owner);
		assertNull(actual.getId());
		assertEquals(number, actual.getNumber());
		assertEquals(owner, actual.getOwner());
		assertEquals(new BigDecimal("0.00"), actual.getBalance());
	}

	@Test
	@DisplayName("test construct account with number owner and balance")
	public void whenCustomConstructed2_ThenAssigned() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		Account actual = new Account(number, owner, balance);
		assertNull(actual.getId());
		assertEquals(number, actual.getNumber());
		assertEquals(owner, actual.getOwner());
		assertEquals(balance, actual.getBalance());
	}

	@Test
	@DisplayName("test deposit")
	public void whenDeposit_ThenBalanceIsGreater() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		BigDecimal delta = new BigDecimal("500.00");
		Account actual = new Account(number, owner, balance);
		actual.deposit(delta);
		assertEquals(balance.add(delta), actual.getBalance());
	}

	@Test
	@DisplayName("test withdraw")
	public void whenWithdraw_ThenBalanceIsLower() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		BigDecimal delta = new BigDecimal("500.00");
		Account actual = new Account(number, owner, balance);
		actual.withdraw(delta);
		assertEquals(balance.subtract(delta), actual.getBalance());
	}

	@Test
	@DisplayName("test equals with self")
	public void whenComparedWithSelf_ThenEquals() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		Account actual = new Account(number, owner, balance);
		assertEquals(actual, actual);
	}

	@Test
	@DisplayName("test not equals with null")
	public void whenComparedWithNull_ThenNotEquals() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		Account actual = new Account(number, owner, balance);
		assertNotEquals(null, actual);
	}

	@Test
	@DisplayName("test equals with other")
	public void whenComparedWithEqualOther_ThenEquals() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		Account actual = new Account(number, owner, balance);
		Account other = new Account(number, owner, balance);
		assertEquals(actual, other);
	}

	@Test
	@DisplayName("test not equals with other")
	public void whenComparedWithOther_ThenNotEquals() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		BigDecimal delta = new BigDecimal("500.00");
		Account actual = new Account(number, owner, balance);
		Account other = new Account(number, owner, delta);
		assertNotEquals(actual, other);
	}

	@Test
	@DisplayName("test not equals with other class instance")
	public void whenComparedWithOtherClassInstance_ThenNotEquals() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		Account actual = new Account(number, owner, balance);
		assertNotEquals(actual, new Object());
	}

	@Test
	@DisplayName("test equal accounts equal hashcode")
	public void whenEqualAccountsCompared_ThenSameHashcode() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		Account actual = new Account(number, owner, balance);
		Account other = new Account(number, owner, balance);
		assertEquals(actual.hashCode(), other.hashCode());
	}

	@Test
	@DisplayName("test different accounts different hashcode")
	public void whenDifferentAccountsCompared_ThenDifferentHashcode() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		BigDecimal delta = new BigDecimal("500.00");
		Account actual = new Account(number, owner, balance);
		Account other = new Account(number, owner, delta);
		assertNotEquals(actual.hashCode(), other.hashCode());
	}

	@Test
	@DisplayName("test to string")
	public void whenToString_ThenDescriptive() {
		String number = "975313579";
		String owner = "Test Account Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		Account actual = new Account(number, owner, balance);
		String toString = actual.toString();
		assertThat(toString).contains(List.of(number, owner, balance.toString()));
	}

}
