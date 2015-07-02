package io.pivotal.microservices.accounts;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface AccountRepository extends Repository<Account, Long> {
	public Account findByNumber(String accountNumber);
	public List<Account> findByOwnerContaining(String accountName);
	
	@Query("SELECT count(*) from Account")
	public int countAccounts();
}
