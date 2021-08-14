/*
 *
 */
package io.pivotal.microservices.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.pivotal.microservices.web.WebConfig;
import io.pivotal.microservices.web.dto.AccountRecord;
import io.pivotal.microservices.web.service.WebAccountsService;

/**
 * Mock test case to test the functionality of the {@link WebAccountsService}
 *
 * @author T.N.Silverman
 *
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(properties = { "eureka.client.enabled=false" })
@ContextConfiguration(classes = { WebConfig.class })
@AutoConfigureWebClient // in charge of preparing RestTemplateBuilder
@ActiveProfiles({ "test" })
public class WebAccountsServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(WebAccountsServiceTest.class);
	// @formatter:off
	@MockBean WebAccountsService webAccountsService;
	// @formatter:on

	@BeforeEach
	public void beforeTest(TestInfo info) throws Exception {
		ActiveProfiles annotation = getClass().getDeclaredAnnotation(ActiveProfiles.class);
		String[] profiles = (null == annotation) ? new String[] { "test" } : annotation.value();
		logger.debug("Entering {} with profile/s '{}'", info.getDisplayName(), Arrays.toString(profiles));
	}

	@ParameterizedTest
	@DisplayName("test find by number")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044.00",
		         "3,123456002,Cornelia J. LeClerc,14400.00",
			     "21,123456020,Maria J. Angelo,15380.00"})
	// @formatter:on
	// @Disabled
	void whenFindByNumber_ThenExpected(Long id, String accountNumber, String owner, BigDecimal balance)
			throws Exception {
		AccountRecord expected = new AccountRecord(id, accountNumber, owner, balance);
		Mockito.when(webAccountsService.findByNumber(accountNumber)).thenReturn(expected);
		// @formatter:off
		AccountRecord actual = webAccountsService.findByNumber(accountNumber);
		assertNotNull(actual);
		assertNotNull(actual.getId());
		assertEquals(id, actual.getId());
		assertEquals(owner, actual.getOwner());
		assertEquals(accountNumber, actual.getNumber());
		assertEquals(balance, actual.getBalance());
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test find by id")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044.00",
		         "3,123456002,Cornelia J. LeClerc,14400.00",
			     "21,123456020,Maria J. Angelo,15380.00"})
	// @formatter:on
	// @Disabled
	void whenFindById_ThenExpected(Long id, String accountNumber, String owner, BigDecimal balance) throws Exception {
		AccountRecord expected = new AccountRecord(id, accountNumber, owner, balance);
		Mockito.when(webAccountsService.findById(id)).thenReturn(expected);
		// @formatter:off
		AccountRecord actual = webAccountsService.findById(id);
		assertNotNull(actual);
		assertNotNull(actual.getId());
		assertEquals(id, actual.getId());
		assertEquals(owner, actual.getOwner());
		assertEquals(accountNumber, actual.getNumber());
		assertEquals(balance, actual.getBalance());
		// @formatter:on
	}

	@ParameterizedTest
	@DisplayName("test find by owner")
	// @formatter:off
	@CsvSource({ "2,123456001,Dollie R. Schnidt,17044.00",
		         "3,123456002,Cornelia J. LeClerc,14400.00",
			     "21,123456020,Maria J. Angelo,15380.00"})
	// @formatter:on
	// @Disabled
	void whenFindByOwner_ThenExpected(Long id, String accountNumber, String owner, BigDecimal balance)
			throws Exception {
		List<AccountRecord> accounts = List.of(new AccountRecord(id, accountNumber, owner, balance));
		Mockito.when(webAccountsService.findByOwner(owner)).thenReturn(accounts);
		// @formatter:off
		List<AccountRecord> actual = webAccountsService.findByOwner(owner);
		assertNotNull(actual);
		assertFalse(actual.isEmpty());
		AccountRecord account = actual.stream().filter(a -> a.getNumber().equals(accountNumber)).findAny().orElse(null);
		assertNotNull(account);
		assertEquals(id, account.getId());
		assertEquals(owner, account.getOwner());
		assertEquals(accountNumber, account.getNumber());
		assertEquals(balance, account.getBalance());
		// @formatter:on
	}

}
