package com.example.campus_share;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.campus_share.mapper")
public class CampusShareApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusShareApplication.class, args);
	}
}
