package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.Coin;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.*;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.CoinRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    CoinRepository coinRepository;

    public ResponseEntity<Object> getAllUser(int page, int count) {
        if(page<1){
            return Response.build("page must not be less than 1", null, null, HttpStatus.BAD_REQUEST);
        }
        if(count<1){
            return Response.build("count must not be less than 1", null, null, HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(page-1, count);
        Page<User> userList = userRepository.findAll(pageable);
        List<User> users = userList.getContent();
        List<UserWithBalanceDto> userWithBalanceDtos = new ArrayList<>();

        users.forEach(user -> {
            if(Boolean.FALSE.equals(balanceRepository.existsByUserId(user.getId()))){
                Balance newBalance = Balance.builder().user(user).amount(0L).build();
                balanceRepository.save(newBalance);
                user.setBalance(newBalance);
            }

            if(Boolean.FALSE.equals(coinRepository.existsByUserId(user.getId()))){
                Coin newCoin = Coin.builder().user(user).amount(0L).build();
                coinRepository.save(newCoin);
                user.setCoin(newCoin);
            }

            userWithBalanceDtos.add(UserWithBalanceDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .balance(BalanceDto.builder()
                            .id(user.getBalance().getId())
                            .amount(user.getBalance().getAmount()).build())
                    .coin(CoinDto.builder()
                            .id(user.getCoin().getId())
                            .amount(user.getCoin().getAmount())
                            .build())
                    .createdAt(user.getCreatedAt())
                    .build());
        });

        UserWithPageDto userWithPageDto = UserWithPageDto.builder()
                .users(userWithBalanceDtos)
                .page(page)
                .pageAvailable(userList.getTotalPages())
                .build();

        return Response.build(Response.get("user data"), userWithPageDto, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> updateUser(UserUpdateDto userUpdateDto, Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            return Response.build(Response.notFound("User"), null, null, HttpStatus.BAD_REQUEST);
        }

        user.get().setName(userUpdateDto.getName());
        user.get().getBalance().setAmount(Long.valueOf(userUpdateDto.getBalance()));
        user.get().getCoin().setAmount(Long.valueOf(userUpdateDto.getCoin()));

        if(!user.get().getPhone().equals(userUpdateDto.getPhone())){
            if (Boolean.TRUE.equals(userRepository.existsByPhone(userUpdateDto.getPhone()))) {
                return Response.build(Response.exist("User", "phone", userUpdateDto.getPhone()), null, null, HttpStatus.BAD_REQUEST);
            }
            user.get().setPhone(userUpdateDto.getPhone());
        }

        userRepository.save(user.get());

        UserWithBalanceDto userDto = UserWithBalanceDto.builder()
                .id(user.get().getId())
                .name(user.get().getName())
                .email(user.get().getEmail())
                .phone(user.get().getPhone())
                .balance(BalanceDto.builder()
                        .id(user.get().getBalance().getId())
                        .amount(user.get().getBalance().getAmount())
                        .build())
                .coin(CoinDto.builder()
                        .id(user.get().getCoin().getId())
                        .amount(user.get().getCoin().getAmount())
                        .build())
                .createdAt(user.get().getCreatedAt())
                .updatedAt(user.get().getUpdatedAt())
                .build();

        return Response.build(Response.update("user data"), userDto, null, HttpStatus.CREATED);
    }

}
