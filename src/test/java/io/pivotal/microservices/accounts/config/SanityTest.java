/*
 * File: SanityTest.java
 * Creation Date: Aug 7, 2021
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
package io.pivotal.microservices.accounts.config;

import java.util.Arrays;

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

import io.pivotal.microservices.accounts.AccountsConfig;

/**
 * Class sanity SanityTest tests the {@code AccountsConfig} configurations by
 * trying to load the context, which is essentially an empty test.
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AccountsConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
public class SanityTest {

	private static final Logger logger = LoggerFactory.getLogger(SanityTest.class);

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering '{}' with profile/s '{}'", info.getDisplayName(), Arrays.toString(profiles));
	}

	@Test
	@DisplayName("test context loads")
	public void whenTested_ThenContextLoads() {

	}

}
