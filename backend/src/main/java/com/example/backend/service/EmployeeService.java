package com.example.backend.service;

import com.example.backend.dto.request.CreateEmployeeRequest;
import com.example.backend.helper.PasswordGenerator;
import com.example.backend.model.Employee;
import com.example.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        Employee employee = new Employee();

        String rawPassword = PasswordGenerator.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword); // Encode the password
        employee.setPassword(encodedPassword);

        employee.setName(request.getName());
        employee.setUsername(request.getUserName());
        employee.setStatus(request.getStatus());
        employee.setCreatedAt(LocalDateTime.now());

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + id));
        employee.setName(employeeDetails.getName());
        employee.setStatus(employeeDetails.getStatus());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
