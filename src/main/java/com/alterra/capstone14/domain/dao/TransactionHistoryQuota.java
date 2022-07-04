package com.alterra.capstone14.domain.dao;

import com.alterra.capstone14.domain.common.BaseCreatedAt;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_history_quota")
public class TransactionHistoryQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "phone", nullable = false)
    private String phone;
}
