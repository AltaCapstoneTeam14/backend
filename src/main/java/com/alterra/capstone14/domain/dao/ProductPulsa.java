package com.alterra.capstone14.domain.dao;

import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@Table(name= "product_pulsa")
@Entity
public class ProductPulsa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pulsa_id")
    private Long pulsaId;

    @Column(name = "denom")
    private int denom;

    @Column(name = "price")
    private int price;

    @Column(name = "name")
    private String name;

    @Column(name = "stock")
    private int stock;
}
