package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TransactionDetailTopup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionDetailTopupRepository extends JpaRepository<TransactionDetailTopup, Long> {
    Optional<TransactionDetailTopup> findByTransactionDetailId(Long transactionDetailId);
}
