package com.example.KYC.project;

import com.example.KYC.project.entity.User;
import com.example.KYC.project.enums.Role;
import com.example.KYC.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class KycProjectApplication {

	public static void main(String[] args) {

		SpringApplication.run(KycProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner createAdmin(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {

		return args -> {

			// Check karo admin already exist karta hai ya nahi
			if (userRepository
					.findByEmail("admin@gmail.com")
					.isEmpty()) {

				User admin = new User();

				admin.setName("Main Admin");

				admin.setEmail("admin@gmail.com");

				// Password ko encrypt karke save karenge
				admin.setPassword(
						passwordEncoder.encode("Admin@123")
				);

				// IMPORTANT
				admin.setRole(Role.ADMIN);

				userRepository.save(admin);

				System.out.println(
						"=============================="
				);

				System.out.println(
						"ADMIN CREATED SUCCESSFULLY"
				);

				System.out.println(
						"Email: admin@gmail.com"
				);

				System.out.println(
						"Password: Admin@123"
				);

				System.out.println(
						"=============================="
				);

			} else {

				System.out.println(
						"Admin already exists"
				);
			}
		};
	}


}
