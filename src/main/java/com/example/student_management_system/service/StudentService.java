package com.example.student_management_system.service;

import com.example.student_management_system.dto.StudentDTO;
import com.example.student_management_system.entity.Department;
import com.example.student_management_system.entity.Student;
import com.example.student_management_system.entity.Course;
import com.example.student_management_system.repository.DepartmentRepository;
import com.example.student_management_system.repository.StudentRepository;
import com.example.student_management_system.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public Student getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with studentId: " + studentId));
    }

    @Transactional
    public Student createStudent(StudentDTO studentDTO) {
        // Check if student ID already exists
        if (studentRepository.findByStudentId(studentDTO.getStudentId()).isPresent()) {
            throw new RuntimeException("Student with ID " + studentDTO.getStudentId() + " already exists");
        }

        // Check if email already exists
        if (studentRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Student with email " + studentDTO.getEmail() + " already exists");
        }

        Department department = departmentRepository.findById(studentDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + studentDTO.getDepartmentId()));

        Student student = new Student();
        student.setStudentId(studentDTO.getStudentId());
        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        student.setPhone(studentDTO.getPhone());
        student.setDepartment(department);

        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, StudentDTO studentDTO) {
        Student student = getStudentById(id);

        // Check if email conflicts (if changed)
        if (!student.getEmail().equals(studentDTO.getEmail()) &&
                studentRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Student with email " + studentDTO.getEmail() + " already exists");
        }

        Department department = departmentRepository.findById(studentDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + studentDTO.getDepartmentId()));

        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        student.setPhone(studentDTO.getPhone());
        student.setDepartment(department);

        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);

        // Remove student from courses
        if (student.getCourses() != null) {
            for (Course course : student.getCourses()) {
                course.getStudents().remove(student);
            }
        }

        studentRepository.deleteById(id);
    }

    @Transactional
    public Student enrollInCourse(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        if (!student.getCourses().contains(course)) {
            student.getCourses().add(course);
            course.getStudents().add(student);
            studentRepository.save(student);
        }

        return student;
    }

    @Transactional
    public Student withdrawFromCourse(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        student.getCourses().remove(course);
        course.getStudents().remove(student);
        studentRepository.save(student);

        return student;
    }

    public List<Course> getEnrolledCourses(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getCourses();
    }

    public long getStudentCount() {
        return studentRepository.count();
    }

    public List<Student> getStudentsByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));
        return department.getStudents();
    }
}