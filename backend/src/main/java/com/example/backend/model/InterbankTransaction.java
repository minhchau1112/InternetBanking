package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.enums.InterbankTransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Entity
@Table(name = "interbank_transactions")
public class InterbankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @Column(nullable = false)
    private String destinationAccountNumber;

    @Column(nullable = false)
    private String destinationBankCode;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterbankTransactionStatus status;

    @Column(nullable = false)
    private String requestSignature;

    @Column(nullable = false)
    private String responseSignature;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;
}

