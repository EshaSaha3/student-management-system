package com.example.student_management_system.config;

import com.example.student_management_system.entity.*;
import com.example.student_management_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create users
        if (userRepository.count() == 0) {
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setEmail("teacher@school.com");
            teacher.setRoles(new HashSet<>(Arrays.asList("TEACHER")));
            teacher.setEnabled(true);
            userRepository.save(teacher);

            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setEmail("student@school.com");
            student.setRoles(new HashSet<>(Arrays.asList("STUDENT")));
            student.setEnabled(true);
            userRepository.save(student);
        }

        // Create departments
        if (departmentRepository.count() == 0) {
            Department cse = new Department();
            cse.setName("Computer Science");
            cse.setDescription("Computer Science and Engineering Department");
            departmentRepository.save(cse);

            Department eee = new Department();
            eee.setName("Electrical Engineering");
            eee.setDescription("Electrical and Electronic Engineering Department");
            departmentRepository.save(eee);
        }
    }
}