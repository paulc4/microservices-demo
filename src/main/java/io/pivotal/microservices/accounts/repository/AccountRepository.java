/*
 * File: AccountRepository.java
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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.pivotal.microservices.accounts.model.Account;

/**
 * Repository for Accoun data implemented using Spring Data JPA.
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
@org.springframework.stereotype.Repository
@RepositoryRestResource(path = "accounts", collectionResourceRel = "accounts")
public interface AccountRepository extends CrudRepository<Account, Long> {
	/**
	 * Find an account with the specified account number.
	 *
	 * @param accountNumber
	 * @return The account if found, empty optional otherwise.
	 */
	// public Optional<Account> findByNumber(String accountNumber);

	/**
	 * Find accounts whose owner name contains the specified string
	 *
	 * @param partialName Any alphabetic string.
	 * @return The list of matching accounts - always non-null, but may be empty.
	 */
	// public List<Account> findByOwnerContainingIgnoreCase(String partialName);

	/**
	 * finds all the accounts with owners matching the given {@code owner}. The
	 * returned list is never null but can be empty.
	 *
	 * @param owner the partial or full name of the account owner
	 * @return a list of accounts matching the given {@code owner}.
	 */
	@Query("SELECT a from Account a WHERE LOWER(a.owner) LIKE CONCAT(LOWER(COALESCE(:owner,LOWER(a.owner))),'%')")
	public List<Account> byOwner(@Param(value = "owner") String owner);

	/**
	 * finds an account by the given account {@code number}.
	 *
	 * @param number the account number
	 * @return and optional of the account or an empty optional if the account is
	 *         not found
	 */
	@Query("SELECT a from Account a WHERE a.number = :number")
	public Optional<Account> byNumber(@Param(value = "number") String number);

}
