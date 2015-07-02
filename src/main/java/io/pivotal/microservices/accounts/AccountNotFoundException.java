package io.pivotal.microservices.accounts;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccountNotFoundException(String accountNumber) {
		super("No such account: " + accountNumber);
	}
}
