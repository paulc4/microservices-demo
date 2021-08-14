/*
 * File: SearchCriteria.java
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

import java.util.Objects;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

/**
 * The class search criteria models an accounts search filter object
 *
 * (T.N.Silverman added {@link #equals(Object)} and {@link #hashCode()} methods)
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
public class SearchCriteria {

	private String accountNumber;

	private String searchText;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public boolean isValid() {
		if (StringUtils.hasText(accountNumber))
			return !(StringUtils.hasText(searchText));
		else
			return (StringUtils.hasText(searchText));
	}

	public boolean validate(Errors errors) {
		if (StringUtils.hasText(accountNumber)) {
			if (accountNumber.length() != 9)
				errors.rejectValue("accountNumber", "badFormat", "Account number should be 9 digits");
			else {
				try {
					Integer.parseInt(accountNumber);
				} catch (NumberFormatException e) {
					errors.rejectValue("accountNumber", "badFormat", "Account number should be 9 digits");
				}
			}
			if (StringUtils.hasText(searchText))
				errors.rejectValue("searchText", "nonEmpty", "Cannot specify both account number and search text");
		} else if (StringUtils.hasText(searchText)) {
			// Nothing to do
		} else
			errors.rejectValue("accountNumber", "nonEmpty", "Must specify either an account number or search text");
		return errors.hasErrors();
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountNumber, searchText);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchCriteria other = (SearchCriteria) obj;
		return Objects.equals(accountNumber, other.accountNumber) && Objects.equals(searchText, other.searchText);
	}

	@Override
	public String toString() {
		return (StringUtils.hasText(accountNumber) ? "number: " + accountNumber : "")
				+ (StringUtils.hasText(searchText) ? "text: " + searchText : "");
	}
}
