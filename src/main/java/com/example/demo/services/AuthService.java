package com.example.demo.services;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.models.AppUser;
import com.example.demo.models.AuthRequest;
import com.example.demo.models.Customer;
import com.example.demo.models.Role;
import com.example.demo.repos.CustomerRepository;
import com.example.demo.repos.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class AuthService {

   public static final String RESERVED_ADMIN_USERNAME = "admin";

   private UserRepository userRepository;
   private CustomerRepository customerRepository;
   private AccountService accountService;
   private PasswordEncoder passwordEncoder;
   private JwtUtil jwtUtil;

   public AuthService(UserRepository userRepository, CustomerRepository customerRepository,
                      AccountService accountService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
       this.userRepository = userRepository;
       this.customerRepository = customerRepository;
       this.accountService = accountService;
       this.passwordEncoder = passwordEncoder;
       this.jwtUtil = jwtUtil;
   }

   // Registration always creates a CUSTOMER (never an admin).
   // It also creates the Customer record and their Checking + Savings accounts.
   public void register(AuthRequest request) {
       String username = request.getUsername().trim();
       if (username.equalsIgnoreCase(RESERVED_ADMIN_USERNAME)) {
           throw new IllegalArgumentException("The username 'admin' is reserved");
       }
       if (userRepository.existsByUsername(username)) {
           throw new IllegalStateException("Username already exists");
       }
       String firstName = isBlank(request.getFirstName()) ? username : request.getFirstName().trim();

       Customer customer = customerRepository.save(
               new Customer(null, firstName, request.getLastName(), request.getEmail()));
       accountService.createDefaultAccounts(customer.getId(), BigDecimal.ZERO, BigDecimal.ZERO);

       userRepository.save(new AppUser(username, passwordEncoder.encode(request.getPassword()),
               Role.CUSTOMER, customer.getId()));
   }

   // Returns token + role + customerId, or null if username/password are wrong
   public Map<String, Object> login(String username, String password) {
       AppUser user = userRepository.findByUsername(username == null ? "" : username.trim()).orElse(null);
       if (user == null || password == null || !passwordEncoder.matches(password, user.getPassword())) {
           return null;
       }
       Map<String, Object> result = new LinkedHashMap<>();
       result.put("token", jwtUtil.generateToken(user));
       result.put("tokenType", user.getRole() == Role.ADMIN ? "AdminToken" : "CustomerToken");
       result.put("role", user.getRole());
       result.put("username", user.getUsername());
       result.put("customerId", user.getCustomerId());
       return result;
   }

   private boolean isBlank(String s) {
       return s == null || s.isBlank();
   }
}
