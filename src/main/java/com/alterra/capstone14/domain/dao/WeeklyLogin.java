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
@Table(name = "weekly_login")
public class WeeklyLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User user;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "login_count", nullable = false)
    private Integer loginCount;

    @Column(name = "last_login", nullable = false)
    private Integer lastLogin;
}
