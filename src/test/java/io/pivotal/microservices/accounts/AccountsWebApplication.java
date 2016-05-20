package io.pivotal.microservices.accounts;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.pivotal.microservices.services.accounts.AccountsServer;

/**
 * Allows the service to be run as a stand-alone Spring Boot web-application for
 * testing (in which case there is <i>no</i> microservice registration because
 * we are not using <code>@EnableDiscoveryClient</code>).
 * 
 * @author Paul Chapman
 */
@SpringBootApplication
public class AccountsWebApplication extends AccountsConfiguration {

	protected Logger logger = Logger.getLogger(AccountsWebApplication.class.getName());

	/**
	 * Run the application using Spring Boot and an embedded servlet engine -
	 * FOR TESTING ONLY. This class is normally invoked via
	 * {@link AccountsServer}.
	 * 
	 * @param args
	 *            Program arguments - ignored.
	 */
	public static void main(String[] args) {
		System.setProperty("server.port", "8888");

		// Tell server to look for accounts-server.properties or
		// accounts-server.yml
		System.setProperty("spring.config.name", "accounts-server");

		SpringApplication.run(AccountsWebApplication.class, args);
	}
}
