package com.example.backend.service;

import com.example.backend.enums.AccountType;
import com.example.backend.helper.PasswordGenerator;
import com.example.backend.model.Account;
import com.example.backend.model.Customer;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final SecureRandom random = new SecureRandom();

    public boolean accountExistsById(Integer id) {
        return accountRepository.existsById(id);
    }

    public Customer createAccount(String username, String name, String email, String phone) {
        // check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        // create new customer since it extends user
        Customer customer = new Customer();
        customer.setUsername(username);
        // generate password
        String password = PasswordGenerator.generateRandomPassword();
        customer.setPassword(password);

        customer.setCreatedAt(LocalDateTime.now());
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        // Save the customer first to ensure it has an ID
        customer = userRepository.save(customer);

        // now create and save the associated account
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(0.0));
        account.setCreatedAt(LocalDateTime.now());
        account.setCustomer(customer);  // Reference the already persisted customer
        account.setIsPrimary(true);
        account.setType(AccountType.CHECKING);
        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        account.setAccountNumber(accountNumber);

        accountRepository.save(account);

        return customer;  // Return the saved customer
    }

    private String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10)); // Appends a digit (0-9)
        }
        return accountNumber.toString();
    }
    public Optional<Account> findByCustomerId(Integer customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

}
