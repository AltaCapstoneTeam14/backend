package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dao.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    Optional<TransactionHistory> findByOrderId(String orderId);

    @Query("SELECT p FROM TransactionHistory p WHERE p.userId=?1 ORDER BY p.createdAt DESC")
    List<TransactionHistory> findByUserIdSorted(Long userId);
}
