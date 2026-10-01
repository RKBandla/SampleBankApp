package com.example.demo.exception;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Turns exceptions thrown by services into clean JSON error responses
@RestControllerAdvice
public class ApiExceptionHandler {

	// e.g. "Insufficient funds", "Amount must be greater than 0"
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
	}

	// e.g. "Username already exists"
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Map<String, String>> conflict(IllegalStateException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
	}

	// e.g. "Account not found"
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Map<String, String>> notFound(NoSuchElementException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
	}
}
