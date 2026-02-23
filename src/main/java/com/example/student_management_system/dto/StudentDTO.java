package com.example.student_management_system.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class StudentDTO {
    private Long id;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private String phone;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
