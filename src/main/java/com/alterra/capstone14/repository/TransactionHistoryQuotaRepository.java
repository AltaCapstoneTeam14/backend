package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TransactionHistory;
import com.alterra.capstone14.domain.dao.TransactionHistoryQuota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryQuotaRepository extends JpaRepository<TransactionHistoryQuota, Long> {
}
