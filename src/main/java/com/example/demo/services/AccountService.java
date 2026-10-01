package com.example.demo.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.Account;
import com.example.demo.models.AccountType;
import com.example.demo.models.Transaction;
import com.example.demo.models.TransactionType;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.TransactionRepository;

@Service
public class AccountService {
   private AccountRepository accountRepository;
   private TransactionRepository transactionRepository;

   public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
       this.accountRepository = accountRepository;
       this.transactionRepository = transactionRepository;
   }

   // Every new customer gets a Checking and a Savings account
   public List<Account> createDefaultAccounts(String customerId, BigDecimal checking, BigDecimal savings) {
       return accountRepository.saveAll(List.of(
               new Account(customerId, AccountType.CHECKING, checking),
               new Account(customerId, AccountType.SAVINGS, savings)));
   }

   public List<Account> getAccounts(String customerId) {
       return accountRepository.findByCustomerId(customerId);
   }

   public BigDecimal totalBalance(List<Account> accounts) {
       return accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
   }

   public List<Transaction> getRecentTransactions(String customerId) {
       return transactionRepository.findTop20ByCustomerIdOrderByTimestampDesc(customerId);
   }

   public Account deposit(String customerId, String accountId, BigDecimal amount) {
       checkAmount(amount);
       Account account = getOwnAccount(customerId, accountId);

       account.setBalance(account.getBalance().add(amount));
       accountRepository.save(account);
       transactionRepository.save(new Transaction(customerId, accountId, null,
               TransactionType.DEPOSIT, amount, account.getBalance()));
       return account;
   }

   public Account withdraw(String customerId, String accountId, BigDecimal amount) {
       checkAmount(amount);
       Account account = getOwnAccount(customerId, accountId);
       if (account.getBalance().compareTo(amount) < 0) {
           throw new IllegalArgumentException("Insufficient funds. Available: " + account.getBalance());
       }

       account.setBalance(account.getBalance().subtract(amount));
       accountRepository.save(account);
       transactionRepository.save(new Transaction(customerId, accountId, null,
               TransactionType.WITHDRAW, amount, account.getBalance()));
       return account;
   }

   // @Transactional: both balance updates succeed together, or neither happens
   @Transactional
   public Account transfer(String customerId, String fromAccountId, String toAccountId, BigDecimal amount) {
       checkAmount(amount);
       if (fromAccountId == null || fromAccountId.equals(toAccountId)) {
           throw new IllegalArgumentException("Choose two different accounts");
       }
       Account from = getOwnAccount(customerId, fromAccountId);
       Account to = accountRepository.findById(toAccountId)
               .orElseThrow(() -> new NoSuchElementException("Destination account not found"));

       if (from.getBalance().compareTo(amount) < 0) {
           throw new IllegalArgumentException("Insufficient funds. Available: " + from.getBalance());
       }

       from.setBalance(from.getBalance().subtract(amount));
       to.setBalance(to.getBalance().add(amount));
       accountRepository.save(from);
       accountRepository.save(to);

       transactionRepository.save(new Transaction(customerId, from.getId(), to.getId(),
               TransactionType.TRANSFER_OUT, amount, from.getBalance()));
       transactionRepository.save(new Transaction(to.getCustomerId(), to.getId(), from.getId(),
               TransactionType.TRANSFER_IN, amount, to.getBalance()));
       return from;
   }

   // A customer may only use their OWN account as the source
   private Account getOwnAccount(String customerId, String accountId) {
       Account account = accountRepository.findById(accountId == null ? "" : accountId)
               .orElseThrow(() -> new NoSuchElementException("Account not found"));
       if (!account.getCustomerId().equals(customerId)) {
           throw new IllegalArgumentException("This account does not belong to you");
       }
       return account;
   }

   private void checkAmount(BigDecimal amount) {
       if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
           throw new IllegalArgumentException("Amount must be greater than 0");
       }
   }
}
