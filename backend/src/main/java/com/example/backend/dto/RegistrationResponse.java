package com.example.backend.dto;

import com.example.backend.model.Account;
import com.example.backend.model.Customer;
import com.example.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationResponse {
    private User user;
    private Customer customer;
    private Account account;
}
