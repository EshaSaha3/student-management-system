package com.example.student_management_system.controller;

import com.example.student_management_system.dto.DepartmentDTO;
import com.example.student_management_system.entity.Department;
import com.example.student_management_system.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "departments/list";
    }

    @GetMapping("/{id}")
    public String viewDepartment(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        return "departments/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("departmentDTO", new DepartmentDTO());
        return "departments/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String createDepartment(@Valid @ModelAttribute DepartmentDTO departmentDTO,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "departments/create";
        }

        try {
            departmentService.createDepartment(departmentDTO);
            return "redirect:/departments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "departments/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setId(department.getId());
        departmentDTO.setName(department.getName());
        departmentDTO.setDescription(department.getDescription());

        model.addAttribute("departmentDTO", departmentDTO);
        return "departments/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateDepartment(@PathVariable Long id,
                                   @Valid @ModelAttribute DepartmentDTO departmentDTO,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "departments/edit";
        }

        try {
            departmentService.updateDepartment(id, departmentDTO);
            return "redirect:/departments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "departments/edit";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteDepartment(@PathVariable Long id, Model model) {
        try {
            departmentService.deleteDepartment(id);
            return "redirect:/departments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/departments";
        }
    }
}