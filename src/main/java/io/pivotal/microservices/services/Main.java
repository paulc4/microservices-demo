package io.pivotal.microservices.services;

import io.pivotal.microservices.services.accounts.AccountsServer;
import io.pivotal.microservices.services.registration.RegistrationServer;
import io.pivotal.microservices.services.web.WebServer;

import org.springframework.boot.SpringApplication;

public class Main {

	protected static void usage() {
		System.out.println("Usage: java -jar ... <server-name> [server-port]");
		System.out.println("     where server-name is"
				+ " 'registration', 'accounts' or 'web' and server-port > 1024");
	}

	public static void main(String[] args) {

		if (args.length < 1 || args.length > 2) {
			usage();
			return;
		}

		String serverName = args[0].toLowerCase();

		if (args.length == 2) {
			System.setProperty("server.port", args[1]);
		}

		// Tell server to look for <server-name>-server.properties or
		// <server-name>-server.yml
		System.setProperty("spring.config.name", serverName + "-server");

		if (serverName.equals("registration")) {
			SpringApplication.run(RegistrationServer.class, args);
		} else if (serverName.equals("accounts")) {
			SpringApplication.run(AccountsServer.class, args);
		} else if (serverName.equals("web")) {
			SpringApplication.run(WebServer.class, args);
		} else {
			System.out.println("Unknown server: " + serverName);
			usage();
		}
	}
}
