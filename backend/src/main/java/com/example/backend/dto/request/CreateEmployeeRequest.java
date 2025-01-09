package com.example.backend.dto.request;

import com.example.backend.enums.EmployeeStatus;
import lombok.Data;

@Data
public class CreateEmployeeRequest {
    private String userName;
    private String name;
    private EmployeeStatus status;
}
