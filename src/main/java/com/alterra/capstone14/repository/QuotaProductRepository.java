package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.PulsaProduct;
import com.alterra.capstone14.domain.dao.QuotaProduct;
import com.alterra.capstone14.domain.dao.TopupProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotaProductRepository extends JpaRepository<QuotaProduct, Long> {
    @Query("SELECT p FROM QuotaProduct p ORDER BY p.grossAmount ASC")
    public List<QuotaProduct> findAllSorted();
}
