package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    Optional<Coin> findByUserId(Long id);
    Boolean existsByUserId(Long id);
}
