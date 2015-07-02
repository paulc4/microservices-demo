package io.pivotal.microservices.accounts;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountsController {

	private AccountRepository accountRepository;

	@Autowired
	public AccountsController(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@RequestMapping("/accounts/{accountNumber}")
	public Account byNumber(@PathVariable("accountNumber") String accountNumber) {

		Logger.getGlobal().info("byNumber() invoked");
		Account account = accountRepository.findByNumber(accountNumber);

		if (account == null)
			throw new AccountNotFoundException(accountNumber);
		else
			return account;
	}

	@RequestMapping("/accounts/owner/{name}")
	public List<Account> byOwner(@PathVariable("name") String name) {
		Logger.getGlobal().info(
				"byName() invoked: " + accountRepository.getClass().getName()
						+ " for " + name);

		List<Account> accounts = accountRepository.findByOwnerContaining(name);

		if (accounts == null || accounts.size() == 0)
			throw new AccountNotFoundException(name);
		else
			return accounts;
	}
}
