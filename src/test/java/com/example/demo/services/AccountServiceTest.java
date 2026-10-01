package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.models.Account;
import com.example.demo.models.AccountType;
import com.example.demo.models.Transaction;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.TransactionRepository;

// Unit tests: the repositories are MOCKS (fake objects), so no database is needed.
class AccountServiceTest {

	private AccountRepository accountRepository;
	private TransactionRepository transactionRepository;
	private AccountService accountService;

	private Account checking;
	private Account savings;
	private Account otherCustomersAccount;

	@BeforeEach
	void setUp() {
		accountRepository = mock(AccountRepository.class);
		transactionRepository = mock(TransactionRepository.class);
		accountService = new AccountService(accountRepository, transactionRepository);

		checking = account("chk", "rohan", AccountType.CHECKING, "1000");
		savings = account("sav", "rohan", AccountType.SAVINGS, "500");
		otherCustomersAccount = account("other", "priya", AccountType.CHECKING, "50");

		when(accountRepository.findById("chk")).thenReturn(Optional.of(checking));
		when(accountRepository.findById("sav")).thenReturn(Optional.of(savings));
		when(accountRepository.findById("other")).thenReturn(Optional.of(otherCustomersAccount));
		when(accountRepository.findById("missing")).thenReturn(Optional.empty());
	}

	@Test
	void depositAddsMoneyAndRecordsTransaction() {
		accountService.deposit("rohan", "chk", new BigDecimal("250.50"));

		assertEquals(new BigDecimal("1250.50"), checking.getBalance());
		verify(accountRepository).save(checking);
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	void withdrawSubtractsMoney() {
		accountService.withdraw("rohan", "chk", new BigDecimal("400"));

		assertEquals(new BigDecimal("600"), checking.getBalance());
	}

	@Test
	void withdrawMoreThanBalanceIsRejected() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> accountService.withdraw("rohan", "chk", new BigDecimal("5000")));

		assertTrue(e.getMessage().contains("Insufficient funds"));
		assertEquals(new BigDecimal("1000"), checking.getBalance());   // unchanged
		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	void transferMovesMoneyBetweenAccounts() {
		accountService.transfer("rohan", "chk", "sav", new BigDecimal("300"));

		assertEquals(new BigDecimal("700"), checking.getBalance());
		assertEquals(new BigDecimal("800"), savings.getBalance());
		verify(transactionRepository, times(2)).save(any(Transaction.class));   // OUT + IN
	}

	@Test
	void transferToAnotherCustomerWorks() {
		accountService.transfer("rohan", "chk", "other", new BigDecimal("100"));

		assertEquals(new BigDecimal("150"), otherCustomersAccount.getBalance());
	}

	@Test
	void cannotSpendFromSomeoneElsesAccount() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> accountService.transfer("rohan", "other", "chk", new BigDecimal("10")));

		assertTrue(e.getMessage().contains("does not belong to you"));
	}

	@Test
	void transferWithInsufficientFundsChangesNothing() {
		assertThrows(IllegalArgumentException.class,
				() -> accountService.transfer("rohan", "sav", "chk", new BigDecimal("501")));

		assertEquals(new BigDecimal("500"), savings.getBalance());
		assertEquals(new BigDecimal("1000"), checking.getBalance());
	}

	@Test
	void transferToSameAccountIsRejected() {
		assertThrows(IllegalArgumentException.class,
				() -> accountService.transfer("rohan", "chk", "chk", new BigDecimal("1")));
	}

	@Test
	void transferToUnknownAccountIsNotFound() {
		assertThrows(NoSuchElementException.class,
				() -> accountService.transfer("rohan", "chk", "missing", new BigDecimal("1")));
	}

	@Test
	void zeroOrNegativeAmountsAreRejected() {
		assertThrows(IllegalArgumentException.class, () -> accountService.deposit("rohan", "chk", BigDecimal.ZERO));
		assertThrows(IllegalArgumentException.class, () -> accountService.withdraw("rohan", "chk", new BigDecimal("-5")));
	}

	private static Account account(String id, String customerId, AccountType type, String balance) {
		Account a = new Account(customerId, type, new BigDecimal(balance));
		a.setId(id);
		return a;
	}
}
