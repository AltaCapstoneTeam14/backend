package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.CashoutProduct;
import com.alterra.capstone14.domain.dao.PulsaProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashoutProductRepository extends JpaRepository<CashoutProduct, Long> {
    @Query("SELECT p FROM CashoutProduct p ORDER BY p.balanceAmount ASC")
    public List<CashoutProduct> findAllSorted();
}
