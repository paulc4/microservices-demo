package io.pivotal.microservices.accounts;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistent account entity with JPA markup. Accounts are stored in an H2
 * relational database.
 * 
 * @author Paul Chapman
 */
@Entity
@Table(name = "T_ACCOUNT")
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Long nextId = 0L;

	@Id
	protected Long id;

	protected String number;

	@Column(name = "name")
	protected String owner;

	protected BigDecimal balance;

	/**
	 * This is a very simple, and non-scalable solution to generating unique
	 * ids. Not recommended for a real application. Consider using the
	 * <tt>@GeneratedValue</tt> annotation and a sequence to generate ids.
	 * 
	 * @return The next available id.
	 */
	protected static Long getNextId() {
		synchronized (nextId) {
			return nextId++;
		}
	}

	/**
	 * Default constructor for JPA only.
	 */
	protected Account() {
		balance = BigDecimal.ZERO;
	}

	public Account(String number, String owner) {
		id = getNextId();
		this.number = number;
		this.owner = owner;
		balance = BigDecimal.ZERO;
	}

	public long getId() {
		return id;
	}

	/**
	 * Set JPA id - for testing and JPA only. Not intended for normal use.
	 * 
	 * @param id
	 *            The new id.
	 */
	protected void setId(long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	protected void setNumber(String accountNumber) {
		this.number = accountNumber;
	}

	public String getOwner() {
		return owner;
	}

	protected void setOwner(String owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance.setScale(2, RoundingMode.HALF_EVEN);
	}

	public void withdraw(BigDecimal amount) {
		balance.subtract(amount);
	}

	public void deposit(BigDecimal amount) {
		balance.add(amount);
	}

	@Override
	public String toString() {
		return number + " [" + owner + "]: $" + balance;
	}

}
