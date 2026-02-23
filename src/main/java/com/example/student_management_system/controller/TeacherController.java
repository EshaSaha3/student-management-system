package com.example.student_management_system.controller;

import com.example.student_management_system.dto.TeacherDTO;
import com.example.student_management_system.entity.Teacher;
import com.example.student_management_system.service.TeacherService;
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
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public String listTeachers(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teachers/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String viewTeacher(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        model.addAttribute("teacher", teacher);
        return "teachers/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("teacherDTO", new TeacherDTO());
        return "teachers/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createTeacher(@Valid @ModelAttribute TeacherDTO teacherDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "teachers/create";
        }

        try {
            teacherService.createTeacher(teacherDTO);
            return "redirect:/teachers";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "teachers/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(teacher.getId());
        teacherDTO.setTeacherId(teacher.getTeacherId());
        teacherDTO.setName(teacher.getName());
        teacherDTO.setEmail(teacher.getEmail());
        teacherDTO.setPhone(teacher.getPhone());

        model.addAttribute("teacherDTO", teacherDTO);
        return "teachers/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateTeacher(@PathVariable Long id,
                                @Valid @ModelAttribute TeacherDTO teacherDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "teachers/edit";
        }

        try {
            teacherService.updateTeacher(id, teacherDTO);
            return "redirect:/teachers";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "teachers/edit";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteTeacher(@PathVariable Long id, Model model) {
        try {
            teacherService.deleteTeacher(id);
            return "redirect:/teachers";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/teachers";
        }
    }

    @GetMapping("/{teacherId}/courses")
    @PreAuthorize("hasRole('TEACHER')")
    public String showTeacherCourses(@PathVariable Long teacherId, Model model) {
        Teacher teacher = teacherService.getTeacherById(teacherId);
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", teacher.getCourses());
        model.addAttribute("availableCourses", courseService.getAllCourses());
        return "teachers/courses";
    }

    @PostMapping("/{teacherId}/courses/assign/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String assignCourse(@PathVariable Long teacherId, @PathVariable Long courseId) {
        teacherService.assignCourseToTeacher(teacherId, courseId);
        return "redirect:/teachers/" + teacherId + "/courses";
    }

    @PostMapping("/{teacherId}/courses/remove/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String removeCourse(@PathVariable Long teacherId, @PathVariable Long courseId) {
        teacherService.removeCourseFromTeacher(teacherId, courseId);
        return "redirect:/teachers/" + teacherId + "/courses";
    }
}