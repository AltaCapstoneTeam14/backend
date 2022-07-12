package com.alterra.capstone14.repository;

import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.dao.Role;
import com.alterra.capstone14.domain.dao.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Long> {
    Optional<TransactionDetail> findByOrderId(String orderId);
}
