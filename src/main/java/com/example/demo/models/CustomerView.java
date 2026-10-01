package com.example.demo.models;

import java.math.BigDecimal;
import java.util.List;

// What the API returns for a customer: the customer + their accounts + totals.
// (A "view" object, built by CustomerService, not stored in the database.)
public class CustomerView {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts;
    private BigDecimal totalBalance;
    private boolean premium;

    public CustomerView(Customer customer, List<Account> accounts, BigDecimal totalBalance, boolean premium) {
        this.id = customer.getId();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.email = customer.getEmail();
        this.accounts = accounts;
        this.totalBalance = totalBalance;
        this.premium = premium;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public boolean isPremium() {
        return premium;
    }
}
