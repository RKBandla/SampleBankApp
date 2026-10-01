package com.example.demo.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Account;

public interface AccountRepository extends MongoRepository<Account, String> {

	List<Account> findByCustomerId(String customerId);

	void deleteByCustomerId(String customerId);
}
