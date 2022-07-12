package com.alterra.capstone14.domain.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_details_topup")
public class TransactionDetailTopup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "json_notification")
    private String jsonNotification;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "transaction_detail_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TransactionDetail transactionDetail;
}
