package com.example.student_management_system.controller;

import com.example.student_management_system.dto.CourseDTO;
import com.example.student_management_system.entity.Course;
import com.example.student_management_system.service.CourseService;
import com.example.student_management_system.service.StudentService;
import com.example.student_management_system.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "courses/list";
    }

    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("students", course.getStudents());
        model.addAttribute("teachers", course.getTeachers());
        return "courses/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("courseDTO", new CourseDTO());
        return "courses/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createCourse(@Valid @ModelAttribute CourseDTO courseDTO,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "courses/create";
        }

        try {
            courseService.createCourse(courseDTO);
            return "redirect:/courses";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "courses/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setCourseCode(course.getCourseCode());
        courseDTO.setCourseName(course.getCourseName());
        courseDTO.setCredits(course.getCredits());
        courseDTO.setDescription(course.getDescription());

        model.addAttribute("courseDTO", courseDTO);
        return "courses/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute CourseDTO courseDTO,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "courses/edit";
        }

        try {
            courseService.updateCourse(id, courseDTO);
            return "redirect:/courses";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "courses/edit";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteCourse(@PathVariable Long id, Model model) {
        try {
            courseService.deleteCourse(id);
            return "redirect:/courses";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/courses";
        }
    }

    @GetMapping("/{courseId}/students")
    public String viewEnrolledStudents(@PathVariable Long courseId, Model model) {
        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        model.addAttribute("students", course.getStudents());

        // Only show available students to teachers
        if (org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            model.addAttribute("availableStudents", studentService.getAllStudents());
        }

        return "courses/students";
    }

    @PostMapping("/{courseId}/students/enroll/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String enrollStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        courseService.enrollStudent(courseId, studentId);
        return "redirect:/courses/" + courseId + "/students";
    }

    @PostMapping("/{courseId}/students/remove/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String removeStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        courseService.unenrollStudent(courseId, studentId);
        return "redirect:/courses/" + courseId + "/students";
    }
}