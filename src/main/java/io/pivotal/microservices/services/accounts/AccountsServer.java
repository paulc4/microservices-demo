package io.pivotal.microservices.services.accounts;

import io.pivotal.microservices.accounts.AccountRepository;
import io.pivotal.microservices.accounts.AccountsController;
import io.pivotal.microservices.accounts.AccountsInfrastructureConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 * 
 * @author Paul Chapman
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("io.pivotal.microservices.acccounts")
@EnableJpaRepositories("io.pivotal.microservices.accounts")
@EntityScan("io.pivotal.microservices.accounts")
@Import(AccountsInfrastructureConfig.class)
public class AccountsServer {

	@Autowired
	AccountRepository accountRepository;

	public static void main(String[] args) {
		// Tell server to look for accounts-server.properties or accounts-server.yml
		System.setProperty("spring.config.name", "accounts-server");

		SpringApplication.run(AccountsServer.class, args);
	}

	@Bean
	public AccountsController accountsController() {
		return new AccountsController(accountRepository);
	}
}
