package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {
    @Modifying
    @Query("DELETE FROM ForgotPasswordToken t WHERE t.email=?1")
    void deleteByEmail(String email);

    Optional<ForgotPasswordToken> findByToken(UUID token);
}
