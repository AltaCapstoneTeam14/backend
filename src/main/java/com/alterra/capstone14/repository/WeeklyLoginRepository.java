package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.WeeklyLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeeklyLoginRepository extends JpaRepository<WeeklyLogin, Long> {
    @Query("SELECT p FROM WeeklyLogin p WHERE p.user.id=?1 AND p.startDate=?2")
    Optional<WeeklyLogin> findByUserAndStartDate(Long id, String startDate);
}
