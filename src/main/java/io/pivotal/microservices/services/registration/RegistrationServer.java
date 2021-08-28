/*
 * File: RegistrationServer.java
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
package io.pivotal.microservices.services.registration;

import java.net.URL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import io.pivotal.microservices.registration.RegistrationSecurityConfig;

/**
 * Run a Eureka registration server.
 *
 * (to exclude security, place SecurityAutoConfiguration class and
 * ManagementWebSecurityAutoConfiguration class in the exclude section)
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
@SpringBootApplication(exclude = { HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class })
@EnableEurekaServer
@PropertySource({ "classpath:filtered.properties" })
@Import({ RegistrationSecurityConfig.class })
public class RegistrationServer {

	public static final String REG_SERVER_HOSTNAME_KEY = "registration.server.hostname";
	public static final String EUREKA_URL_KEY = "EUREKA_URL";
	public static final String DEFAULT_EUREKA_URL = "localhost";

	/**
	 * Run the application using Spring Boot and an embedded servlet engine.
	 *
	 * @param args Program arguments - ignored.
	 */
	public static void main(String[] args) {
		// instruct the server to look for registration.properties or registration.yml
		System.setProperty("spring.config.name", "registration-server");
		SpringApplication.run(RegistrationServer.class, args);
	}

	/**
	 * Used to mask the password in a basic auth URL
	 *
	 * @param url the url
	 * @return the url with the password section of the {@code userInfo} masked
	 */
	public static String mask(String url) {
		try {
			URL _url = new URL(url);
			String userInfo = _url.getUserInfo();
			if (null == userInfo || !userInfo.contains(":"))
				return url;
			String[] userInfoData = userInfo.split(":");
			String password = userInfoData[1];
			return url.replace(password, "*".repeat(password.length()));
		} catch (Exception ex) { // not url argument or malformed
			return url;
		}
	}

}
