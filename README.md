# Enhanced Spring Boot Microservices Demo

* This is an enhanced fork for Paul Chapman's [Microservices Demo](https://github.com/paulc4/microservices-demo "Microservices Demo"). It includes many aspects that make the original demo more production
ready and a little more 'Springy` in spirit.


* Version v2.2.0 (Aug 2021) corresponds to Spring Boot `2.5.3` and Spring Cloud `2020.0.3` release
train.


* The application was developed and built with [OpenJdk 16.0.1](https://jdk.java.net/archive/ "OpenJdk 16.0.1").

## Table of contents

* [What's here?](#what-s-here)
* [(Optional) Prepare a MySQL schema](#prepare-the-mysql-schema)
* [Testing the application](#testing-the-application)
* [Running the application](#running-the-application)
* [Changelog v2.2.0](#changelog-v2-2-0-release)
    * [Persistence](#persistence)
    * [Resources](#resources)
    * [Accounts REST API](#accounts-rest-api)
    * [Security](#security)
    * [Caching](#caching)
    * [Testing](#testing)
    * [Docker](#docker)
    * [Ports](#ports)
    * [Misc...](#misc)
* [Credits](#credits)
* [License](#license)
* [Paul Chapman's Microservices Demo](#microservices-demo)

# What's here?

* This is a fork off Paul Chapman's original [Microservices Demo](https://github.com/paulc4/microservices-demo "Microservices Demo"). I've been using this extensive demo since 2016 as a microservices with Spring Boot introduction to my students. It's been a great success and an excellent demo. However, with COVID-19 and a whole lot of me time, I've felt it deserved some infra-structural refactoring and face lift upgrades.


* This application consists of a `Eureka microservices discovery server` and two collaborating and dependent microservices; The `accounts service` and the `web service`. The first consists the persistence services layer of the application, and the later, which depends on the first, consists the web/ui/presentation layer.
The web service 'talks' to the accounts service by a REST API and the accounts service talks to the persistence storage with yet another REST API. The microservices require Eureka to register themselves
and be able to obtain references to one another, in order to invoke operations (by REST API).

### Prepare the MySQL schema

* By default the accounts server uses an in memory H2 embedded database. If you choose to, you can
run this application against a live MySql server instance. The `pom.xml` property `db.vendor.name`
determines the database type. It supports `h2|mysql` and if you want to run with the later, you'll
need to swap the default value `h2` to `mysql`.


* From the root directory of the project, execute the `once-as-root.sql` script. You must have mysql
administrative user privileges such as with the mysql user `root`.

>  * user@localhost:$mysql -u root --force=true < sql/once-as-root.sql

* The script will create the mysql schema `accounts` and a user `accounts` with an empty password and
sufficient r/w privileges on all schema's objects.

## Testing the Application

* From the root directory of the project, run `mvn clean verify`. This will run all test cases and integrations tests. Tests reports are generated to the following locations:
    * Integration Tests summary `failsafe-report.html` available in `target/site`
    * Test Cases summary `surefire-report.html` available in `target/site`
    * Jacoco Code Coverage summary `index.html` available in `target/site/jacoco`


* It is also possible to run test cases from your IDE, wither individually or in packages.

## Running the Application

* The run order of this application, due to it's architecture and purpose is pre-determined.
    * run Eureka server
    * run the accounts service
    * run the web service


* Default Login credentials (defined in each module specific `xxx_server.properties` file)
    * Eureka Server dashboard: `admin/password`
    * Accounts Web login form: `client/password`
    * Web Server login form: `client/password`


* You can use your IDE to run:
    * `io.pivotal.microservices.services.registration.RegistrationServer` as a `SpringBoot Application` or a `java application`.
	* `io.pivotal.microservices.services.accounts.AccountsServer` as a `SpringBoot Application` or a `java application`.
	* `io.pivotal.microservices.services.web.WebServer` as a `SpringBoot Application` or a `java application`.


* The following web UI's become available after the running sequence:
    * Eureka server dashboard: <http://localhost:8761>
	* Accounts Server home page: <http://localhost:7070>
	* Web Application home page: <http://localhost:8080>


* You could also use Paul's `io.pivotal.microservices.services.Main` class, which is the Java entry point to the system. (JavaDoc usage documentation exist in the source code). We'll be using this entry point if we [dockerize](#docker) the application. You'll generally need to use three different command line / shell windows in order to run the three components of the system using this method.

## Changelog v2.2.0.RELEASE

* Current version (Aug 2021) v2.2.0 corresponds to Spring Boot 2.5.3 and Spring Cloud 2020.0.3 release
train - built with [OpenJdk 16.0.1](https://jdk.java.net/archive/ "OpenJdk 16.0.1").

### Persistence

* The application supports H2 an MySql databases. By default it uses an embedded in memory H2 with the h2-console
available at the accounts server host <http://localhost:7070/h2-console>. The governing mechanism to switch
databases is the `pom.xml` property `db.vendor.name`. It is filtered by maven into `filtered.properties` and
makes configuration classes read one of either `h2.properties` or `mysql.properties`. Note, for `mysql` some
preliminary actions (prepare the schem and user) must be taken. The script `sql/once-as-root.sql` takes care
of this. It can be ran with mysql root user like this: `mysql -u root -p<rootpass> --force < sql/once-as-root.sql`.


* Flyway DB had been integrated into the application to migrate the necessary tables and data for either database
vendor in what I feel is a more production ready pattern than using loose scripts in the resources directory. No
more `testdb`. We test with production grade data. If we need a separate db for testing, we can copy `h2.properties`
and `mysql.properties` to `src/test/resources` and configure the `spring.datasource.hikari.jdbc-url` to point
to a test database.

### Resources

* All `YAML` configuration files had been converted to `.properties` files. YAML relies on indentations, which are
too easy to screw up or miss on account of `tab vs. space` policy or on account of a CRLF mis-configuration
of a git push (so far like Python). However, my real issue with YAML files is the spring IDE language server features
(added in recent years to the Spring IDE) by which any maven bat-wing filtering or any loose character yields a
plethora of fake error markers that are nearly impossible to take out permanently without screwing up some other
important validation feature. It's intolerable when working with Eclipse! Hence - no more YAML files!


* No more test resources in the `src/main/resources` directory.


* The `src/main/resources` directory now looks like this:

>
```
├── accounts-server
│   ├── db
│   │   └── migration
│   │       ├── h2
│   │       │   ├── V1_1__tables.sql
│   │       │   └── V1_2__data.sql
│   │       └── mysql
│   │           ├── V1_1__tables.sql
│   │           └── V1_2__data.sql
│   └── templates
│       ├── error.html
│       ├── footer.html
│       ├── header.html
│       └── index.html
├── accounts-server.properties
├── filtered.properties
├── h2.properties
├── logback.xml
├── mysql.properties
├── registration-server.properties
├── static
│   ├── extlink.png
│   ├── favicon.ico
│   ├── logo-vmware-tanzu.png
│   ├── pws-header-logo_new.png
│   ├── spring-trans.png
│   └── styles.css
├── web-server
│   └── templates
│       ├── account.html
│       ├── accounts.html
│       ├── empty.html
│       ├── error.html
│       ├── footer.html
│       ├── header.html
│       ├── index.html
│       └── search.html
└── web-server.properties
```

* and the `src/main/java` directory now looks like this:

>
```
.
└── io
    └── pivotal
        └── microservices
            ├── accounts
            │   ├── AccountsConfig.java
            │   ├── config
            │   │   ├── AccountsRestConfig.java
            │   │   ├── AccountsSecurityConfig.java
            │   │   └── AccountsWebConfig.java
            │   ├── model
            │   │   └── Account.java
            │   └── repository
            │       └── AccountRepository.java
            ├── exception
            │   └── AccountNotFoundException.java
            ├── registration
            │   └── RegistrationSecurityConfig.java
            ├── services
            │   ├── accounts
            │   │   └── AccountsServer.java
            │   ├── Main.java
            │   ├── registration
            │   │   └── RegistrationServer.java
            │   └── web
            │       └── WebServer.java
            └── web
                ├── config
                │   ├── WebMvcConfig.java
                │   └── WebSecurityConfig.java
                ├── controller
                │   └── WebAccountsController.java
                ├── dto
                │   └── AccountRecord.java
                ├── service
                │   └── WebAccountsService.java
                ├── util
                │   └── SearchCriteria.java
                └── WebConfig.java
```

### Accounts REST API

* The `AccountsController` is gone. Spring Data Rest (`SDR`) repositories expose a REST API as is, out of the box,
with HATEOUS support (in HAL+JSON format, no less). There's no need to interfere or augment it - if it can be avoided.


* The new REST api can be discovered with `curl -v -u client:password -G http://localhost:7070/profile/accounts | jq .`.
The `findByOwnerContainingIgnoreCase` auto generated SDR repository methods is a cool feature but the naming of the
methods quickly becomes monolithic and ugly (IMHO) for use.


* As you can see, I've changed the original custom repository `findBy` style methods to `@Query` based short hand
named (`byOwner` and `byNumber`) equivalent methods, which are discoverable, and can now be observed with
`curl -v -u client:password -G http://localhost:7070/accounts/search | jq .`:

>
```
{
  "_links": {
    "byOwner": {
      "href": "http://localhost:7070/accounts/search/byOwner{?owner}",
      "templated": true
    },
    "byNumber": {
      "href": "http://localhost:7070/accounts/search/byNumber{?number}",
      "templated": true
    },
  }
}
```

* We can now access these methods methods with:

>    * curl -v -u client:password -G http://localhost:7070/accounts/search/byNumber?number=123456789 | jq .

* ...and:

>    * curl -v -u client:password -G http://localhost:7070/accounts/search/byOwner?owner=K | jq .

* The rest of the `AccountsRepository` REST API does not deviate from any `SDR CRUDRepository` API, for example:


>    * curl -v -u client:password -G http://localhost:7070/accounts | jq .
>    * curl -v -u client:password -G http://localhost:7070/accounts/1 | jq .

* ...and the IDEMPOTENT and UNSAFE methods such as POST:

>
```
curl -v -u client:password -X POST http://localhost:7070/accounts \
-H "Content-Type: application/json" \
-d '{"number": "132435467",
  "owner":"Tom Cruise",
  "balance":"1000000.00"}' | jq .
```

* ...and PUT:

>
```
curl -v -u client:password -X PUT http://localhost:7070/accounts/22 \
-H "Content-Type: application/json" \
-d '{"number": "132435467",
  "owner":"Tom Silverman",
  "balance":"8.75"}' | jq .
```

* ...or PATCH:

>
```
curl -v -u client:password -X PATCH http://localhost:7070/accounts/22 \
-H "Content-Type: application/json" \
-d '{"number": "132435467",
  "owner":"Tom Silverman",
  "balance":"12.45"}' | jq .
```

### Configuration Classes

* I've added 3 different accounts module configuration classes for security, rest and web - all governed by the
central module configuration class `AccountsConfig` in the root of the accounts package (with a convenient
component scan location). This separates configuration concerns for the accounts module, but keeps it centralized
via the main configuration class for testing purposes and for configuring the main accounts server Spring Boot
application.


* Initially, all I wanted to do was provide security constraints for the idempotent and unsafe REST verbs
such as `PATCH` and `DELETE`. Pretty soon it became clear that in this architecture, where 3 applications are
in the same project and share the same classpath, once spring security is on the classpath (with even a single
`spring.security...` property defined in the properties resource, all hell breaks loose with `csrf` protection
and the entire aspect of security needs some form of management. Hence, the accounts module `SecurityConfig`
class came to existence.


* This was followed by some minor REST configurations (I wanted to expose the entities id
property on update and creation) and the `RestConfg` class was born with some configurations that are not possible
to achieve with property configuration files alone.


* Finally, I've taken out the redundant `HomeController` class and replaced
it in the `AccountsWebConfig` class with an automatic view controller (and an appropriate redirect view).


* Other specializing configuration classes `AccountsRestConfig`, `AccountsSecurityConfig` and `AccountsWebConfig` had been
added to the accounts module package `io.pivotal.microservices.accounts.config`. The registration module reads these
configurations by components scanning of the global module configuration class `AccountsConfig`.


* The account module looks like this:

>
```
.
├── AccountsConfig.java
├── config
│   ├── AccountsRestConfig.java
│   ├── AccountsSecurityConfig.java
│   └── AccountsWebConfig.java
├── model
│   └── Account.java
└── repository
    └── AccountRepository.java
```

* Configuration classes had been added to the registration and web modules too. The registration has a
new `RegistrationSecurityConfig` class and the web module has it's own `WebConfig` class, which by component
scanning discovers the `WebMvcConfig` and `WebSecurityConfig` configuration classes in
the `io.pivotal.microservices.web.config` package. The web module looks like this:

>
```
.
├── config
│   ├── WebMvcConfig.java
│   └── WebSecurityConfig.java
├── controller
│   └── WebAccountsController.java
├── dto
│   └── AccountRecord.java
├── service
│   └── WebAccountsService.java
├── util
│   └── SearchCriteria.java
└── WebConfig.java
```

### Security

* Security in the application: Euroka server is now governed by spring security. Two users (`admin/password`
and `clinet/password`) had been added to the system with corresponding `ADMIN` and `CLIENT` authorities.


* The `Eureka Dashboard` can only be accessed by `admin/password`. `Eureka discovery` is only possible for
authorized clients (with `client/password` credentials and `CLIENT` authority). The security confiurations
for the accounts and registration module are responsible for enforcing this policy along with changes to the
registration url's in both `accounts-server.properties` and `web-server.properties`.


* The accounts server client
service url, for example, is now defined as: `eureka.client.service-url.defaultZone=${EUREKA_URL:http://client:password@localhost:8761/eureka}`


* For this to work, basic authentication needs to be added to security filter chain and the registration module's
`SecurityConfig` class takes care of that.

### Caching

* Caching in the application: I've added caching to the web layer through a `com.github.ben-manes.caffeine:caffeine`
maven dependency and standard Spring `@Cacheable` annotations in the web module service layer in
`io.pivotal.microservices.web.service.WebAccountsService`.


* My policy is let RDBMS's do what RDBMS's do best - so when our service layer only consists of a `CrudRepository`,
such as with the `accounts` module, there's really no point in adding a second layer of caching on top of `mysql's`.
In the web layer however, especially with potentially many instances of microservices communicating with each other
over the network, we'd really like to keep the chatter to a minimum and avoid unnecessary calls from the web server
to the accounts server microservice.


* Hence... caching in the web layer. The only minor issues are periodically evicting the cache and
avoiding caching null values. Evicting the cache (the whole of it or just specific keys) is pretty easy and I've
implemented the methods in `WebAccountsService` and provided two demo REST endoints in `WebAccountsController`.
I've also linked to it in the home page.


* An important note regarding null values... The order of running is Eureka server, then accounts server and finally
the web server. It is very possible for the web server not to obtain a reference to the accounts server client instance
by the time it loads it's fully functional UI. In this case when we search for an account, either by owner or by
account number, we'll get a null result. This will literally fix it self within a few seconds when the web server
microservice obtains the accounts server client instance from Eureka discovery server, but if we cache the initial
null result - we'll be stuck wit it forever. Hence... do not cache null values!

### Testing

* The contents of the src/test directory now looks like this:

>
```
.
├── java
│   └── io
│       └── pivotal
│           └── microservices
│               ├── accounts
│               │   ├── config
│               │   │   └── SanityTest.java
│               │   ├── model
│               │   │   └── AccountTest.java
│               │   └── repository
│               │       ├── AccountRepositoryIT.java
│               │       └── RestApiIT.java
│               ├── config
│               │   └── TestConfig.java
│               ├── exception
│               │   └── AccountExceptionTest.java
│               ├── services
│               │   └── registration
│               │       └── UrlMaskTest.java
│               └── web
│                   ├── config
│                   │   └── SanityTest.java
│                   ├── controller
│                   │   └── WebAccountsControllerTest.java
│                   ├── dto
│                   │   └── AccountRecordTest.java
│                   ├── service
│                   │   └── WebAccountsServiceTest.java
│                   └── util
│                       └── SearchCriteriaTest.java
└── resources
    └── logback-test.xml
```

* It is not easy to test such extensive functionality! In fact, I've spent more time on completing the tests
than on refactoring the entire demo in a 3 to 1 ratio. I had to resort to a mixture of integration tests (`IT` suffix)
and mock tests (`Test` suffix) to capture what can be referred to as minimal acceptance testing. Even so, as the
Jacoco maven coverage plugin will testify, I've not covered the half of it. I'll complete this in the future.


* `SanityTest` just loads the context. Apparently a redundant test but in essence, it captures the context loading
test configurations, which for the accounts module are:

>
```
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AccountsConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
public class SanityTest {
	...
}
```

* This is where the refactoring of the configuration classes and the separatin of concerns pays off. In our
tests, we don't care about specific configurations of security or rest in teh accounts module. All we need
is the global configurations class `AccountsConfig`. Note how test properties are passed to the context. There's
no need for any test configurations files. In fact, the test/resources directory now looks like this:

>
```
.
└── logback-test.xml
```

* The `AccountRepositoryIT` integration test receives a reference to the `AccountRepository` implementation and
invokes it's methods. This is as real as it gets. Live database records are being CRUD'ed. Hence 'Integration Test`.
I suppose I could have mocked it and renamed it `AccountRepositoryTEST` but then it's a lot harder and I don't
get no extra credit with `Jacoco coverage` for the work, so I've left it at that. The configurations for this test
are very similar to the sanity test's except, it's @Trasactional, so we don't actually `decrapetate` any existing
database records.

>
```
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AccountsConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
@Transactional
class AccountRepositoryIT {
	...
}
```

* The `RestApiIT` integration test is a MockMvc test for testing the new REST API of the accounts module. The
entire API stems from the `AccountRepository` (since I've taken out the AccountsController) and this is where
I test the API's CRUD and custom functionality. Don't let the 'Mock' in `MockMvc` fool you. This isn't a mock test.
MockMvc just mocks the servlet container (Tomcat) but the rest of it is very real. Hence, another integration test.


* This test also uses the `io.pivotal.microservices.config.TestConfig` configuration class, which defines an `org.apache.http`  client based `TestRestTemplate`. This is done to avoid the I/O error on PATCH requests where the exception involves a message saying `Invalid HTTP method: PATCH; nested exception is java.net.ProtocolException`. This happens because of the HttpURLConnection used by default in Spring Boot RestTemplate, that is provided by the
standard JDK HTTP library. We just replace it with an implementation from apache.

>
```
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { TestConfig.class, AccountsConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
@Transactional
@DirtiesContext
class RestApiIT {
	...
}
```

* There's a web layer `SanityTest` as well. It's pretty much the same as for the accounts module but now,
since the web layer depends on the accounts module (just as the web server microservice depends on the
accounts server microservice) - the configurations differ by a bit:

>
```
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TestConfig.class, AccountsConfig.class, WebConfig.class })
@TestPropertySource(properties = { "eureka.client.enabled=false" })
@ActiveProfiles({ "test" })
public class SanityTest {
	...
}
```

* I have mocked a test specifically for the `WebAccountsService` and it is nothing irregular:

>
```
@ExtendWith(SpringExtension.class)
@WebMvcTest(properties = { "eureka.client.enabled=false" })
@ContextConfiguration(classes = { WebConfig.class })
@AutoConfigureWebClient // in charge of preparing RestTemplateBuilder
@ActiveProfiles({ "test" })
public class WebAccountsServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(WebAccountsServiceTest.class);
	@MockBean WebAccountsService webAccountsService;
	...
}
```

* The `WebAccountsControllerTest` is where things get interesting. Notice it is unaware of the accounts module. It is
a mock unit test case. The essence of the configurations are:

>
```
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = { WebAccountsController.class }, properties = { "eureka.client.enabled=false" })
@ContextConfiguration(classes = { WebConfig.class })
@AutoConfigureWebClient
@ActiveProfiles({ "test" })
@WithMockUser(username = "client", password = "password", authorities = { "CLIENT" })
class WebAccountsControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(WebAccountsControllerTest.class);
	private static final String restApiEndpoint = "/accounts";
	@Autowired MockMvc mockMvc;
	@MockBean WebAccountsService webAccountsService;
	@Autowired ObjectMapper mapper;
	...
}
```

* This test case issues `MockMvc` http method calls to the `WebAccountsController` in each test and compares the responses
to the mocked expected results. This is not an integration test and there's no real live data involved, not even a
repository (not to mention a database).


* This test case was rather tough to configure and sort of difficult to code at first, mainly due to the mixture of
`MockMvcRequestBuilders`, `MockMvcResultMatchers` and `MockMvcResultHandlers` myriad of methods used. However,
once those imports are sorted out, and a template of the first test is ready - the rest of it becomes much
easier to complete. The last tests are literally a copy & paste job.


* Note the existence of other test cases (manly simple domain objects tests) to satisfy the Jacoco maven plugin
code coverage requirements (mine actually). I have barely covered 50% - but this is mainly due to Jacoco not
registering any Mockito mock objects. I could have used a @SpyBean but then my tests would become integration tests
as methods would be invoked on real references (not to mention the complexity of the services dependencies on one
another).

### Docker

* This application uses google's [jib-maven-plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#readme "jib-maven-plugin") for containerizing the application.


* The **new documentation** is in [DOCKERIZE.md](./docker/DOCKERIZE.md "DOCKERIZE.md").


* I've revised and modified the entire process of `dockerizing` and running the application. Paul's old `Dockerfile`
is no longer required, nor is any deep mastery of what it does or how to write it. It's still in the `docker` directory
for reference, but is no longer in any applicative use.


* A new image of the modified application exists on my [DockerHub](https://registry.hub.docker.com/repository/docker/tnsilver/microservices-demo "DockerHub") repository.

### Ports

* I've chosen to revise the original port choices made by Paul and revert them to a more standardized scheme:
    * Eureka Server runs on the default port `8761`
    * Accounts Server runs on port `7070`
    * Web Server runs on the default development server port `8080`

### Misc

* I've fixed the `Account` class `deposit` and `withdraw` methods - as `BigDecimal` is an immutable object and
we cannot expect balance modifications to take place by calling methods on the original reference.


* The DTO pattern is IMO a little redundant since the serializable JPA entities came into existence. I feel DTO's
are a redundant layer of wrappers around entities and they need to be maintained and managed whenever the
corresponding entity evolves through development.


* However... I agree with Paul's responses in one of the blog's comments about keeping microservices 'micro' and
avoiding transforming them into monolithic applications with multiple jar dependencies. Hence, instead of packing
the `Account` entity in a common jar and packaging it with the web module, I've kept the `Account` DTO.


* I did, however, transformed it into a JDK-16 record and renamed it `io.pivotal.microservices.web.dto.AccountRecord`.


* Immutable Java 16 records are especially appropriate for utilization in the DTO pattern so if I'm stuck with DTO's
- why not use Java records...


* `SearchCriteria`... When we don't override the inherited `equals` and `hashcode` methods Java is going to have a
very hard time comparing one instance to another... Adding `equals` and `hashcode` to `SearchCriteria` resolved many
of the (new) unit tests failures.

## Credits

* Special thanks to [Paul Chapman](https://github.com/paulc4 "Paul Chapman")


* and... [Tom Silverman](https://github.com/tnsilver "Tom Silverman")

## License

[Licensed under the Apache License, Version 2.0](LICENSE.md "Apache License")

# microservices-demo

Demo application to go with (Paul's) [Microservices Blog](https://spring.io/blog/2015/07/14/microservices-with-spring) on the spring.io website.  **WARNING:** Only maven build has been updated.  Gradle build still to be done.

![Demo System Schematic](https://github.com/paulc4/microservices-demo/blob/master/mini-system.jpg)

Clone it and either load into your favorite IDE or use maven/gradle directly.

_Note for gradle users:_ to make the intructions below build-tool independent, the gradle build copies its artifacts from `build/libs` to `target`.

## Versions

Current version (Jan 2021) v2.1.1 corresponds to Spring Boot 2.4.2 and Spring Cloud 2020.0.0 release train.

Previous versions have been tagged and can be accessed using the `Branch` button above or using `git checkout <version>` - for example `git checkout v1.2.0`.

Tagged versions are:

* v2.1.1 - Spring Boot 2.4.2, Spring Cloud release-train 2020.0.0 and overdue update to Bootstrap 4 (Jan 2021)
* v2.1.0 - Spring Boot 2.4.2 and Spring Cloud release-train 2020.0.0 (Jan 2021)
* v2.0.0 - Spring Boot 2.0 and Spring Cloud release-train Finchley (Feb 2020)
* v1.2.0 corresponds to Spring Boot 1.5 and Spring Cloud release-train Edgeware (Apr 2018)
* v1.1.0 corresponds to Spring Cloud release-train Brixton (Jan 2018)
* v1.0.0 corresponds to Spring Cloud release-train Angel.SR6 (May 2016)

If running with Java 11 or later, you need to upgrade the build to include additional dependencies. Refer to https://github.com/paulc4/microservices-demo/issues/32 for details.

## Using an IDE

You can run the system in your IDE by running the three server classes in order: _RegistrationService_, _AccountsService_ and _WebService_.  Each is a Spring Boot application using embedded Tomcat.  If using Spring Tools use `Run As ... Spring Boot App` otherwise just run each as a Java application - each has a static `main()` entry point.

As discussed in the Blog, open the Eureka dashboard [http://localhost:1111](http://localhost:1111) in your browser to see that the `ACCOUNTS-SERVICE` and `WEB-SERVICE` applications have registered.  Next open the Demo Home Page [http://localhost:3333](http://localhost:3333) in and click one of the demo links.

The `localhost:3333` web-site is being handled by a Spring MVC Controller in the _WebService_ application, but you should also see logging output from _AccountsService_ showing requests for Account data.

## Command Line

You may find it easier to view the different applications by running them from a command line since you can place the three windows side-by-side and watch their log output

For convenience we are building a 'fat' executble jar whose start-class (main method entry-point) is defined to be in the class `io.pivotal.microservices.services.Main`.  This application expects a single command-line argument that tells it to run as any of our three servers.

```
java -jar target/microservices-demo-2.0.0.RELEASE.jar registration|accounts|web
```

### Procedure

To run the microservices system from the command-line, open three CMD windows (Windows) or three Terminal windows (MacOS, Linux) and arrange so you can view them conveniently.

 1. In each window, change to the directory where you cloned the demo.
 1. In the first window, build the application using either `./mvnw clean package` or `./gradlew clean assemble`.  Either way the generated file will be `target/microservices-demo-2.0.0.RELEASE.jar` (even if you used gradle).
 1. In the same window run: `java -jar target/microservices-demo-2.0.0.RELEASE.jar registration`
 1. Switch to the second window and run: `java -jar target/microservices-demo-2.0.0.RELEASE.jar accounts`
 1. In the third window run: `java -jar target/microservices-demo-2.0.0.RELEASE.jar web`
 1. In your favorite browser open the same two links: [http://localhost:1111](http://localhost:1111) and [http://localhost:3333](http://localhost:3333)

You should see servers being registered in the log output of the first (registration) window.
As you interact wiht the Web application, you should logging in the both the second and third windows.

For a list of valid accounts refer to the [data.sql](https://github.com/paulc4/microservices-demo/blob/master/src/main/resources/testdb/data.sql) that is used by the Account Service to set them up.

 1. In a new window, run up a second account-server using HTTP port 2223:
     * `java -jar target/microservices-demo-2.0.0.RELEASE.jar accounts 2223`
 1. Allow it to register itself
 1. Kill the first account-server and see the web-server switch to using the new account-server - no loss of service.

## Using Docker

This application can also be run using 3 docker containers. See [here](use-docker.md).

