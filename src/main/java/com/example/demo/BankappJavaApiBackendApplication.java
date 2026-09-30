package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Users come from MongoDB + JWT, so turn off Spring's default in-memory user
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class BankappJavaApiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankappJavaApiBackendApplication.class, args);
	}

}
