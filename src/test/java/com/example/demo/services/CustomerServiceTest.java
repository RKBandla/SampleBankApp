package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.models.Account;
import com.example.demo.models.AccountType;
import com.example.demo.models.Customer;
import com.example.demo.models.CustomerView;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.CustomerRepository;
import com.example.demo.repos.TransactionRepository;
import com.example.demo.repos.UserRepository;

class CustomerServiceTest {

	private CustomerRepository customerRepository;
	private AccountRepository accountRepository;
	private TransactionRepository transactionRepository;
	private UserRepository userRepository;
	private CustomerService customerService;

	private final Customer rich = new Customer("c1", "Robert", "Brown", "robert@example.com");
	private final Customer regular = new Customer("c2", "Priya", "Sharma", "priya@example.com");

	@BeforeEach
	void setUp() {
		customerRepository = mock(CustomerRepository.class);
		accountRepository = mock(AccountRepository.class);
		transactionRepository = mock(TransactionRepository.class);
		userRepository = mock(UserRepository.class);
		AccountService accountService = new AccountService(accountRepository, transactionRepository);
		customerService = new CustomerService(customerRepository, accountService,
				accountRepository, transactionRepository, userRepository);

		when(customerRepository.findAll()).thenReturn(List.of(rich, regular));
		when(accountRepository.findByCustomerId("c1")).thenReturn(List.of(
				new Account("c1", AccountType.CHECKING, new BigDecimal("2500")),
				new Account("c1", AccountType.SAVINGS, new BigDecimal("7500"))));     // total 10,000 → Premium
		when(accountRepository.findByCustomerId("c2")).thenReturn(List.of(
				new Account("c2", AccountType.CHECKING, new BigDecimal("1200")),
				new Account("c2", AccountType.SAVINGS, new BigDecimal("3400"))));     // total 4,600
	}

	@Test
	void totalBalanceAddsAllAccounts() {
		List<CustomerView> all = customerService.getAllCustomers();

		assertEquals(new BigDecimal("10000"), all.get(0).getTotalBalance());
		assertEquals(new BigDecimal("4600"), all.get(1).getTotalBalance());
	}

	@Test
	void premiumMeansTotalBalanceOfAtLeastTenThousand() {
		List<CustomerView> premium = customerService.getPremiumCustomers();

		assertEquals(1, premium.size());
		assertEquals("Robert", premium.get(0).getFirstName());
		assertTrue(premium.get(0).isPremium());
	}

	@Test
	void searchByFirstNameUsesTheRepositoryQuery() {
		when(customerRepository.findByFirstNameContainingIgnoreCase("ro")).thenReturn(List.of(rich));

		List<CustomerView> found = customerService.findByFirstName("ro");

		assertEquals(1, found.size());
		assertEquals("Robert", found.get(0).getFirstName());
	}

	@Test
	void createCustomerRequiresFirstName() {
		assertThrows(IllegalArgumentException.class,
				() -> customerService.createCustomer(new Customer(null, " ", "X", null)));
	}

	@Test
	void deleteCustomerAlsoDeletesAccountsHistoryAndLogin() {
		when(customerRepository.existsById("c2")).thenReturn(true);

		assertTrue(customerService.deleteCustomer("c2"));
		verify(accountRepository).deleteByCustomerId("c2");
		verify(transactionRepository).deleteByCustomerId("c2");
		verify(userRepository).deleteByCustomerId("c2");
		verify(customerRepository).deleteById("c2");
	}

	@Test
	void deleteUnknownCustomerReturnsFalse() {
		when(customerRepository.existsById("nope")).thenReturn(false);

		assertFalse(customerService.deleteCustomer("nope"));
	}
}
