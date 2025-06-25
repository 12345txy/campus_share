package com.example.campus_share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.example.campus_share")
public class CampusShareApplication {
	public static void main(String[] args) {
		SpringApplication.run(CampusShareApplication.class, args);
	}
}
