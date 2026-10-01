package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

// Enables @Transactional for MongoDB (Atlas supports multi-document transactions),
// so a transfer's two balance updates happen all-or-nothing.
@Configuration
public class MongoConfig {

	@Bean
	public MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
		return new MongoTransactionManager(factory);
	}
}
