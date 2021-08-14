/*
 * File: UrlMaskTest.java
 * Creation Date: 13 Aug 2021
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
package io.pivotal.microservices.services.registration;

import static io.pivotal.microservices.services.registration.RegistrationServer.mask;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import io.pivotal.microservices.registration.RegistrationSecurityConfig;

/**
 * The class UrlMaskTest tests the url masking (hide password) functionality in
 * the {@code RegistrationServer}.
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { RegistrationSecurityConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
public class UrlMaskTest {

	private static final Logger logger = LoggerFactory.getLogger(UrlMaskTest.class);
	private static String client = "client";
	private static String password = "password";
	private static String eurekaUrlFmt = "http://%s%s%s%s172.19.0.2:8761/eureka";

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering {} with profile/s {}", info.getDisplayName(), Arrays.toString(profiles));
	}

	@Test
	@DisplayName("test mask password")
	public void whenUrlContainsPassword_ThenMasked() throws Exception {
		String eurekaUrl = String.format(eurekaUrlFmt, client, ":", password, "@");
		String actual = mask(eurekaUrl);
		logger.debug("masked url: {}", actual);
		assertTrue(actual.contains("*".repeat(password.length())));
	}

	@Test
	@DisplayName("test dont mask password")
	public void whenUrlDoesNotContainsPassword_ThenNotMasked() throws Exception {
		String eurekaUrl = String.format(eurekaUrlFmt, "", "", "", "");
		String actual = mask(eurekaUrl);
		logger.debug("url: {}", actual);
		assertFalse(actual.contains("*"));
	}

	@Test
	@DisplayName("test try mask password without password")
	public void whenUrlContainsPartialInfo_ThenNotMasked() throws Exception {
		String eurekaUrl = String.format(eurekaUrlFmt, client, "", "", "@");
		String actual = mask(eurekaUrl);
		logger.debug("url: {}", actual);
		assertFalse(actual.contains("*"));
	}

	@Test
	@DisplayName("test try mask password with junk url")
	public void whenUrlContainsMalformedInfo_ThenNotMasked() throws Exception {
		String eurekaUrl = String.format(eurekaUrlFmt, "", ":", "", "@");
		String actual = mask(eurekaUrl);
		logger.debug("url: {}", actual);
		assertFalse(actual.contains("*"));
	}

}
