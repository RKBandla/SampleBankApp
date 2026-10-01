package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.demo.models.AppUser;
import com.example.demo.models.Customer;
import com.example.demo.models.RegisterRequest;
import com.example.demo.models.Role;
import com.example.demo.repos.CustomerRepository;
import com.example.demo.repos.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

	private UserRepository userRepository;
	private CustomerRepository customerRepository;
	private AccountService accountService;
	private PasswordEncoder passwordEncoder;
	private JwtUtil jwtUtil;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		customerRepository = mock(CustomerRepository.class);
		accountService = mock(AccountService.class);
		passwordEncoder = mock(PasswordEncoder.class);
		jwtUtil = mock(JwtUtil.class);
		authService = new AuthService(userRepository, customerRepository, accountService, passwordEncoder, jwtUtil);
	}

	@Test
	void registeringAsAdminIsBlocked() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> authService.register(request("Admin", "Password1")));

		assertEquals("The username 'admin' is reserved", e.getMessage());
		verify(userRepository, never()).save(any(AppUser.class));
	}

	@Test
	void duplicateUsernameIsRejected() {
		when(userRepository.existsByUsername("rohan")).thenReturn(true);

		assertThrows(IllegalStateException.class, () -> authService.register(request("rohan", "Password1")));
	}

	@Test
	void registerStoresHashedPasswordAndCustomerRole() {
		Customer saved = new Customer("c1", "Rohan", null, null);
		when(customerRepository.save(any(Customer.class))).thenReturn(saved);
		when(passwordEncoder.encode("Password1")).thenReturn("$2a$10$hashed");

		authService.register(request("rohan", "Password1"));

		ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
		verify(userRepository).save(captor.capture());
		AppUser user = captor.getValue();
		assertEquals("$2a$10$hashed", user.getPassword());       // never the plain password
		assertNotEquals("Password1", user.getPassword());
		assertEquals(Role.CUSTOMER, user.getRole());
		assertEquals("c1", user.getCustomerId());
	}

	@Test
	void loginWithWrongPasswordReturnsNull() {
		AppUser user = new AppUser("rohan", "$2a$10$hashed", Role.CUSTOMER, "c1");
		when(userRepository.findByUsername("rohan")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrong", "$2a$10$hashed")).thenReturn(false);

		assertNull(authService.login("rohan", "wrong"));
	}

	@Test
	void loginAsAdminReturnsAdminToken() {
		AppUser admin = new AppUser("admin", "$2a$10$hashed", Role.ADMIN, null);
		when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
		when(passwordEncoder.matches("Admin@123", "$2a$10$hashed")).thenReturn(true);
		when(jwtUtil.generateToken(admin)).thenReturn("jwt-token");

		Map<String, Object> result = authService.login("admin", "Admin@123");

		assertEquals("jwt-token", result.get("token"));
		assertEquals("AdminToken", result.get("tokenType"));
		assertEquals(Role.ADMIN, result.get("role"));
	}

	private static RegisterRequest request(String username, String password) {
		RegisterRequest r = new RegisterRequest();
		r.setUsername(username);
		r.setPassword(password);
		r.setFirstName("Rohan");
		return r;
	}
}
