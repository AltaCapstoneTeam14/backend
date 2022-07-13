package com.alterra.capstone14.domain.dao;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@Table(name = "forgot_password_token")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "token", nullable = false, unique = true)
    private UUID token;


    @PrePersist
    void onCreate(){
        this.token = UUID.randomUUID();
    }
}
