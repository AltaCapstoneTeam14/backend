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
@Table(name = "products_pulsa")
public class PulsaProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Provider provider;

    @Column(name = "denom")
    private Long denom;

    @Column(name = "gross_amount")
    private Long grossAmount;

    @Column(name = "stock")
    private Long stock;

}
