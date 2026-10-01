package com.example.demo.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.models.AppUser;
import com.example.demo.models.Customer;
import com.example.demo.models.Role;
import com.example.demo.repos.CustomerRepository;
import com.example.demo.repos.UserRepository;
import com.example.demo.services.AccountService;
import com.example.demo.services.AuthService;

// Runs once at startup:
//  1) creates the single ADMIN login (the only account allowed to use the username "admin")
//  2) optionally adds a few demo customers so the dashboards aren't empty
@Component
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final AccountService accountService;
	private final PasswordEncoder passwordEncoder;
	private final String adminPassword;
	private final boolean seedDemoData;

	public DataSeeder(UserRepository userRepository, CustomerRepository customerRepository,
					  AccountService accountService, PasswordEncoder passwordEncoder,
					  @Value("${app.admin-password}") String adminPassword,
					  @Value("${app.seed-demo-data}") boolean seedDemoData) {
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.accountService = accountService;
		this.passwordEncoder = passwordEncoder;
		this.adminPassword = adminPassword;
		this.seedDemoData = seedDemoData;
	}

	@Override
	public void run(String... args) {
		if (!userRepository.existsByUsername(AuthService.RESERVED_ADMIN_USERNAME)) {
			userRepository.save(new AppUser(AuthService.RESERVED_ADMIN_USERNAME,
					passwordEncoder.encode(adminPassword), Role.ADMIN, null));
		}

		if (seedDemoData && customerRepository.count() == 0) {
			addDemo("Robert", "Brown", "robert@example.com", "2500", "15000");   // Premium
			addDemo("Priya", "Sharma", "priya@example.com", "1200", "3400");
			addDemo("Rosa", "Martinez", "rosa@example.com", "8000", "6500");     // Premium
		}
	}

	private void addDemo(String first, String last, String email, String checking, String savings) {
		Customer c = customerRepository.save(new Customer(null, first, last, email));
		accountService.createDefaultAccounts(c.getId(), new BigDecimal(checking), new BigDecimal(savings));
	}
}
