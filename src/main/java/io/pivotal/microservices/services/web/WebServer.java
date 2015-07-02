package io.pivotal.microservices.services.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Run as a micro-service client, interacting with the Discovery Server
 * (Eureka).
 * 
 * @author Paul Chapman
 */
@SpringBootApplication
@EnableDiscoveryClient
// Disable component scanner ...
@ComponentScan(useDefaultFilters = false)
public class WebServer {

	public static void main(String[] args) {
		// Tell server to look for web-server.properties or web-server.yml
		System.setProperty("spring.config.name", "web-server");
		SpringApplication.run(WebServer.class, args);
	}

	@Bean
	public AccountsService accountsService() {
		return new AccountsService("http://accounts-service");
	}

	@Bean
	public AccountsController accountsController() {
		return new AccountsController(accountsService());
	}
}
