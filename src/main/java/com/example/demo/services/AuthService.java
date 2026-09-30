package com.example.demo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.models.AppUser;
import com.example.demo.repos.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class AuthService {
   private UserRepository userRepository;
   private PasswordEncoder passwordEncoder;
   private JwtUtil jwtUtil;

   public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
       this.userRepository = userRepository;
       this.passwordEncoder = passwordEncoder;
       this.jwtUtil = jwtUtil;
   }

   // Returns false if the username is already taken
   public boolean register(String username, String password) {
       if (userRepository.existsByUsername(username)) {
           return false;
       }
       userRepository.save(new AppUser(username, passwordEncoder.encode(password)));
       return true;
   }

   // Returns a JWT if username/password are correct, otherwise null
   public String login(String username, String password) {
       AppUser user = userRepository.findByUsername(username).orElse(null);
       if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
           return null;
       }
       return jwtUtil.generateToken(username);
   }
}
