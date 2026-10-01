package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.AuthRequest;
import com.example.demo.models.RegisterRequest;
import com.example.demo.services.AuthService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuthController {

	private AuthService authService;

	@Autowired
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// Body: {"username","password","firstName","lastName","email"}
	// @Valid checks the rules in RegisterRequest (username format, password >= 8 chars, email) → 400 if broken
	@PostMapping("/auth/register") // http://localhost:8080/api/auth/register
	public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);   // 'admin' → 400, taken → 409 (see ApiExceptionHandler)
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered"));
	}

	// Same login for admin and customers; the returned token says which one you are
	@PostMapping("/auth/login") // http://localhost:8080/api/auth/login
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
		Map<String, Object> result = authService.login(request.getUsername(), request.getPassword());
		if (result == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
		}
		return ResponseEntity.ok(result);
	}
}
