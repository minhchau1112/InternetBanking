package com.example.backend.service;

import com.example.backend.exception.EmailNotFoundException;
import com.example.backend.model.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    public CustomerService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    public void resetPassword(String email, String newPassword) throws EmailNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        if (optionalCustomer.isEmpty()) {
            throw new EmailNotFoundException("Email không tồn tại.");
        }

        Customer customer = optionalCustomer.get();
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }
    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }
    public boolean updatePassword(String username, String newPassword) {
        Customer customer = customerRepository.findByUsername(username);

        if (customer != null) {
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerRepository.save(customer);
            return true;
        }

        return false;
    }

}
