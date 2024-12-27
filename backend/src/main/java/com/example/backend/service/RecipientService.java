package com.example.backend.service;

import com.example.backend.dto.request.RecipientCreateRequest;
import com.example.backend.dto.request.RecipientUpdateRequest;
import com.example.backend.model.Customer;
import com.example.backend.model.Recipient;
import com.example.backend.repository.CustomerRepository;
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

    public List<Recipient> findByCustomer(Integer customerId) {
        return recipientRepository.findByCustomerId(customerId);
    }

    public Recipient saveRecipient(RecipientCreateRequest createRequest) {

        Customer customer = customerRepository.findById(createRequest.getCustomerId()).get();

        String aliasName = (!Objects.equals(createRequest.getAliasName(), null)) ? createRequest.getAliasName() : customer.getName();

        Recipient recipient = Recipient.builder()
                .customer(customer)
                .accountNumber(createRequest.getAccountNumber())
                .aliasName(aliasName)
                .bankCode(createRequest.getBankCode())
                .createdAt(LocalDateTime.now())
                .build();

        System.out.println(recipient.getAccountNumber());
        return recipientRepository.save(recipient);
    }

    public Recipient updateRecipient(RecipientUpdateRequest updateRequest) {

        Recipient existingRecipient = recipientRepository.findById(updateRequest.getRecipientId()).get();

        String aliasName = (!Objects.equals(updateRequest.getAliasName(), null)) ?
                updateRequest.getAliasName() : existingRecipient.getCustomer().getName();

        existingRecipient.setAliasName(aliasName);
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
