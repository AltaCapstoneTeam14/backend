package com.alterra.capstone14.domain.dao;

import com.alterra.capstone14.domain.common.BaseIsDeleted;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update users set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class User extends BaseIsDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<TransactionDetail> topupDetails;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private Balance balance;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private Coin coin;
}
