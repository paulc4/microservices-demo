package io.pivotal.microservices.accounts;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.pivotal.microservices.exceptions.AccountNotFoundException;

public abstract class AbstractAccountControllerTests {

	protected static final String ACCOUNT_1 = "123456789";
	protected static final String ACCOUNT_1_NAME = "Keri Lee";

	@Autowired
	AccountsController accountController;

	@Test
	public void validAccountNumber() {
		Logger.getGlobal().info("Start validAccountNumber test");
		Account account = accountController.byNumber(ACCOUNT_1);

		Assert.assertNotNull(account);
		Assert.assertEquals(ACCOUNT_1, account.getNumber());
		Assert.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}
	
	@Test
	public void validAccountOwner() {
		Logger.getGlobal().info("Start validAccount test");
		List<Account> accounts = accountController.byOwner(ACCOUNT_1_NAME);
		Logger.getGlobal().info("In validAccount test");

		Assert.assertNotNull(accounts);
		Assert.assertEquals(1, accounts.size());

		Account account = accounts.get(0);
		Assert.assertEquals(ACCOUNT_1, account.getNumber());
		Assert.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}

	@Test
	public void validAccountOwnerMatches1() {
		Logger.getGlobal().info("Start validAccount test");
		List<Account> accounts = accountController.byOwner("Keri");
		Logger.getGlobal().info("In validAccount test");

		Assert.assertNotNull(accounts);
		Assert.assertEquals(1, accounts.size());

		Account account = accounts.get(0);
		Assert.assertEquals(ACCOUNT_1, account.getNumber());
		Assert.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}
	
	@Test
	public void validAccountOwnerMatches2() {
		Logger.getGlobal().info("Start validAccount test");
		List<Account> accounts = accountController.byOwner("keri");
		Logger.getGlobal().info("In validAccount test");

		Assert.assertNotNull(accounts);
		Assert.assertEquals(1, accounts.size());

		Account account = accounts.get(0);
		Assert.assertEquals(ACCOUNT_1, account.getNumber());
		Assert.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}

	@Test
	public void invalidAccountNumber() {
		try {
			accountController.byNumber("10101010");
			Assert.fail("Expected an AccountNotFoundException");
		} catch (AccountNotFoundException e) {
			// Worked!
		}
	}

	@Test
	public void invalidAccountName() {
		try {
			accountController.byOwner("Fred Smith");
			Assert.fail("Expected an AccountNotFoundException");
		} catch (AccountNotFoundException e) {
			// Worked!
		}
	}
}
