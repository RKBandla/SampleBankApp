package com.example.demo.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

	List<Transaction> findTop20ByCustomerIdOrderByTimestampDesc(String customerId);

	List<Transaction> findTop10ByOrderByTimestampDesc();

	void deleteByCustomerId(String customerId);
}
