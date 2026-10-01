package com.example.demo.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {

	// Search: "ro" finds "Rohan", "ROBERT", ... (Spring builds the query from the method name)
	List<Customer> findByFirstNameContainingIgnoreCase(String firstName);
}
