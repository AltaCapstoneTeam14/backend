package com.alterra.capstone14.domain.dao;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Table(name = "products_topup")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TopupProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "gross_amount", nullable = false)
    private Long grossAmount;

}
