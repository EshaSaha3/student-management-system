package com.example.student_management_system.service;

import com.example.student_management_system.dto.StudentDTO;
import com.example.student_management_system.entity.Department;
import com.example.student_management_system.entity.Student;
import com.example.student_management_system.repository.DepartmentRepository;
import com.example.student_management_system.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private StudentService studentService;

    private Department department;
    private Student student;
    private StudentDTO studentDTO;

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

        studentDTO = new StudentDTO();
        studentDTO.setStudentId("STU001");
        studentDTO.setName("John Doe");
        studentDTO.setEmail("john@example.com");
        studentDTO.setDepartmentId(1L);
    }

    @Test
    void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student));

        List<Student> students = studentService.getAllStudents();

        assertThat(students).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("John Doe");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student found = studentService.getStudentById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John Doe");
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");

        verify(studentRepository, times(1)).findById(99L);
    }

    @Test
    void testCreateStudent_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student created = studentService.createStudent(studentDTO);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("John Doe");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testCreateStudent_DepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.createStudent(studentDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Department not found");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDTO updateDTO = new StudentDTO();
        updateDTO.setName("Jane Doe");
        updateDTO.setEmail("jane@example.com");
        updateDTO.setDepartmentId(1L);

        Student updated = studentService.updateStudent(1L, updateDTO);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Jane Doe");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testDeleteStudent() {
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).deleteById(1L);
    }
}