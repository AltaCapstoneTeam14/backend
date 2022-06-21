package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceService {
    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<Object> activateBalance() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Balance balance = Balance.builder()
                .user(user.get())
                .amount(0L)
                .build();

        balanceRepository.save(balance);
        UserDto userDto = UserDto.builder()
                .build();

        return Response.build(Response.delete("user"), null, null, HttpStatus.CREATED);
    }
}
