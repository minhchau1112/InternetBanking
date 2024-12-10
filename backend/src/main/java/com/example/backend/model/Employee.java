package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.enums.EmployeeStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee extends User {
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;
}

