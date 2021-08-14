/*
 * File: AccountRecord.java
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

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Account DTO - used to interact with the {@code WebAccountsService}.
 *
 * @author T.N.Silverman
 */
@JsonRootName("Account")
@JsonIgnoreProperties(ignoreUnknown = true) // ignore hal (hateous) _embedded, _links etc...
public record AccountRecord(Long id, String number, String owner, BigDecimal balance) {

	public Long getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public String getOwner() {
		return owner;
	}

	public BigDecimal getBalance() {
		return balance.setScale(2, RoundingMode.HALF_EVEN);
	}

	@Override
	public String toString() {
		return number + " [" + owner + "]: $" + getBalance();
	}

}
