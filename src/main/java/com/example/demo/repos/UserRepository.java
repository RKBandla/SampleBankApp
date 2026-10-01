package com.example.demo.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.AppUser;

public interface UserRepository extends MongoRepository<AppUser, String> {

	Optional<AppUser> findByUsername(String username);

	boolean existsByUsername(String username);

	List<AppUser> findByCustomerId(String customerId);

	void deleteByCustomerId(String customerId);
}
