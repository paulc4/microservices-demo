package io.pivotal.microservices.services.web;

import io.pivotal.microservices.accounts.Account;
import io.pivotal.microservices.accounts.AccountNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AccountsController {

	@Autowired
	protected RestTemplate restTemplate;

	protected String serviceUrl;

	public AccountsController(String serviceUrl) {
		this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl
				: "http://" + serviceUrl;
	}

	@RequestMapping("/accounts/{accountNumber}")
	public Account byNumber(@PathVariable("accountNumber") String accountNumber) {

		Logger.getGlobal().info("byNumber() invoked");

		Account account = restTemplate.getForObject(serviceUrl
				+ "/acounts/{number}", Account.class, accountNumber);

		if (account == null)
			throw new AccountNotFoundException(accountNumber);
		else
			return account;
	}

	@RequestMapping("/accounts/owner/{name}")
	public List<Account> byOwner(@PathVariable("name") String name) {
		Logger.getGlobal().info("byName() invoked:  for " + name);

		Account[] accounts = restTemplate.getForObject(serviceUrl
				+ "/accounts/owner/{name}", Account[].class, name);

		if (accounts == null || accounts.length == 0)
			throw new AccountNotFoundException(name);
		else
			return Arrays.asList(accounts);
	}
}
