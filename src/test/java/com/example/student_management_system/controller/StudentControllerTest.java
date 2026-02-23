package com.example.student_management_system.controller;

import com.example.student_management_system.dto.StudentDTO;
import com.example.student_management_system.entity.Department;
import com.example.student_management_system.entity.Student;
import com.example.student_management_system.service.DepartmentService;
import com.example.student_management_system.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(TestSecurityConfig.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Department department;
    private Student student;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");

        student = new Student();
        student.setId(1L);
        student.setStudentId("STU001");
        student.setName("John Doe");
        student.setEmail("john@example.com");
        student.setDepartment(department);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testListStudents_AsStudent() throws Exception {
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testListStudents_AsTeacher() throws Exception {
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testViewStudent_AsStudent() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(student);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/view"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testShowCreateForm_AsTeacher() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(department));

        mockMvc.perform(get("/students/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/create"))
                .andExpect(model().attributeExists("studentDTO"))
                .andExpect(model().attributeExists("departments"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testShowCreateForm_AsStudent_ShouldForbidden() throws Exception {
        mockMvc.perform(get("/students/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testCreateStudent_AsTeacher() throws Exception {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId("STU002");
        studentDTO.setName("Jane Doe");
        studentDTO.setEmail("jane@example.com");
        studentDTO.setDepartmentId(1L);

        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(new Student());

        mockMvc.perform(post("/students/create")
                        .with(csrf())
                        .param("studentId", "STU002")
                        .param("name", "Jane Doe")
                        .param("email", "jane@example.com")
                        .param("departmentId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testDeleteStudent_AsTeacher() throws Exception {
        mockMvc.perform(get("/students/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testDeleteStudent_AsStudent_ShouldForbidden() throws Exception {
        mockMvc.perform(get("/students/delete/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}