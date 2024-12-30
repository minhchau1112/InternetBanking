package com.example.backend.controller;

import com.example.backend.model.Transaction;
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
    public List<Transaction> getTransactions(
            @RequestParam(value = "accountId") String accountId,
            @RequestParam(value = "destinationAccountId", required = false) String destinationAccountId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        Integer srcAccountId = Integer.parseInt(accountId);
        Integer destAccountId = (destinationAccountId != null && !destinationAccountId.isEmpty())
                ? Integer.parseInt(destinationAccountId) : null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = (startDate != null && !startDate.isEmpty())
                ? LocalDateTime.parse(startDate + "T00:00:00", formatter) : null;
        LocalDateTime end = (endDate != null && !endDate.isEmpty())
                ? LocalDateTime.parse(endDate + "T23:59:59", formatter) : null;

        System.out.println("accountId: " + srcAccountId + ", destinationAccountId: " + destAccountId + ", startDate: " + start + ", endDate: " + end);

        return transactionService.getTransactions(srcAccountId, destAccountId, start, end);
    }
}
