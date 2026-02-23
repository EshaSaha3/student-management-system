package com.example.student_management_system.service;

import com.example.student_management_system.dto.DepartmentDTO;
import com.example.student_management_system.entity.Department;
import com.example.student_management_system.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Department not found with name: " + name));
    }

    @Transactional
    public Department createDepartment(DepartmentDTO departmentDTO) {
        // Check if department already exists
        if (departmentRepository.findByName(departmentDTO.getName()).isPresent()) {
            throw new RuntimeException("Department with name " + departmentDTO.getName() + " already exists");
        }

        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());

        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department department = getDepartmentById(id);

        // Check if new name conflicts with existing department
        if (!department.getName().equals(departmentDTO.getName()) &&
                departmentRepository.findByName(departmentDTO.getName()).isPresent()) {
            throw new RuntimeException("Department with name " + departmentDTO.getName() + " already exists");
        }

        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());

        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);

        // Check if department has students
        if (department.getStudents() != null && !department.getStudents().isEmpty()) {
            throw new RuntimeException("Cannot delete department with assigned students");
        }

        departmentRepository.deleteById(id);
    }

    public long getDepartmentCount() {
        return departmentRepository.count();
    }
}