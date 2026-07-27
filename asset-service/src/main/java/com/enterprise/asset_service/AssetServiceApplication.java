package com.enterprise.asset_service;

import com.enterprise.asset_service.model.Asset;
import com.enterprise.asset_service.repository.jpa.AssetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
// Point strictly to the SQL subfolder
@EnableJpaRepositories(basePackages = "com.enterprise.asset_service.repository.jpa")
// Point strictly to the NoSQL subfolder
@EnableMongoRepositories(basePackages = "com.enterprise.asset_service.repository.mongo")
public class AssetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(AssetRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				System.out.println("🚀 Seeding enterprise asset data records into PostgreSQL container...");
				repository.save(new Asset(101L, "Production Database Server", "Infrastructure", "Active", "DBA Team"));
				repository.save(new Asset(102L, "Customer Portal Frontend", "Application", "Maintenance", "Web Team"));
				repository.save(new Asset(103L, "Legacy Payment Gateway", "API Integration", "Decommissioned", "Finance IT"));
				System.out.println("✅ Database seeding completed successfully!");
			}
		};
	}
}
