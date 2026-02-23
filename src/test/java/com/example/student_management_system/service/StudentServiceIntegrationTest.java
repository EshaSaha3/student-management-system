package com.example.student_management_system.service;

import com.example.student_management_system.dto.StudentDTO;
import com.example.student_management_system.entity.Department;
import com.example.student_management_system.entity.Student;
import com.example.student_management_system.repository.DepartmentRepository;
import com.example.student_management_system.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class StudentServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        departmentRepository.deleteAll();

        department = new Department();
        department.setName("Computer Science");
        department.setDescription("CS Department");
        department = departmentRepository.save(department);
    }

    @Test
    void testCreateAndRetrieveStudent() {
        // Create student
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId("STU001");
        studentDTO.setName("John Doe");
        studentDTO.setEmail("john@example.com");
        studentDTO.setDepartmentId(department.getId());

        Student created = studentService.createStudent(studentDTO);
        assertThat(created.getId()).isNotNull();

        // Retrieve student
        Student found = studentService.getStudentById(created.getId());
        assertThat(found.getName()).isEqualTo("John Doe");
        assertThat(found.getDepartment().getName()).isEqualTo("Computer Science");
    }

    @Test
    void testGetAllStudents() {
        // Create multiple students
        StudentDTO student1 = new StudentDTO();
        student1.setStudentId("STU001");
        student1.setName("John Doe");
        student1.setEmail("john@example.com");
        student1.setDepartmentId(department.getId());

        StudentDTO student2 = new StudentDTO();
        student2.setStudentId("STU002");
        student2.setName("Jane Doe");
        student2.setEmail("jane@example.com");
        student2.setDepartmentId(department.getId());

        studentService.createStudent(student1);
        studentService.createStudent(student2);

        List<Student> students = studentService.getAllStudents();
        assertThat(students).hasSize(2);
    }
}