package io.pivotal.microservices.accounts;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Spring Integration/System test - by using @SpringApplicationConfiguration
 * instead of @ContextConfiguration, it picks up the same configuration that
 * Spring Boot would use.
 * <p>
 * Note 1: We have disabled the discovery client since it is not required for
 * testing (the tests pass without it but generate ugly exceptions failing to
 * contact the discovery server).
 * <p>
 * Note 2: @SpringBootTest does not, of itself, enable auto-configuration.
 * 
 * @author Paul Chapman
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest(classes = AccountsConfiguration.class, properties = { "eureka.client.enabled=false" })
public class AccountsControllerIntegrationTests extends AbstractAccountControllerTests {

}
