/*
 * File: AccountRecordTest.java
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
package io.pivotal.microservices.web.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.pivotal.microservices.accounts.AccountsConfig;
import io.pivotal.microservices.web.dto.AccountRecord;

/**
 * The test AccountRecordTest tests the basic constructs of domain object
 * {@link AccountRecord}
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AccountsConfig.class }, properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
public class AccountRecordTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountRecordTest.class);

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering {} with profile/s {}", info.getDisplayName(), Arrays.toString(profiles));
	}

	@Test
	@DisplayName("test construct record")
	public void whenConstruct_ThenAssigned() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		assertEquals(id, actual.id());
		assertEquals(number, actual.number());
		assertEquals(owner, actual.owner());
		assertEquals(balance, actual.balance());
	}

	@Test
	@DisplayName("test record equals with self")
	public void whenComparedWithSelf_ThenEquals() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		assertTrue(actual.equals(actual));
	}

	@Test
	@DisplayName("test record not equals with null")
	public void whenComparedWithNull_ThenNotEquals() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		assertFalse(actual.equals(null));
	}

	@Test
	@DisplayName("test record equals with other")
	public void whenComparedWithEqualOther_ThenEquals() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		AccountRecord other = new AccountRecord(id, number, owner, balance);
		assertTrue(actual.equals(other));
	}

	@Test
	@DisplayName("test record not equals with other")
	public void whenComparedWithOther_ThenNotEquals() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		BigDecimal delta = new BigDecimal("500.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		AccountRecord other = new AccountRecord(id, number, owner, delta);
		assertFalse(actual.equals(other));
	}

	@Test
	@DisplayName("test record not equals with other class instance")
	public void whenComparedWithOtherClassInstance_ThenNotEquals() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		assertFalse(actual.equals(new Object()));
	}

	@Test
	@DisplayName("test records equal hashcodes equal")
	public void whenEqualAccountRecordsCompared_ThenSameHashcodes() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		AccountRecord other = new AccountRecord(id, number, owner, balance);
		assertEquals(actual.hashCode(), other.hashCode());
	}

	@Test
	@DisplayName("test different record different hashcodes")
	public void whenDifferentAccountRecordsCompared_ThenDifferentHashcodes() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		BigDecimal delta = new BigDecimal("500.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		AccountRecord other = new AccountRecord(id, number, owner, delta);
		assertNotEquals(actual.hashCode(), other.hashCode());
	}

	@Test
	@DisplayName("test to string")
	public void whenToString_ThenDescriptive() throws Exception {
		Long id = 987654321L;
		String number = "975313579";
		String owner = "Test AccountRecord Owner";
		BigDecimal balance = new BigDecimal("1000.00");
		AccountRecord actual = new AccountRecord(id, number, owner, balance);
		String toString = actual.toString();
		assertThat(toString).contains(List.of(number, owner, balance.toString()));
	}


}
