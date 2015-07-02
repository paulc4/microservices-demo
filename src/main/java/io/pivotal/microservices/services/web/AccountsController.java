package io.pivotal.microservices.services.web;

import io.pivotal.microservices.accounts.Account;
import io.pivotal.microservices.accounts.AccountNotFoundException;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Client controller, fetches Account info from the microservice via
 * {@link AccountsService}.
 * 
 * @author Paul Chapman
 */
@RestController
public class AccountsController {

	@Autowired
	protected AccountsService accountsService;

	protected String serviceUrl;

	public AccountsController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	@RequestMapping("/accounts/{accountNumber}")
	public Account byNumber(@PathVariable("accountNumber") String accountNumber) {

		Logger.getGlobal().info("byNumber() invoked");

		Account account = accountsService.findByNumber(accountNumber);

		if (account == null)
			throw new AccountNotFoundException(accountNumber);
		else
			return account;
	}

	@RequestMapping("/accounts/owner/{name}")
	public List<Account> byOwner(@PathVariable("name") String name) {
		Logger.getGlobal().info("byName() invoked:  for " + name);

		List<Account> accounts = accountsService.byOwnerContains(name);

		if (accounts == null)
			throw new AccountNotFoundException(name);
		else
			return accounts;
	}
}
