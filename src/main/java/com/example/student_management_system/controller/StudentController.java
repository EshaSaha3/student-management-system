package com.example.student_management_system.controller;

import com.example.student_management_system.dto.StudentDTO;
import com.example.student_management_system.entity.Student;
import com.example.student_management_system.service.DepartmentService;
import com.example.student_management_system.service.StudentService;
import com.example.student_management_system.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String listStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "students/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "students/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("studentDTO", new StudentDTO());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "students/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createStudent(@Valid @ModelAttribute StudentDTO studentDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "students/create";
        }

        try {
            studentService.createStudent(studentDTO);
            return "redirect:/students";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "students/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setStudentId(student.getStudentId());
        studentDTO.setName(student.getName());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setPhone(student.getPhone());
        studentDTO.setDepartmentId(student.getDepartment().getId());

        model.addAttribute("studentDTO", studentDTO);
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "students/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute StudentDTO studentDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "students/edit";
        }

        try {
            studentService.updateStudent(id, studentDTO);
            return "redirect:/students";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "students/edit";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteStudent(@PathVariable Long id, Model model) {
        try {
            studentService.deleteStudent(id);
            return "redirect:/students";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }

    @GetMapping("/{studentId}/courses")
    public String viewStudentCourses(@PathVariable Long studentId, Model model) {
        Student student = studentService.getStudentById(studentId);
        model.addAttribute("student", student);
        model.addAttribute("courses", student.getCourses());

        // Only show available courses to teachers
        if (org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            model.addAttribute("availableCourses", courseService.getAllCourses());
        }

        return "students/courses";
    }

    @PostMapping("/{studentId}/courses/enroll/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String enrollCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.enrollInCourse(studentId, courseId);
        return "redirect:/students/" + studentId + "/courses";
    }

    @PostMapping("/{studentId}/courses/withdraw/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String withdrawCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.withdrawFromCourse(studentId, courseId);
        return "redirect:/students/" + studentId + "/courses";
    }
}