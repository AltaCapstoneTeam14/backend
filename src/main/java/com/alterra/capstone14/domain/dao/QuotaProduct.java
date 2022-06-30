package com.alterra.capstone14.domain.dao;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Table(name = "products_quota")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class QuotaProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Provider provider;

    @Column(name = "gross_amount")
    private Long grossAmount;

    @Column(name = "stock")
    private Long stock;

}