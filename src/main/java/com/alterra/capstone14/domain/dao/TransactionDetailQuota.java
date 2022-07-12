package com.alterra.capstone14.domain.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_details_quota")
public class TransactionDetailQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone")
    private String phone;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "transaction_detail_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TransactionDetail transactionDetail;
}
