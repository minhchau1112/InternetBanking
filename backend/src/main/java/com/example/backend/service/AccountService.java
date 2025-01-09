package com.example.backend.service;

import com.example.backend.dto.request.DepositRequest;
import com.example.backend.dto.response.interbank.UserDetailResponse;
import com.example.backend.enums.AccountType;
import com.example.backend.enums.FeePayer;
import com.example.backend.enums.TransactionType;
import com.example.backend.exception.AccountNotFoundException;
import com.example.backend.helper.PasswordGenerator;
import com.example.backend.model.Account;
import com.example.backend.model.Customer;
import com.example.backend.model.Transaction;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.TransactionRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
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
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    public boolean accountExistsById(Integer id) {
        return accountRepository.existsById(id);
    }

    public Customer createAccount(String username, String name, String email, String phone) {
        // check if username already exists
        if (customerRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }else if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }else if (customerRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        // create new customer since it extends user
        Customer customer = new Customer();
        customer.setUsername(username);
        // generate password
        String rawPassword = PasswordGenerator.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword); // Encode the password
        customer.setPassword(encodedPassword);

        customer.setCreatedAt(LocalDateTime.now());
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        // Save the customer first to ensure it has an ID
        customer = customerRepository.save(customer);

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

        // first time create account, return actual password
        customer.setPassword(rawPassword);

        return customer;  // Return the saved customer
    }

    private String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10)); // Appends a digit (0-9)
        }
        return accountNumber.toString();
    }

    public UserDetailResponse getUserDetailByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new IllegalArgumentException("Account does not exist"));
        Customer customer = account.getCustomer();

        UserDetailResponse response = new UserDetailResponse();
        response.setName(customer.getName());
        response.setBankCode("GROUP2");
        response.setAccountNumber(account.getAccountNumber());

        return response;
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

    public Account getAccountDetailsByAccountId(String accountId) {
//        // get customer class by username
//        Optional<Customer> customer = customerRepository.findById(Integer.valueOf(userId));
//        // get account class associated with the customer
//        if(customer.isEmpty()){
//            throw new IllegalArgumentException("User does not exist");
//        }
        Optional<Account> account = accountRepository.findById(Integer.valueOf(accountId));
        if(account.isEmpty()){
            throw new IllegalArgumentException("Account does not exist");
        }
        return account.get();
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
            if (!customerRepository.existsByUsername(depositRequest.getUsername())) {
                throw new IllegalArgumentException("User does not exist");
            }
            Customer customer = customerRepository.findByUsername(depositRequest.getUsername());
            account = accountRepository.findByCustomerId(customer.getId());
        }
        else {
            throw new IllegalArgumentException("Account number or username is required");
        }

        Account newAccount = null;
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

    public boolean checkIfAccountExistedByAccountNumber(String recipientAccountNumber) {
        return accountRepository.existsByAccountNumber(recipientAccountNumber);
    }

    public Account findAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    public Account getAccountByAccountNumber(String accountNumber) throws AccountNotFoundException {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }
}
