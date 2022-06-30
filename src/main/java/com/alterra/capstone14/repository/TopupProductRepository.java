package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.TopupProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopupProductRepository extends JpaRepository<TopupProduct, Long> {
    @Query("SELECT p FROM TopupProduct p ORDER BY p.amount ASC")
    public List<TopupProduct> findAllSorted();
}
