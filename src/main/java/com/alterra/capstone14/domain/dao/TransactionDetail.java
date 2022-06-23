package com.alterra.capstone14.domain.dao;

import com.alterra.capstone14.constant.EProductType;
import com.alterra.capstone14.domain.common.BaseCreatedAt;
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
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "transaction_details")
public class TransactionDetail extends BaseCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User user;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "product_type", nullable = false)
//    @Enumerated(EnumType.STRING)
    private String productType;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "gross_amount", nullable = false)
    private Long grossAmount;

    @Column(name = "payment_type", nullable = false)
    //    @Enumerated(EnumType.STRING)
    private String paymentType;

    @Column(name = "transfer_method", nullable = false)
    //    @Enumerated(EnumType.STRING)
    private String transferMethod;

    @Column(name = "status", nullable = false)
    private String status;
    
    @Lob
    @Column(name = "json_notification")
    private String jsonNotification;
}
