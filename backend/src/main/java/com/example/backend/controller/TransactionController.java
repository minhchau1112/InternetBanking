package com.example.backend.controller;

import com.example.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.Integer.parseInt;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<?> getTransactions(
            @RequestParam(value = "accountId") String accountId,
            @RequestParam(value = "destinationAccountNumber", required = false) String destinationAccountNumber,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        try {
            Integer srcAccountId = Integer.parseInt(accountId);
            String desAccountNum = (destinationAccountNumber != null && !destinationAccountNumber.isEmpty()) ? destinationAccountNumber : null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime start = (startDate != null && !startDate.isEmpty())
                    ? LocalDateTime.parse(startDate + "T00:00:00", formatter) : null;
            LocalDateTime end = (endDate != null && !endDate.isEmpty())
                    ? LocalDateTime.parse(endDate + "T23:59:59", formatter) : null;

            System.out.println("accountId: " + srcAccountId + ", destinationAccountNumber: " + desAccountNum + ", startDate: " + start + ", endDate: " + end);

            if ("interbank".equals(type)) {
                return transactionService.getInterbankTransactionsWithBankName(srcAccountId, desAccountNum, start, end);
            } else {
                return transactionService.getTransactions(srcAccountId, desAccountNum, start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing request: " + e.getMessage());
        }
    }
}
