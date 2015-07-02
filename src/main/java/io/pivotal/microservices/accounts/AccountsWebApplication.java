package io.pivotal.microservices.accounts;

import io.pivotal.microservices.services.accounts.AccountsServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Run the Accounts Service as a stand-alone Spring Boot web-application for
 * testing. To run as a microservice, use {@link AccountsServer}.
 * 
 * @author Paul Chapman
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories("io.pivotal.microservices.accounts")
@ComponentScan("io.pivotal.microservices.accounts")
public class AccountsWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsWebApplication.class, args);
	}
}
