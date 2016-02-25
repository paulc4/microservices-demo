package io.pivotal.microservices.services;


import io.pivotal.microservices.services.web.WebServer;

import org.springframework.boot.SpringApplication;

/**
 * Allow the servers to be invoke from the command-line. The jar is built with
 * this as the <code>Main-Class</code> in the jar's <code>MANIFEST.MF</code>.
 * 
 * @author Paul Chapman
 */
public class Main {

	public static void main(String[] args) {
		
		if (args.length > 0)
			System.setProperty("server.port", args[1]);

		
		SpringApplication.run(WebServer.class, args);
		
	}

	protected static void usage() {
		System.out.println("Usage: java -jar ... [server-port]");
	}
}
