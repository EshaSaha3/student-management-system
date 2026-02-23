package com.example.student_management_system.service;

import com.example.student_management_system.dto.TeacherDTO;
import com.example.student_management_system.entity.Teacher;
import com.example.student_management_system.entity.Course;
import com.example.student_management_system.repository.TeacherRepository;
import com.example.student_management_system.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
    }

    public Teacher getTeacherByTeacherId(String teacherId) {
        return teacherRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with teacherId: " + teacherId));
    }

    @Transactional
    public Teacher createTeacher(TeacherDTO teacherDTO) {
        // Check if teacher ID already exists
        if (teacherRepository.findByTeacherId(teacherDTO.getTeacherId()).isPresent()) {
            throw new RuntimeException("Teacher with ID " + teacherDTO.getTeacherId() + " already exists");
        }

        // Check if email already exists
        if (teacherRepository.findByEmail(teacherDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Teacher with email " + teacherDTO.getEmail() + " already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setTeacherId(teacherDTO.getTeacherId());
        teacher.setName(teacherDTO.getName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setPhone(teacherDTO.getPhone());

        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher teacher = getTeacherById(id);

        // Check if email conflicts (if changed)
        if (!teacher.getEmail().equals(teacherDTO.getEmail()) &&
                teacherRepository.findByEmail(teacherDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Teacher with email " + teacherDTO.getEmail() + " already exists");
        }

        teacher.setName(teacherDTO.getName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setPhone(teacherDTO.getPhone());

        return teacherRepository.save(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = getTeacherById(id);

        // Remove teacher from courses
        if (teacher.getCourses() != null) {
            for (Course course : teacher.getCourses()) {
                course.getTeachers().remove(teacher);
            }
        }

        teacherRepository.deleteById(id);
    }

    @Transactional
    public Teacher assignCourseToTeacher(Long teacherId, Long courseId) {
        Teacher teacher = getTeacherById(teacherId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        if (!teacher.getCourses().contains(course)) {
            teacher.getCourses().add(course);
            course.getTeachers().add(teacher);
            teacherRepository.save(teacher);
        }

        return teacher;
    }

    @Transactional
    public Teacher removeCourseFromTeacher(Long teacherId, Long courseId) {
        Teacher teacher = getTeacherById(teacherId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        teacher.getCourses().remove(course);
        course.getTeachers().remove(teacher);
        teacherRepository.save(teacher);

        return teacher;
    }
}
