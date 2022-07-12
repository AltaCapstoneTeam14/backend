package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TransactionHistory;
import com.alterra.capstone14.domain.dao.TransactionHistoryPulsa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryPulsaRepository extends JpaRepository<TransactionHistoryPulsa, Long> {
}
