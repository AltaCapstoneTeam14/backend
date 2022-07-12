package com.alterra.capstone14.domain.dao;

import com.alterra.capstone14.constant.ERole;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private ERole name;
}
