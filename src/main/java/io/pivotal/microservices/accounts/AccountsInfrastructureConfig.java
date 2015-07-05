package io.pivotal.microservices.accounts;

import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

//@Configuration
public class AccountsInfrastructureConfig {

	private Logger logger = Logger.getLogger(AccountsInfrastructureConfig.class
			.getName());

	/**
	 * Creates an in-memory "rewards" database populated with test data for fast
	 * testing
	 */
	//@Bean
	public DataSource dataSource() {
		logger.info("dataSource() invoked");

		DataSource dataSource = (new EmbeddedDatabaseBuilder())
				.addScript("classpath:testdb/schema.sql")
				.addScript("classpath:testdb/data.sql").build();

		logger.info("dataSource = " + dataSource);
		logger.info("System has "
				+ new JdbcTemplate(dataSource).queryForObject(
						"SELECT count(*) FROM T_ACCOUNT",
						Integer.class) + " accounts");
		
		return dataSource;
	}
}
