package com.example.demo.security;

// Who is making the current request (read from the JWT by JwtAuthFilter)
public record AuthUser(String username, String role, String customerId) {
}
