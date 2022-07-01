package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.Coin;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.*;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.CoinRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    CoinRepository coinRepository;

    public ResponseEntity<Object> getProfile() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<Balance> balance = balanceRepository.findByUserId(user.get().getId());
        if(balance.isEmpty()){
            Balance newBalance = Balance.builder().user(user.get()).amount(0L).build();
            balanceRepository.save(newBalance);
            balance = Optional.of(newBalance);
        }

        Optional<Coin> coin = coinRepository.findByUserId(user.get().getId());
        if(coin.isEmpty()){
            Coin newCoin = Coin.builder().user(user.get()).amount(0L).build();
            coinRepository.save(newCoin);
            coin = Optional.of(newCoin);
        }

        UserWithBalanceDto userDto = UserWithBalanceDto.builder()
                .id(user.get().getId())
                .name(user.get().getName())
                .email(user.get().getEmail())
                .phone(user.get().getPhone())
                .balance(BalanceDto.builder()
                        .id(balance.get().getId())
                        .amount(balance.get().getAmount())
                        .build())
                .coin(CoinDto.builder()
                        .id(coin.get().getId())
                        .amount(coin.get().getAmount())
                        .build())
                .createdAt(user.get().getCreatedAt())
                .updatedAt(user.get().getUpdatedAt())
                .build();

        return Response.build(Response.get("user data"), userDto, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> updateUserData(UserNoPwdDto userDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        user.get().setName(userDto.getName());

        if(!user.get().getPhone().equals(userDto.getPhone())){
            if (Boolean.TRUE.equals(userRepository.existsByPhone(userDto.getPhone()))) {
                return Response.build(Response.exist("user", "phone", userDto.getPhone()), null, null, HttpStatus.BAD_REQUEST);
            }
            user.get().setPhone(userDto.getPhone());
        }

        if(!user.get().getEmail().equals(userDto.getEmail())){
            if (Boolean.TRUE.equals(userRepository.existsByEmail(userDto.getEmail()))) {
                return Response.build(Response.exist("user", "email", userDto.getEmail()), null, null, HttpStatus.BAD_REQUEST);
            }
            user.get().setEmail(userDto.getEmail());
        }
        userRepository.save(user.get());

        UserNoPwdDto userNoPwdDto = UserNoPwdDto.builder()
                .id(user.get().getId())
                .name(user.get().getName())
                .email(user.get().getEmail())
                .phone(user.get().getPhone())
                .createdAt(user.get().getCreatedAt())
                .build();

        return Response.build(Response.update("user"), userNoPwdDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> updateUserPassword(PasswordDto passwordDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        user.get().setPassword(encoder.encode(passwordDto.getPassword()));
        userRepository.save(user.get());

        UserNoPwdDto userNoPwdDto = UserNoPwdDto.builder()
                .id(user.get().getId())
                .name(user.get().getName())
                .email(user.get().getEmail())
                .phone(user.get().getPhone())
                .createdAt(user.get().getCreatedAt())
                .build();

        return Response.build(Response.update("password"), userNoPwdDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        userRepository.deleteById(user.get().getId());

        return Response.build(Response.delete("user"), null, null, HttpStatus.CREATED);
    }
}
