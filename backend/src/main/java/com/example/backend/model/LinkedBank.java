package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Entity
@Table(name = "linked_banks")
public class LinkedBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String theirPublicKey;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String publicKey;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String privateKey;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

