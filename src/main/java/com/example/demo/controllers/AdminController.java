package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.AdminService;

// ADMIN ONLY. A customer token gets 403 Forbidden here (see SecurityConfig).
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AdminController {

	private AdminService adminService;

	@Autowired
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/admin") // http://localhost:8080/api/admin
	public Map<String, Object> getAdminDashboard() {
		return adminService.getDashboard();
	}
}
