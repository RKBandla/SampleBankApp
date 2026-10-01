package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.models.AppUser;
import com.example.demo.models.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final SecretKey key;
	private final long expirationMs;

	public JwtUtil(@Value("${jwt.secret}") String secret,
				   @Value("${jwt.expiration-ms}") long expirationMs) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	// The server creates the token. The role is put INSIDE the token,
	// so there are two kinds: AdminToken (role=ADMIN) and CustomerToken (role=CUSTOMER).
	public String generateToken(AppUser user) {
		Date now = new Date();
		var builder = Jwts.builder()
				.subject(user.getUsername())
				.claim("role", user.getRole().name())
				.claim("tokenType", user.getRole() == Role.ADMIN ? "AdminToken" : "CustomerToken")
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMs));
		if (user.getCustomerId() != null) {
			builder.claim("customerId", user.getCustomerId());
		}
		return builder.signWith(key).compact();
	}

	// Returns who the token belongs to, or null if it's invalid/expired/tampered
	public AuthUser parse(String token) {
		try {
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();
			return new AuthUser(claims.getSubject(),
					claims.get("role", String.class),
					claims.get("customerId", String.class));
		} catch (JwtException | IllegalArgumentException e) {
			return null;
		}
	}
}
