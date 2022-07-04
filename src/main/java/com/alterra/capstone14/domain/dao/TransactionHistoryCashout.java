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
@Table(name = "transaction_history_cashout")
public class TransactionHistoryCashout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance_amount", nullable = false)
    private Long balanceAmount;
}
