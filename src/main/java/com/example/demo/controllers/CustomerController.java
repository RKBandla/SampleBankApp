package com.example.demo.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Customer;
import com.example.demo.models.CustomerView;
import com.example.demo.services.CustomerService;

// ADMIN ONLY (see SecurityConfig): manage all customers
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CustomerController {

	private CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping("/customers") // http://localhost:8080/api/customers
	public List<CustomerView> getAllCustomers() {
		return customerService.getAllCustomers();
	}

	// Search: /api/customers/search?firstName=ro
	@GetMapping("/customers/search")
	public List<CustomerView> findCustomerByFirstName(@RequestParam(defaultValue = "") String firstName) {
		return customerService.findByFirstName(firstName);
	}

	// Filter: customers with total balance >= 10,000
	@GetMapping("/customers/premium")
	public List<CustomerView> getPremiumCustomers() {
		return customerService.getPremiumCustomers();
	}

	@GetMapping("/customers/{id}")
	public ResponseEntity<CustomerView> getCustomerById(@PathVariable String id) {
		CustomerView customer = customerService.getCustomerById(id);
		if (customer == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(customer);
	}

	@PostMapping("/customers")
	public ResponseEntity<CustomerView> createCustomer(@RequestBody Customer customer) {
		CustomerView created = customerService.createCustomer(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/customers/{id}")
	public ResponseEntity<CustomerView> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
		CustomerView updated = customerService.updateCustomer(id, customer);
		if (updated == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/customers/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
		if (!customerService.deleteCustomer(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
	}
}
