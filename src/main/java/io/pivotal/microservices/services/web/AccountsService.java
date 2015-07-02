package io.pivotal.microservices.services.web;

import io.pivotal.microservices.accounts.Account;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Hide the access to the microservice inside this local service.
 * 
 * @author Paul Chapman
 */
@Service
public class AccountsService {

	@Autowired
	protected RestTemplate restTemplate;

	protected String serviceUrl;

	public AccountsService(String serviceUrl) {
		this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl
				: "http://" + serviceUrl;
	}

	public Account findByNumber(String accountNumber) {

		Logger.getGlobal().info("findByNumber() invoked: for " + accountNumber);

		return restTemplate.getForObject(serviceUrl + "/acounts/{number}",
				Account.class, accountNumber);
	}

	public List<Account> byOwnerContains(String name) {
		Logger.getGlobal().info("byOwnerContains() invoked:  for " + name);

		Account[] accounts = restTemplate.getForObject(serviceUrl
				+ "/accounts/owner/{name}", Account[].class, name);

		if (accounts == null || accounts.length == 0)
			return null;
		else
			return Arrays.asList(accounts);
	}
}
