package com.example.demo.repos;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.demo.models.Customer;

// In-memory storage (no DB). Data resets when the app restarts.
@Repository
public class CustomerRepository {

	private final Map<String, Customer> customers = new LinkedHashMap<>();
	private final AtomicLong idCounter = new AtomicLong(0);

	public List<Customer> findAll() {
		return new ArrayList<>(customers.values());
	}

	public Optional<Customer> findById(String id) {
		return Optional.ofNullable(customers.get(id));
	}

	public Customer save(Customer customer) {
		if (customer.getId() == null || customer.getId().isEmpty()) {
			customer.setId(String.valueOf(idCounter.incrementAndGet()));
		}
		customers.put(customer.getId(), customer);
		return customer;
	}

	public boolean existsById(String id) {
		return customers.containsKey(id);
	}

	public void deleteById(String id) {
		customers.remove(id);
	}
}
