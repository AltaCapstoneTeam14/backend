package com.alterra.capstone14.domain.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_details_pulsa")
public class TransactionDetailPulsa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "transaction_detail_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TransactionDetail transactionDetail;
}
