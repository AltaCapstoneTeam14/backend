package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TransactionHistoryCashout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryCashoutRepository extends JpaRepository<TransactionHistoryCashout, Long> {
}
