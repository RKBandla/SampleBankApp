package com.example.demo.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.controllers.AdminController;
import com.example.demo.controllers.CustomerDashboardController;
import com.example.demo.models.AppUser;
import com.example.demo.models.Role;
import com.example.demo.services.AccountService;
import com.example.demo.services.AdminService;
import com.example.demo.services.CustomerService;

// Loads ONLY the web layer + our real security rules (no database).
// Proves: no token → 401, CustomerToken on /api/admin → 403, AdminToken → 200.
@WebMvcTest(controllers = { AdminController.class, CustomerDashboardController.class })
@Import({ SecurityConfig.class, JwtAuthFilter.class, JwtUtil.class })
class SecurityRulesTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtUtil jwtUtil;

	@MockBean
	private AdminService adminService;

	@MockBean
	private CustomerService customerService;

	@MockBean
	private AccountService accountService;

	private String adminToken() {
		return "Bearer " + jwtUtil.generateToken(new AppUser("admin", "x", Role.ADMIN, null));
	}

	private String customerToken(String customerId) {
		return "Bearer " + jwtUtil.generateToken(new AppUser("rohan", "x", Role.CUSTOMER, customerId));
	}

	@Test
	void noTokenGets401() throws Exception {
		mockMvc.perform(get("/api/admin")).andExpect(status().isUnauthorized());
	}

	@Test
	void customerTokenOnAdminDashboardGets403() throws Exception {
		mockMvc.perform(get("/api/admin").header("Authorization", customerToken("c1")))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminTokenOnAdminDashboardGets200() throws Exception {
		when(adminService.getDashboard()).thenReturn(Map.of("totalCustomers", 0));

		mockMvc.perform(get("/api/admin").header("Authorization", adminToken()))
				.andExpect(status().isOk());
	}

	@Test
	void customerCannotOpenAnotherCustomersDashboard() throws Exception {
		mockMvc.perform(get("/api/customerDashboard/c2").header("Authorization", customerToken("c1")))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminCannotOpenCustomerDashboard() throws Exception {
		mockMvc.perform(get("/api/customerDashboard/c1").header("Authorization", adminToken()))
				.andExpect(status().isForbidden());
	}
}
