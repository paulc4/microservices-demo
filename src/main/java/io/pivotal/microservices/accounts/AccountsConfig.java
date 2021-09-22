/*
 * File: AccountsConfig.java
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
package io.pivotal.microservices.accounts;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.pivotal.microservices.accounts.model.Account;
import io.pivotal.microservices.accounts.repository.AccountRepository;

/**
 * Web configurations for the account service defines resource handlers and an
 * automatic {@code index view} controller and of redirecting from the root of
 * the context {@code '/'} to {@code '/index'}. This is instead of creating a
 * redundant {@code HomeController}
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EntityScan(basePackageClasses = { Account.class })
@EnableJpaRepositories(basePackageClasses = { AccountRepository.class })
@PropertySource({ "classpath:filtered.properties", "classpath:accounts-server.properties", "classpath:${db.vendor.name:h2}.properties" })
public class AccountsConfig {
}
