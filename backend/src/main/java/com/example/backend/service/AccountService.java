package com.example.backend.service;

import com.example.backend.dto.request.DepositRequest;
import com.example.backend.enums.AccountType;
import com.example.backend.enums.FeePayer;
import com.example.backend.enums.TransactionType;
import com.example.backend.helper.PasswordGenerator;
import com.example.backend.model.Account;
import com.example.backend.model.Customer;
import com.example.backend.model.Transaction;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.TransactionRepository;
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

    @Autowired
    private TransactionRepository transactionRepository;

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

    public Account getAccountDetails(String accountNumber) {
        // also return error if account does not exist
        if (!accountRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("Account does not exist");
        }
        return accountRepository.findByAccountNumber((accountNumber)).get();
    }

    public Account getAccountDetailsByUsername(String username) {
        // get customer class by username
        Customer customer = customerRepository.findByUsername(username);
        // get account class associated with the customer
        return accountRepository.findByCustomerId(customer.getId()).get();
    }

    public Transaction deposit(DepositRequest depositRequest){
        Optional<Account> account = null;
        if (depositRequest.getAccountNumber() != null) {
            if (!accountRepository.existsByAccountNumber(depositRequest.getAccountNumber())) {
                throw new IllegalArgumentException("Account does not exist");
            }
            account = accountRepository.findByAccountNumber(depositRequest.getAccountNumber());
        }
        else if (depositRequest.getUsername() != null) {
            if (!userRepository.existsByUsername(depositRequest.getUsername())) {
                throw new IllegalArgumentException("User does not exist");
            }
            // get the first customer class of the user
            Customer customer = customerRepository.findByUsername(depositRequest.getUsername());
            // get account class associated with the customer
            account = accountRepository.findByCustomerId(customer.getId());
        }
        else {
            throw new IllegalArgumentException("Account number or username is required");
        }

        Account newAccount = null;
        // check if account is present
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account does not exist");
        }
        else{
            // cast account to Account class
            newAccount = account.get();
        }

        // create transaction
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(newAccount);
        transaction.setDestinationAccount(newAccount);
        transaction.setBankCode(null);
        transaction.setAmount(BigDecimal.valueOf(depositRequest.getDepositAmount()));
        transaction.setFee(BigDecimal.valueOf(0.0));
        transaction.setFeePayer(FeePayer.SENDER);
        transaction.setMessage("Deposit");
        transaction.setStatus("COMPLETED");
        transaction.setOtpVerified(true);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCompletedAt(LocalDateTime.now());
        // save transaction
        transactionRepository.save(transaction);

        // update account balance
//        System.out.println("account balance before: " + account.getBalance());
        newAccount.setBalance(newAccount.getBalance().add(transaction.getAmount()));
        accountRepository.save(newAccount);
//        System.out.println("account balance after: " + account.getBalance());

        return transaction;

    }
    public Optional<Account> findByCustomerId(Integer customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

}
