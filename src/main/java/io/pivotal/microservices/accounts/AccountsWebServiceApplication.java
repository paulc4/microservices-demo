package io.pivotal.microservices.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Run the Accounts Service as a stand-alone Spring Boot web-application.
 * 
 * @author Paul Chapman
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories("io.pivotal.microservices.accounts")
@ComponentScan("io.pivotal.microservices.accounts")
public class AccountsWebServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsWebServiceApplication.class, args);
	}
}
