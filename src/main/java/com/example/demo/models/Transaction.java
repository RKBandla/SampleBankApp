package com.example.demo.models;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

// A history entry: deposit, money in, or money out
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    private String customerId;

    private String accountId;

    private String otherAccountId;   // the other side of a transfer (null for deposits)

    private TransactionType type;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal balanceAfter;

    private Instant timestamp = Instant.now();

    public Transaction() {
    }

    public Transaction(String customerId, String accountId, String otherAccountId,
                       TransactionType type, BigDecimal amount, BigDecimal balanceAfter) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.otherAccountId = otherAccountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOtherAccountId() {
        return otherAccountId;
    }

    public void setOtherAccountId(String otherAccountId) {
        this.otherAccountId = otherAccountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
