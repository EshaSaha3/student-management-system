package com.example.student_management_system.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class TeacherDTO {
    private Long id;

    @NotBlank(message = "Teacher ID is required")
    @Pattern(regexp = "^TCH\\d{3}$", message = "Teacher ID must be in format TCH001")
    private String teacherId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;
}
