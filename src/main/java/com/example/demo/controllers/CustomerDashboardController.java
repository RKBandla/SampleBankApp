package com.example.demo.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.CustomerView;
import com.example.demo.models.AmountRequest;
import com.example.demo.models.TransferRequest;
import com.example.demo.security.AuthUser;
import com.example.demo.services.AccountService;
import com.example.demo.services.CustomerService;

import jakarta.validation.Valid;

// CUSTOMER ONLY, and only for their OWN id
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CustomerDashboardController {

	private CustomerService customerService;
	private AccountService accountService;

	@Autowired
	public CustomerDashboardController(CustomerService customerService, AccountService accountService) {
		this.customerService = customerService;
		this.accountService = accountService;
	}

	// http://localhost:8080/api/customerDashboard/{id}
	@GetMapping("/customerDashboard/{id}")
	public ResponseEntity<?> getDashboard(@PathVariable String id, @AuthenticationPrincipal AuthUser user) {
		if (!isOwner(id, user)) {
			return forbidden();
		}
		CustomerView customer = customerService.getCustomerById(id);
		if (customer == null) {
			return ResponseEntity.notFound().build();
		}
		Map<String, Object> dashboard = new LinkedHashMap<>();
		dashboard.put("customer", customer);   // includes accounts + total balance
		dashboard.put("transactions", accountService.getRecentTransactions(id));
		return ResponseEntity.ok(dashboard);
	}

	@PostMapping("/customerDashboard/{id}/deposit")
	public ResponseEntity<?> deposit(@PathVariable String id, @Valid @RequestBody AmountRequest request,
									 @AuthenticationPrincipal AuthUser user) {
		if (!isOwner(id, user)) {
			return forbidden();
		}
		return ResponseEntity.ok(accountService.deposit(id, request.getAccountId(), request.getAmount()));
	}

	@PostMapping("/customerDashboard/{id}/withdraw")
	public ResponseEntity<?> withdraw(@PathVariable String id, @Valid @RequestBody AmountRequest request,
									  @AuthenticationPrincipal AuthUser user) {
		if (!isOwner(id, user)) {
			return forbidden();
		}
		return ResponseEntity.ok(accountService.withdraw(id, request.getAccountId(), request.getAmount()));
	}

	@PostMapping("/customerDashboard/{id}/transfer")
	public ResponseEntity<?> transfer(@PathVariable String id, @Valid @RequestBody TransferRequest request,
									  @AuthenticationPrincipal AuthUser user) {
		if (!isOwner(id, user)) {
			return forbidden();
		}
		return ResponseEntity.ok(accountService.transfer(id, request.getFromAccountId(),
				request.getToAccountId(), request.getAmount()));
	}

	// The customerId inside the token must match the {id} in the URL
	private boolean isOwner(String id, AuthUser user) {
		return user != null && id.equals(user.customerId());
	}

	private ResponseEntity<Map<String, String>> forbidden() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(Map.of("error", "Forbidden - you can only view your own dashboard"));
	}
}
