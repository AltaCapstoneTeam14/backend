package com.alterra.capstone14.domain.dao;

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
@Table(name = "topup_details")
public class TopupDetail extends BaseCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User user;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "gross_amount", nullable = false)
    private Integer grossAmount;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "transfer_method", nullable = true)
    private String transferMethod;

    @Column(name = "status", nullable = true)
    private String status;
    
    @Lob
    @Column(name = "additional_data")
    private String additionalData;
}
