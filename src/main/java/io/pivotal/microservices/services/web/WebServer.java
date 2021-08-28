/*
 * File: WebServer.java
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
package io.pivotal.microservices.services.web;

import static io.pivotal.microservices.services.registration.RegistrationServer.DEFAULT_EUREKA_URL;
import static io.pivotal.microservices.services.registration.RegistrationServer.EUREKA_URL_KEY;
import static io.pivotal.microservices.services.registration.RegistrationServer.REG_SERVER_HOSTNAME_KEY;
import static io.pivotal.microservices.services.registration.RegistrationServer.mask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import io.pivotal.microservices.web.WebConfig;

/**
 * Accounts web-server. Works as a microservice client, fetching data from the
 * Account-Service. Uses the Discovery Server (Eureka) to find the microservice.
 *
 * @author Paul Chapman
 * @author T.N.Silverman
 */
@SpringBootApplication(exclude = { HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class })
/*
 * we need to annotate a @Configuration with either @EnableDiscoveryClient
 * or @EnableEurekaClient
 *
 * This annotation is optional if we have the
 * spring-cloud-starter-netflix-eureka-client dependency on the classpath (we
 * do).
 */
@EnableDiscoveryClient
@Import({ WebConfig.class })
public class WebServer {

	private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
	private static String eurekaUrl = System.getenv(EUREKA_URL_KEY);

	/**
	 * Run the application using Spring Boot and an embedded servlet engine.
	 *
	 * The logic of finding the eurekaUrl is:
	 *
	 * <pre>
	 * 1. look for environment variable 'EUREKA_URL'
	 * 2. look for system property 'registration.server.hostname'
	 * 3. default to 'localhost'
	 * </pre>
	 *
	 * @param args Program arguments - ignored.
	 */
	public static void main(String[] args) {
		if (null == eurekaUrl) {
			eurekaUrl = System.getProperty(REG_SERVER_HOSTNAME_KEY);
			if (null != eurekaUrl) {
				logger.debug("EUREKA_URL '{}' found in system property '{}'", mask(eurekaUrl), REG_SERVER_HOSTNAME_KEY);
			} else {
				eurekaUrl = DEFAULT_EUREKA_URL;
				logger.debug("EUREKA_URL not found in system/env. defaults to: '{}'", mask(eurekaUrl));
			}
		} else
			logger.debug("EUREKA_URL '{}' found in environment.", mask(eurekaUrl));
		System.setProperty(REG_SERVER_HOSTNAME_KEY, eurekaUrl);
		// Tell server to look for web-server.properties or web-server.yml
		System.setProperty("spring.config.name", "web-server");
		SpringApplication.run(WebServer.class, args);
	}

}
