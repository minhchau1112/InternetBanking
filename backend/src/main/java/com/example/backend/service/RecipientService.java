package com.example.backend.service;

import com.example.backend.dto.request.RecipientCreateRequest;
import com.example.backend.dto.request.RecipientUpdateRequest;
import com.example.backend.model.Account;
import com.example.backend.model.Customer;
import com.example.backend.model.LinkedBank;
import com.example.backend.model.Recipient;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.LinkedBankRepository;
import com.example.backend.repository.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class RecipientService {

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LinkedBankRepository linkedBankRepository;

    public List<Recipient> findByCustomer(Integer customerId) {
        return recipientRepository.findByCustomerId(customerId);
    }

    public Recipient saveRecipient(RecipientCreateRequest createRequest) {

        Customer customer = accountRepository.findById(createRequest.getCustomerId()).get().getCustomer();

        Account account = accountRepository.findByAccountNumber(createRequest.getAccountNumber()).get();

        String aliasName = (!createRequest.getAliasName().isEmpty()) ?
                createRequest.getAliasName() : account.getCustomer().getName();

        Recipient recipient = Recipient.builder()
                .customer(customer)
                .accountNumber(createRequest.getAccountNumber())
                .aliasName(aliasName)
                .bankCode(createRequest.getBankCode())
                .createdAt(LocalDateTime.now())
                .build();

        return recipientRepository.save(recipient);
    }

    public Recipient updateRecipient(RecipientUpdateRequest updateRequest) {

        Recipient existingRecipient = recipientRepository.findById(updateRequest.getRecipientId()).get();

        if(updateRequest.getAliasName().isEmpty()){
            existingRecipient.setAliasName(updateRequest.getAliasName());
        } else {
            String aliasName = accountRepository.findByAccountNumber(updateRequest.getAccountNumber()).get()
                    .getCustomer().getName();
            existingRecipient.setAliasName(aliasName);
        }

        existingRecipient.setBankCode(updateRequest.getBankCode());
        existingRecipient.setAccountNumber(updateRequest.getAccountNumber());

        return recipientRepository.save(existingRecipient);
    }

    public void deleteRecipient(Integer id) {
        Recipient recipient = recipientRepository.findById(id).get();
        recipientRepository.delete(recipient);
    }
}
