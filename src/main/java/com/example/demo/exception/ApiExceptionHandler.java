package com.example.demo.exception;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Turns exceptions into clean JSON error responses: {"error": "..."}
@RestControllerAdvice
public class ApiExceptionHandler {

	// @Valid failed, e.g. "password must be at least 8 characters"
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getDefaultMessage())
				.distinct()
				.collect(Collectors.joining(", "));
		return ResponseEntity.badRequest().body(Map.of("error", message));
	}

	// Body is not valid JSON, or a number field contains text
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> unreadable(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest().body(Map.of("error", "Request body is missing or not valid JSON"));
	}

	// e.g. "Insufficient funds", "The username 'admin' is reserved"
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
