package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TransactionDetailQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailQuotaRepository extends JpaRepository<TransactionDetailQuota, Long> {
}
