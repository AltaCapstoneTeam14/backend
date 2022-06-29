package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TransactionDetailPulsa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailPulsaRepository extends JpaRepository<TransactionDetailPulsa, Long> {
}
