/*
 * File: Account.java
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

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Persistent account entity with JPA markup.
 *
 * (T.N.Silverman fixed {@link #withdraw(BigDecimal)} and {@link #deposit(BigDecimal)} methods since
 * {@link BigDecimal} is immutable).
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
@Entity
@Table(name = "account", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "owner" }, name = "UQ_ACCOUNT_OWNER") })
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "id", "number", "owner", "balance" })
public class Account implements Serializable {

	@Serial
	private static final long serialVersionUID = -1000376960358338001L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;
	@Column(name = "number", nullable = false, length = 50)
	protected String number;
	@Column(name = "owner", nullable = false, length = 25)
	protected String owner;
	@Column(name = "balance", nullable = false)
	protected BigDecimal balance;

	/**
	 * Default constructor for JPA only.
	 */
	protected Account() {
		super();
		balance = BigDecimal.ZERO;
	}

	public Account(String number, String owner) {
		this();
		this.number = number;
		this.owner = owner;
	}

	public Account(String number, String owner, BigDecimal balance) {
		this(number, owner);
		this.balance = balance;
	}

	public Long getId() {
		return id;
	}

	/**
	 * Set JPA id - for testing and JPA only. Not intended for normal use.
	 *
	 * @param id The new id.
	 */
	protected void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	protected void setNumber(String accountNumber) {
		this.number = accountNumber;
	}

	public String getOwner() {
		return owner;
	}

	protected void setOwner(String owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance.setScale(2, RoundingMode.HALF_EVEN);
	}

	public void withdraw(BigDecimal amount) {
		balance = balance.subtract(amount);
	}

	public void deposit(BigDecimal amount) {
		balance = balance.add(amount);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", number=" + number + ", owner=" + owner + ", balance=" + getBalance() + "]";
	}

}
