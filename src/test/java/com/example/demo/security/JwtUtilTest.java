package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.example.demo.models.AppUser;
import com.example.demo.models.Role;

class JwtUtilTest {

	private static final String SECRET = "test-secret-key-that-is-at-least-32-characters";

	private final JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);

	@Test
	void tokenContainsUsernameRoleAndCustomerId() {
		String token = jwtUtil.generateToken(new AppUser("rohan", "hash", Role.CUSTOMER, "c1"));

		AuthUser user = jwtUtil.parse(token);

		assertEquals("rohan", user.username());
		assertEquals("CUSTOMER", user.role());
		assertEquals("c1", user.customerId());
	}

	@Test
	void changingTheRoleInsideTheTokenIsRejected() {
		// Attack: take a CUSTOMER token and swap in the payload of an ADMIN token, keeping the old signature
		String[] customer = jwtUtil.generateToken(new AppUser("rohan", "hash", Role.CUSTOMER, "c1")).split("\\.");
		String[] admin = jwtUtil.generateToken(new AppUser("admin", "hash", Role.ADMIN, null)).split("\\.");
		String forged = customer[0] + "." + admin[1] + "." + customer[2];

		assertNull(jwtUtil.parse(forged));   // signature no longer matches → rejected
	}

	@Test
	void tokenSignedWithAnotherSecretIsRejected() {
		JwtUtil otherServer = new JwtUtil("a-completely-different-secret-key-of-32-chars", 60_000);
		String token = otherServer.generateToken(new AppUser("admin", "hash", Role.ADMIN, null));

		assertNull(jwtUtil.parse(token));
	}

	@Test
	void expiredTokenIsRejected() {
		JwtUtil expiresImmediately = new JwtUtil(SECRET, -1_000);
		String token = expiresImmediately.generateToken(new AppUser("rohan", "hash", Role.CUSTOMER, "c1"));

		assertNull(jwtUtil.parse(token));
	}

	@Test
	void garbageIsRejected() {
		assertNull(jwtUtil.parse("not-a-jwt"));
	}
}
