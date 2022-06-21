package com.alterra.capstone14.domain.dao;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Table(name = "topup_amount_list")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TopupAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

}
