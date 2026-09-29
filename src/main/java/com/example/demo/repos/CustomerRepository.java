package com.example.demo.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {

}
