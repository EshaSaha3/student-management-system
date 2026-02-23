package com.example.student_management_system.service;

import com.example.student_management_system.dto.CourseDTO;
import com.example.student_management_system.entity.Course;
import com.example.student_management_system.entity.Student;
import com.example.student_management_system.entity.Teacher;
import com.example.student_management_system.repository.CourseRepository;
import com.example.student_management_system.repository.StudentRepository;
import com.example.student_management_system.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public Course getCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
    }

    @Transactional
    public Course createCourse(CourseDTO courseDTO) {
        // Check if course code already exists
        if (courseRepository.findByCourseCode(courseDTO.getCourseCode()).isPresent()) {
            throw new RuntimeException("Course with code " + courseDTO.getCourseCode() + " already exists");
        }

        Course course = new Course();
        course.setCourseCode(courseDTO.getCourseCode());
        course.setCourseName(courseDTO.getCourseName());
        course.setCredits(courseDTO.getCredits());
        course.setDescription(courseDTO.getDescription());

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, CourseDTO courseDTO) {
        Course course = getCourseById(id);

        course.setCourseName(courseDTO.getCourseName());
        course.setCredits(courseDTO.getCredits());
        course.setDescription(courseDTO.getDescription());

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);

        // Remove course from all students
        if (course.getStudents() != null) {
            for (Student student : course.getStudents()) {
                student.getCourses().remove(course);
            }
        }

        // Remove course from all teachers
        if (course.getTeachers() != null) {
            for (Teacher teacher : course.getTeachers()) {
                teacher.getCourses().remove(course);
            }
        }

        courseRepository.deleteById(id);
    }

    @Transactional
    public Course enrollStudent(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        if (!course.getStudents().contains(student)) {
            course.getStudents().add(student);
            student.getCourses().add(course);
            courseRepository.save(course);
        }

        return course;
    }

    @Transactional
    public Course unenrollStudent(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        course.getStudents().remove(student);
        student.getCourses().remove(course);
        courseRepository.save(course);

        return course;
    }

    public List<Student> getEnrolledStudents(Long courseId) {
        Course course = getCourseById(courseId);
        return course.getStudents();
    }

    public List<Teacher> getAssignedTeachers(Long courseId) {
        Course course = getCourseById(courseId);
        return course.getTeachers();
    }

    public long getCourseCount() {
        return courseRepository.count();
    }
}