package com.example.student_management_system.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CourseDTO {
    private Long id;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 6, message = "Credits cannot exceed 6")
    private Integer credits;

    private String description;
}