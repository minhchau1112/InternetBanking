package com.example.backend.service;

import com.example.backend.dto.request.OtpVerificationRequest;
import com.example.backend.dto.response.interbank.DepositInterbankRequest;
import com.example.backend.enums.InterbankTransactionStatus;
import com.example.backend.model.Account;
import com.example.backend.model.InterbankTransaction;
import com.example.backend.model.Transaction;
import com.example.backend.repository.InterbankTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class InterbankService {

    @Autowired
    private InterbankTransactionRepository interbankTransactionRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    private final Map<Integer, String> otpCache = new HashMap<>();

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
        String otp = String.valueOf((int) ((Math.random() * 9000) + 1000)); // Generate 4-digit OTP
        otpCache.put(transaction.getId(),otp);
        // get email from source account if incoming transaction, else dont sent otp
        if(!isIncoming){
            String email = transaction.getSourceAccount().getCustomer().getEmail();
            Map<String, Object> variables = new HashMap<>();
            variables.put("OTP_CODE", otp);
            emailService.sendEmailFromTemplateSync(email, "Transaction OTP", "otp", variables);
        }

        return transaction;
    }

    public boolean verifyOtpAndCompleteTransaction(OtpVerificationRequest otpRequest) {
        String cachedOtp = otpCache.get(otpRequest.getTransactionId());
        if (cachedOtp != null && cachedOtp.equals(otpRequest.getOtp())) {
            // Hoàn tất giao dịch
            InterbankTransaction transaction = interbankTransactionRepository.findById(otpRequest.getTransactionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

            transaction.setStatus(InterbankTransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            interbankTransactionRepository.save(transaction);

            // Xóa OTP khỏi cache
            otpCache.remove(otpRequest.getTransactionId());
            return true;
        }
        return false;
    }

}
