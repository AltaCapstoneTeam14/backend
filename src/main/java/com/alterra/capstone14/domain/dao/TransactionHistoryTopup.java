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
@Table(name = "transaction_history_topup")
public class TransactionHistoryTopup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "json_notification", nullable = false)
    private String jsonNotification;
}
