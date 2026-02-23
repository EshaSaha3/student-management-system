package com.example.student_management_system;

import org.springframework.boot.SpringApplication;

public class TestStudentManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.from(StudentManagementSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
