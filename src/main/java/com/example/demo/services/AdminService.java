package com.example.demo.services;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.models.CustomerView;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.TransactionRepository;

@Service
public class AdminService {
   private CustomerService customerService;
   private AccountRepository accountRepository;
   private TransactionRepository transactionRepository;

   public AdminService(CustomerService customerService, AccountRepository accountRepository,
                       TransactionRepository transactionRepository) {
       this.customerService = customerService;
       this.accountRepository = accountRepository;
       this.transactionRepository = transactionRepository;
   }

   // Summary numbers for the Admin Dashboard
   public Map<String, Object> getDashboard() {
       List<CustomerView> customers = customerService.getAllCustomers();
       BigDecimal totalDeposits = customers.stream()
               .map(CustomerView::getTotalBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

       Map<String, Object> dashboard = new LinkedHashMap<>();
       dashboard.put("totalCustomers", customers.size());
       dashboard.put("premiumCustomers", customers.stream().filter(CustomerView::isPremium).count());
       dashboard.put("totalAccounts", accountRepository.count());
       dashboard.put("totalDeposits", totalDeposits);
       dashboard.put("recentTransactions", transactionRepository.findTop10ByOrderByTimestampDesc());
       return dashboard;
   }
}
