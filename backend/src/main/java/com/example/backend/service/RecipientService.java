package com.example.backend.service;

import com.example.backend.dto.RecipientCreateRequest;
import com.example.backend.dto.RecipientUpdateRequest;
import com.example.backend.dto.response.GetRecipientsResponse;
import com.example.backend.exception.NotFoundException;
import com.example.backend.model.Account;
import com.example.backend.model.Customer;
import com.example.backend.model.Recipient;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipientService {

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Recipient> findByCustomer(Integer customerId) {
        return recipientRepository.findByCustomerId(customerId);
    }
    public List<GetRecipientsResponse> getRecipientsByCustomerId(Integer customerId) {
        return recipientRepository.getRecipientsByCustomerId(customerId);
    }
    public Recipient saveRecipient(RecipientCreateRequest createRequest) {

        Customer customer = customerRepository.findById(createRequest.getCustomerId()).get();

        Recipient recipient = Recipient.builder()
                .accountNumber(createRequest.getAccountNumber())
                .aliasName(createRequest.getAliasName())
                .bankCode(createRequest.getBankCode())
                .build();

        return recipientRepository.save(recipient);
    }

    public Recipient updateRecipient(RecipientUpdateRequest updateRequest) {

        Recipient existingRecipient = recipientRepository.findById(updateRequest.getRecipientId()).get();
        existingRecipient.setAliasName(updateRequest.getAliasName());
        existingRecipient.setBankCode(updateRequest.getBankCode());
        existingRecipient.setAccountNumber(updateRequest.getAccountNumber());

        return recipientRepository.save(existingRecipient);
    }

    public void delete(Integer id) {
        Recipient recipient = recipientRepository.findById(id).get();
        recipientRepository.delete(recipient);
    }

    public boolean customerExistsById(Integer id) {
        return customerRepository.existsById(id);
    }

    public boolean recipientExistsById(Integer id) {
        return recipientRepository.existsById(id);
    }

}
