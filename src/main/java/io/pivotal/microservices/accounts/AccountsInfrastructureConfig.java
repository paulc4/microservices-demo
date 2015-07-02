package io.pivotal.microservices.accounts;

import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.Assert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
public class AccountsInfrastructureConfig {

	/**
	 * Creates an in-memory "rewards" database populated with test data for fast
	 * testing
	 */
	@Bean
	public DataSource dataSource() {
		Logger.getGlobal().info("AccountsInfrastructureConfig dataSource() invoked");

		DataSource dataSource = (new EmbeddedDatabaseBuilder())
				.addScript("classpath:testdb/schema.sql")
				.addScript("classpath:testdb/data.sql").build();

		Logger.getGlobal().info("AccountsInfrastructureConfig: dataSource = " + dataSource);

		int rows = new JdbcTemplate(dataSource).queryForObject(
				"SELECT count(*) FROM T_ACCOUNT", Integer.class);
		Assert.assertEquals(21, rows);
		
		String name = new JdbcTemplate(dataSource).queryForObject(
				"SELECT name FROM T_ACCOUNT WHERE number = '123456789'", String.class);
		Assert.assertEquals(name, "Keri Lee");
	
		return dataSource;
	}
}
