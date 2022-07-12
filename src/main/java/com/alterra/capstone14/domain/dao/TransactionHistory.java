package com.alterra.capstone14.domain.dao;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_history")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "transaction_detail_id", nullable = false)
    private Long transactionDetailId;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "product_history_id", nullable = false)
    private Long productHistoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "gross_amount", nullable = false)
    private Long grossAmount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "transfer_method", nullable = false)
    private String transferMethod;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
