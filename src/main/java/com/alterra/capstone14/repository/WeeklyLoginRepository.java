package com.alterra.capstone14.repository;

import com.alterra.capstone14.domain.dao.WeeklyLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeeklyLoginRepository extends JpaRepository<WeeklyLogin, Long> {
    Optional<WeeklyLogin> findByStartDate(String startDate);
}
