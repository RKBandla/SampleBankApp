package com.example.demo.services;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Service;

import com.example.demo.models.Account;
import com.example.demo.models.Customer;
import com.example.demo.models.CustomerView;
import com.example.demo.repos.CustomerRepository;
import com.example.demo.repos.TransactionRepository;
import com.example.demo.repos.UserRepository;
import com.example.demo.repos.AccountRepository;

@Service
public class CustomerService {

   // A customer is "Premium" when all their accounts together hold at least this much
   public static final BigDecimal PREMIUM_THRESHOLD = new BigDecimal("10000");

   private CustomerRepository customerRepository;
   private AccountService accountService;
   private AccountRepository accountRepository;
   private TransactionRepository transactionRepository;
   private UserRepository userRepository;

   public CustomerService(CustomerRepository customerRepository, AccountService accountService,
                          AccountRepository accountRepository, TransactionRepository transactionRepository,
                          UserRepository userRepository) {
	   this.customerRepository = customerRepository;
	   this.accountService = accountService;
	   this.accountRepository = accountRepository;
	   this.transactionRepository = transactionRepository;
	   this.userRepository = userRepository;
   }

   public List<CustomerView> getAllCustomers() {
       return toViews(customerRepository.findAll());
   }

   public CustomerView getCustomerById(String id) {
       Customer customer = customerRepository.findById(id).orElse(null);
       return customer == null ? null : toView(customer);
   }

   // Search: GET /api/customers/search?firstName=ro
   public List<CustomerView> findByFirstName(String firstName) {
       return toViews(customerRepository.findByFirstNameContainingIgnoreCase(firstName == null ? "" : firstName));
   }

   // Filter: GET /api/customers/premium
   public List<CustomerView> getPremiumCustomers() {
       return getAllCustomers().stream().filter(CustomerView::isPremium).toList();
   }

   // Creates the customer AND their Checking + Savings accounts
   public CustomerView createCustomer(Customer customer) {
       requireName(customer);
       customer.setId(null);
       Customer saved = customerRepository.save(customer);
       accountService.createDefaultAccounts(saved.getId(), BigDecimal.ZERO, BigDecimal.ZERO);
       return toView(saved);
   }

   public CustomerView updateCustomer(String id, Customer customer) {
       Customer existing = customerRepository.findById(id).orElse(null);
       if (existing == null) {
           return null;
       }
       requireName(customer);
       existing.setFirstName(customer.getFirstName());
       existing.setLastName(customer.getLastName());
       existing.setEmail(customer.getEmail());
       return toView(customerRepository.save(existing));
   }

   // Deleting a customer also removes their accounts, history and login
   public boolean deleteCustomer(String id) {
       if (!customerRepository.existsById(id)) {
           return false;
       }
       accountRepository.deleteByCustomerId(id);
       transactionRepository.deleteByCustomerId(id);
       userRepository.deleteByCustomerId(id);
       customerRepository.deleteById(id);
       return true;
   }

   public CustomerView toView(Customer customer) {
       List<Account> accounts = accountService.getAccounts(customer.getId());
       BigDecimal total = accountService.totalBalance(accounts);
       return new CustomerView(customer, accounts, total, total.compareTo(PREMIUM_THRESHOLD) >= 0);
   }

   private List<CustomerView> toViews(List<Customer> customers) {
       return customers.stream().map(this::toView).toList();
   }

   private void requireName(Customer customer) {
       if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
           throw new IllegalArgumentException("firstName is required");
       }
   }
}
