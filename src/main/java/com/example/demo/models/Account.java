package com.example.demo.models;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

// One customer has two accounts: CHECKING and SAVINGS
@Document(collection = "accounts")
public class Account {

    @Id
    private String id;               // also used as the account number

    private String customerId;       // link to the owning Customer

    private AccountType type;

    @Field(targetType = FieldType.DECIMAL128)   // store money as an exact decimal, not a double
    private BigDecimal balance = BigDecimal.ZERO;

    public Account() {
    }

    public Account(String customerId, AccountType type, BigDecimal balance) {
        this.customerId = customerId;
        this.type = type;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
