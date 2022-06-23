package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByUserId(Long id);
}
