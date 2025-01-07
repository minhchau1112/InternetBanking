package com.example.backend.service;

import com.example.backend.dto.response.interbank.DepositInterbankRequest;
import com.example.backend.enums.InterbankTransactionStatus;
import com.example.backend.model.Account;
import com.example.backend.model.InterbankTransaction;
import com.example.backend.repository.InterbankTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InterbankService {

    @Autowired
    private InterbankTransactionRepository interbankTransactionRepository;

    @Autowired
    private AccountService accountService;

    public InterbankService() {
    }

    public InterbankService(InterbankTransactionRepository interbankTransactionRepository) {
        this.interbankTransactionRepository = interbankTransactionRepository;
    }

    public InterbankTransaction createTransaction(DepositInterbankRequest payload) {
        InterbankTransaction transaction = new InterbankTransaction();

        // Check if this is an incoming transaction (recipient account belongs to your bank)
        boolean isIncoming = accountService.checkIfAccountExistedByAccountNumber(payload.getRecipientAccountNumber());
        // if account exists in my bank => incoming transaction and add balance to the account
        // if account does not exist in my bank => outgoing transaction and deduct balance from the account
        // find the account by account number
        if(isIncoming){ // outside bank to my bank
            Account account = accountService.findAccountByAccountNumber(payload.getRecipientAccountNumber());
            if("sender".equalsIgnoreCase(payload.getFeePayer())){ // If sender pays the fee
                // Deduct the amount from the account
                account.setBalance(account.getBalance().add(BigDecimal.valueOf(payload.getAmount())));
            } else { // if recipient pays the fee then minus the fee from the amount
                // Deduct the amount from the account
                account.setBalance(account.getBalance().add(BigDecimal.valueOf(payload.getAmount() - payload.getFeeAmount())));
            }
        }
        else{ // my bank to outside bank
            Account account = accountService.findAccountByAccountNumber(payload.getSenderAccountNumber());
            if("sender".equalsIgnoreCase(payload.getFeePayer())){ // If sender pays the fee, sender here is
                // in my bank
                // Deduct the amount from the account and fee
                account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(payload.getAmount() + payload.getFeeAmount())));
            } else { // if recipient pays the fee then minus the fee from the amount
                // Deduct the amount from the account
                account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(payload.getAmount())));
            }
        }


        transaction.setFeeAmount(BigDecimal.valueOf(payload.getFeeAmount()));
        // Set common fields
        if("sender".equals(payload.getFeePayer())){ // If sender pays the fee
            transaction.setAmount(BigDecimal.valueOf(payload.getAmount()));
        } else { // if recipient pays the fee then minus the fee from the amount
            transaction.setAmount(BigDecimal.valueOf(payload.getAmount() - payload.getFeeAmount()));

        }
        transaction.setDescription(payload.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus(InterbankTransactionStatus.PENDING); // Assume PENDING status initially
        transaction.setFeePayer(payload.getFeePayer());

        if (isIncoming) {
            // Incoming transaction: your bank is the recipient
            Account destinationAccount = accountService.findAccountByAccountNumber(payload.getRecipientAccountNumber());
            if (destinationAccount == null) {
                throw new IllegalArgumentException("Recipient account not found in your bank.");
            }
            transaction.setDestinationAccount(destinationAccount);
            transaction.setExternalAccountNumber(payload.getSenderAccountNumber());
            transaction.setExternalBankCode(payload.getSenderBankCode());
            transaction.setIncoming(true); // Mark as incoming transaction
        } else {
            // Outgoing transaction: your bank is the sender
            Account sourceAccount =
                    accountService.findAccountByAccountNumber(payload.getSenderAccountNumber());
            if (sourceAccount == null) {
                throw new IllegalArgumentException("Sender account not found in your bank.");
            }
            transaction.setSourceAccount(sourceAccount);
            transaction.setExternalAccountNumber(payload.getRecipientAccountNumber());
            transaction.setExternalBankCode(payload.getSenderBankCode());
            transaction.setIncoming(false); // Mark as outgoing transaction
        }

        // Save the transaction
        interbankTransactionRepository.save(transaction);

        return transaction;
    }

}