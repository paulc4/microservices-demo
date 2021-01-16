package io.pivotal.microservices.accounts;

import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import io.pivotal.microservices.exceptions.AccountNotFoundException;

// The following are equivalent, we only need to use one.

// 1. Read test properties from a file - neater when there are multiple properties
@TestPropertySource(locations = "classpath:account-controller-tests.properties")

// 2. Define test properties directly, acceptable here since we only have one.
// @TestPropertySource(properties={"eureka.client.enabled=false"})
public abstract class AbstractAccountControllerTests {

	protected static final String ACCOUNT_1 = "123456789";
	protected static final String ACCOUNT_1_NAME = "Keri Lee";

	@Autowired
	AccountsController accountController;

	@Test
	public void validAccountNumber() {
		Logger.getGlobal().info("Start validAccountNumber test");
		Account account = accountController.byNumber(ACCOUNT_1);

		Assertions.assertNotNull(account);
		Assertions.assertEquals(ACCOUNT_1, account.getNumber());
		Assertions.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}

	@Test
	public void validAccountOwner() {
		Logger.getGlobal().info("Start validAccount test");
		List<Account> accounts = accountController.byOwner(ACCOUNT_1_NAME);
		Logger.getGlobal().info("In validAccount test");

		Assertions.assertNotNull(accounts);
		Assertions.assertEquals(1, accounts.size());

		Account account = accounts.get(0);
		Assertions.assertEquals(ACCOUNT_1, account.getNumber());
		Assertions.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}

	@Test
	public void validAccountOwnerMatches1() {
		Logger.getGlobal().info("Start validAccount test");
		List<Account> accounts = accountController.byOwner("Keri");
		Logger.getGlobal().info("In validAccount test");

		Assertions.assertNotNull(accounts);
		Assertions.assertEquals(1, accounts.size());

		Account account = accounts.get(0);
		Assertions.assertEquals(ACCOUNT_1, account.getNumber());
		Assertions.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}

	@Test
	public void validAccountOwnerMatches2() {
		Logger.getGlobal().info("Start validAccount test");
		List<Account> accounts = accountController.byOwner("keri");
		Logger.getGlobal().info("In validAccount test");

		Assertions.assertNotNull(accounts);
		Assertions.assertEquals(1, accounts.size());

		Account account = accounts.get(0);
		Assertions.assertEquals(ACCOUNT_1, account.getNumber());
		Assertions.assertEquals(ACCOUNT_1_NAME, account.getOwner());
		Logger.getGlobal().info("End validAccount test");
	}

	@Test
	public void invalidAccountNumber() {
		try {
			accountController.byNumber("10101010");
			Assertions.fail("Expected an AccountNotFoundException");
		} catch (AccountNotFoundException e) {
			// Worked!
		}
	}

	@Test
	public void invalidAccountName() {
		try {
			accountController.byOwner("Fred Smith");
			Assertions.fail("Expected an AccountNotFoundException");
		} catch (AccountNotFoundException e) {
			// Worked!
		}
	}
}
