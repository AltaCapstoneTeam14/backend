package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TransactionHistory;
import com.alterra.capstone14.domain.dao.TransactionHistoryTopup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryTopupRepository extends JpaRepository<TransactionHistoryTopup, Long> {
}
