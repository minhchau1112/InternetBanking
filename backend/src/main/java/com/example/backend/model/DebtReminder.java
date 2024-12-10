package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.backend.enums.DebtReminderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Entity
@Table(name = "debt_reminders")
public class DebtReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "creator_account_id", nullable = false)
    private Account creatorAccount;

    @ManyToOne
    @JoinColumn(name = "debtor_account_id", nullable = false)
    private Account debtorAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtReminderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

