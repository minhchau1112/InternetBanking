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

    // Indicate whether the transaction is incoming or outgoing
    @Column(nullable = false)
    private boolean isIncoming;

    // For outgoing transactions, this is the source account in your bank
    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = true)
    private Account sourceAccount;

    // For incoming transactions, this is the destination account in your bank
    @ManyToOne
    @JoinColumn(name = "destination_account_id", nullable = true)
    private Account destinationAccount;

    @Column
    private String externalAccountNumber; // Can be source or destination based on isIncoming

    @Column
    private String externalBankCode; // Can be source or destination based on isIncoming

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterbankTransactionStatus status;

    @Column(nullable = true)
    private String requestSignature;

    @Column(nullable = true)
    private String responseSignature;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private BigDecimal feeAmount;

    @Column
    private String description;

    @Column
    private String feePayer = "SENDER"; // E.g., "sender" or "receiver"
}
