package com.example.loanapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class LoanAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanAppApplication.class, args);
	}

}
