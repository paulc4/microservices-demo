package io.pivotal.microservices.accounts;

import io.pivotal.microservices.services.accounts.AccountsServer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * Run the Accounts Service as a stand-alone Spring Boot web-application for
 * testing. To run as a microservice, use {@link AccountsServer}.
 * 
 * @author Paul Chapman
 */
@SpringBootApplication
@EntityScan("io.pivotal.microservices.accounts")
@ComponentScan("io.pivotal.microservices.accounts")
@EnableJpaRepositories("io.pivotal.microservices.accounts")
@PropertySource("classpath:db-config.properties")
public class AccountsWebApplication {

	protected Logger logger = Logger
			.getLogger(AccountsInfrastructureConfig.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(AccountsWebApplication.class, args);
	}

	/**
	 * Creates an in-memory "rewards" database populated with test data for fast
	 * testing
	 */
	@Bean
	public DataSource dataSource() {
		logger.info("dataSource() invoked");

		DataSource dataSource = (new EmbeddedDatabaseBuilder())
				.addScript("classpath:testdb/schema.sql")
				.addScript("classpath:testdb/data.sql").build();

		logger.info("dataSource = " + dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Map<String, Object>> accounts = jdbcTemplate
				.queryForList("SELECT number FROM T_ACCOUNT");
		logger.info("System has " + accounts.size() + " accounts");

		// Populate with random balances
		Random rand = new Random();

		for (Map<String, Object> item : accounts) {
			String number = (String) item.get("number");
			BigDecimal balance = new BigDecimal(rand.nextInt(10000000) / 100.0)
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			jdbcTemplate.update(
					"UPDATE T_ACCOUNT SET balance = ? WHERE number = ?",
					balance, number);
		}

		return dataSource;
	}
}
