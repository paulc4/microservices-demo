/*
 * File: SearchCriteriaTest.java
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
package io.pivotal.microservices.web.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.pivotal.microservices.accounts.AccountsConfig;
import io.pivotal.microservices.web.util.SearchCriteria;

/**
 * The test case SearchCriteriaTest tests the functionality of the
 * {@link SearchCriteria} class. This is another simple test for the sake of
 * Jacoco coverage reports.
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AccountsConfig.class }, properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
class SearchCriteriaTest {

	@Test
	@DisplayName("test equals with self")
	void whenComapredWithSelf_ThenEquals() {
		SearchCriteria actual = new SearchCriteria();
		actual.setSearchText("test");
		assertTrue(actual.equals(actual));
	}

	@Test
	@DisplayName("test not equals with other")
	void whenComapredWithOther_ThenNotEquals() {
		SearchCriteria actual = new SearchCriteria();
		actual.setSearchText("self");
		SearchCriteria other = new SearchCriteria();
		other.setSearchText("other");
		assertFalse(actual.equals(other));
	}

	@Test
	@DisplayName("test not equals with null")
	void whenComapredWithNull_ThenNotEquals() {
		SearchCriteria actual = new SearchCriteria();
		assertFalse(actual.equals(null));
	}

	@Test
	@DisplayName("test not equals with another class instance")
	void whenComapredWithAnotherClassInstance_ThenNotEquals() {
		SearchCriteria actual = new SearchCriteria();
		assertFalse(actual.equals(new Object()));
	}

	@Test
	@DisplayName("test different objects different hascodes")
	void whenDifferentObjects_ThenDifferentHashcodes() {
		SearchCriteria actual = new SearchCriteria();
		actual.setSearchText("self");
		SearchCriteria other = new SearchCriteria();
		other.setSearchText("other");
		assertNotEquals(actual.hashCode(), other.hashCode());
	}

	@Test
	@DisplayName("test equal objects equal hascodes")
	void whenEqualObjects_ThenEqualHashcodes() {
		SearchCriteria actual = new SearchCriteria();
		actual.setSearchText("test");
		SearchCriteria other = new SearchCriteria();
		other.setSearchText("test");
		assertEquals(actual.hashCode(), other.hashCode());
	}

	@Test
	@DisplayName("test set and get account number")
	void whenGetAccountNumber_ThenExpected() {
		String expected = "101010101";
		SearchCriteria criteria = new SearchCriteria();
		criteria.setAccountNumber(expected);
		String actual = criteria.getAccountNumber();
		assertEquals(expected, actual);
	}

	@Test
	@DisplayName("test set and get search text")
	void whenGetSearchText_ThenExpected() {
		String expected = "Test";
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSearchText(expected);
		String actual = criteria.getSearchText();
		assertEquals(expected, actual);
	}

	@Test
	@DisplayName("assert valid with account number")
	void whenSetOnlyAccountNumber_ThenValid() {
		String accountNumber = "101010101";
		SearchCriteria actual = new SearchCriteria();
		actual.setAccountNumber(accountNumber);
		assertTrue(actual.isValid());
	}

	@Test
	@DisplayName("assert valid with search text")
	void whenSetOnlySearchText_ThenValid() {
		String searchText = "Test";
		SearchCriteria actual = new SearchCriteria();
		actual.setSearchText(searchText);
		assertTrue(actual.isValid());
	}

	@Test
	@DisplayName("assert valid with search text")
	void whenSetBothAccountNumberAndSearchTest_ThenInvalid() {
		String accountNumber = "101010101";
		String searchText = "Test";
		SearchCriteria actual = new SearchCriteria();
		actual.setAccountNumber(accountNumber);
		actual.setSearchText(searchText);
		assertFalse(actual.isValid());
	}

	@Test
	@DisplayName("test validate valid")
	void whenValidateValid_ThenValidated() {
		String accountNumber = "101010101";
		SearchCriteria actual = new SearchCriteria();
		actual.setAccountNumber(accountNumber);
		Errors errors = new BeanPropertyBindingResult(actual, "searchCritera");
		assertFalse(actual.validate(errors)); // test for non-existence of validation errors
	}

	@Test
	@DisplayName("test validate invalid")
	void whenValidateInvalid_ThenNotValidated() {
		String accountNumber = "101010101";
		String searchText = "Test";
		SearchCriteria actual = new SearchCriteria();
		actual.setAccountNumber(accountNumber);
		actual.setSearchText(searchText);
		Errors errors = new BeanPropertyBindingResult(actual, "searchCritera");
		assertTrue(actual.validate(errors)); // test for existence of validation errors
	}

	@Test
	@DisplayName("test reject short number")
	void whenValidateInvalidNumber_ThenNotValidated() {
		String accountNumber = "12345678";
		SearchCriteria actual = new SearchCriteria();
		actual.setAccountNumber(accountNumber);
		Errors errors = new BeanPropertyBindingResult(actual, "searchCritera");
		actual.validate(errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	@DisplayName("test reject string number")
	void whenValidateNotANumber_ThenNotValidated() {
		String accountNumber = "abcdefghi";
		SearchCriteria actual = new SearchCriteria();
		actual.setAccountNumber(accountNumber);
		Errors errors = new BeanPropertyBindingResult(actual, "searchCritera");
		actual.validate(errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	@DisplayName("test reject empty number")
	void whenValidateEmptyNumber_ThenNotValidated() {
		String accountNumber = "";
		SearchCriteria actual = new SearchCriteria();
		actual.setAccountNumber(accountNumber);
		Errors errors = new BeanPropertyBindingResult(actual, "searchCritera");
		actual.validate(errors);
		assertTrue(errors.hasErrors());
	}

}
