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
import com.example.demo.services.AuthService;

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
	@PostMapping("/auth/register") // http://localhost:8080/api/auth/register
	public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest request) {
		if (isBlank(request.getUsername()) || isBlank(request.getPassword())) {
			return ResponseEntity.badRequest().body(Map.of("error", "username and password are required"));
		}
		authService.register(request);   // 'admin' → 400, taken → 409 (see ApiExceptionHandler)
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered"));
	}

	// Same login for admin and customers; the returned token says which one you are
	@PostMapping("/auth/login") // http://localhost:8080/api/auth/login
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		Map<String, Object> result = authService.login(request.getUsername(), request.getPassword());
		if (result == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
		}
		return ResponseEntity.ok(result);
	}

	private boolean isBlank(String s) {
		return s == null || s.isBlank();
	}
}
