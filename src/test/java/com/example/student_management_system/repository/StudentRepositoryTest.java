package com.example.student_management_system.repository;

import com.example.student_management_system.entity.Department;
import com.example.student_management_system.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;
    private Student student;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setName("Computer Science");
        department.setDescription("CS Department");
        department = departmentRepository.save(department);

        student = new Student();
        student.setStudentId("STU001");
        student.setName("John Doe");
        student.setEmail("john@example.com");
        student.setPhone("1234567890");
        student.setDepartment(department);
    }

    @Test
    void testSaveStudent() {
        Student savedStudent = studentRepository.save(student);

        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getStudentId()).isEqualTo("STU001");
    }

    @Test
    void testFindByStudentId() {
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findByStudentId("STU001");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void testFindByEmail() {
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findByEmail("john@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getStudentId()).isEqualTo("STU001");
    }

    @Test
    void testDeleteStudent() {
        Student saved = studentRepository.save(student);
        studentRepository.deleteById(saved.getId());

        Optional<Student> found = studentRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}