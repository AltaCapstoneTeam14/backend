package com.alterra.capstone14.domain.dao;

import com.alterra.capstone14.domain.common.BaseCreatedAt;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_history_pulsa")
public class TransactionHistoryPulsa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "denom", nullable = false)
    private Long denom;

    @Column(name = "phone", nullable = false)
    private String phone;
}
