package com.alterra.capstone14.domain.dao;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Table(name = "subscribers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

}
