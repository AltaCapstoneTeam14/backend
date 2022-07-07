package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.Coin;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.UserUpdateDto;
import com.alterra.capstone14.domain.dto.UserWithBalanceDto;
import com.alterra.capstone14.domain.dto.UserWithPageDto;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.CoinRepository;
import com.alterra.capstone14.repository.RoleRepository;
import com.alterra.capstone14.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AdminService.class)
public class AdminServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    BalanceRepository balanceRepository;

    @MockBean
    CoinRepository coinRepository;

    @Autowired
    AdminService adminService;

    @Test
    void getAllUserSuccess_Test(){

        User user = User.builder()
                .id(1L)
                .name("Hamidb")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888990")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Balance balance = Balance.builder().id(1L).user(user).amount(140000L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(9000L).build();

        user.setCoin(coin);
        user.setBalance(balance);

        List<User> userList = new ArrayList<>();
        userList.add(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(users);
        when(balanceRepository.existsByUserId(anyLong())).thenReturn(Boolean.TRUE);
        when(coinRepository.existsByUserId(anyLong())).thenReturn(Boolean.TRUE);

        ResponseEntity<Object> response = adminService.getAllUser(1, 10);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserWithPageDto data = (UserWithPageDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get user data success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertNotNull(data.getPage());
        assertNotNull(data.getPageAvailable());
        assertNotNull(data.getUsers());
    }

    @Test
    void getAllUserNoBalanceSuccess_Test(){

        User user = User.builder()
                .id(1L)
                .name("Hamidb")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888990")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Balance balance = Balance.builder().id(1L).user(user).amount(140000L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(9000L).build();

        user.setCoin(coin);
        user.setBalance(balance);

        List<User> userList = new ArrayList<>();
        userList.add(user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(users);
        when(balanceRepository.existsByUserId(anyLong())).thenReturn(Boolean.TRUE);
        when(coinRepository.existsByUserId(anyLong())).thenReturn(Boolean.TRUE);

        ResponseEntity<Object> response = adminService.getAllUser(1, 10);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserWithPageDto data = (UserWithPageDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get user data success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertNotNull(data.getPage());
        assertNotNull(data.getPageAvailable());
        assertNotNull(data.getUsers());
    }

    @Test
    void updateUserSuccess_Test() {

        User user = User.builder()
                .id(1L)
                .name("Hamidb")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888990")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Balance balance = Balance.builder().id(1L).user(user).amount(140000L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(9000L).build();

        user.setCoin(coin);
        user.setBalance(balance);

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Hamid")
                .phone("081999888999")
                .balance("150000")
                .coin("10000")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByPhone(any())).thenReturn(Boolean.FALSE);
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<Object> response = adminService.updateUser(userUpdateDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserWithBalanceDto data = (UserWithBalanceDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Update user data success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Hamid", data.getName());
        assertEquals("hamid@gmail.com", data.getEmail());
        assertEquals("081999888999", data.getPhone());
        assertNotNull(data.getCreatedAt());
        assertNotNull(data.getUpdatedAt());
        assertEquals(1L, data.getBalance().getId());
        assertEquals(150000L, data.getBalance().getAmount());
        assertEquals(1L, data.getCoin().getId());
        assertEquals(10000L, data.getCoin().getAmount());
    }

    @Test
    // return Response.build(Response.notFound("user"), null, null, HttpStatus.BAD_REQUEST);
    void updateUserNotFound_Test(){
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Hamid")
                .phone("081999888999")
                .balance("150000")
                .coin("10000")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = adminService.updateUser(userUpdateDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("User not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void updateUserPhoneExist_Test(){
        User user = User.builder()
                .id(1L)
                .name("Hamidb")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888990")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Balance balance = Balance.builder().id(1L).user(user).amount(140000L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(9000L).build();

        user.setCoin(coin);
        user.setBalance(balance);

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Hamid")
                .phone("081999888999")
                .balance("150000")
                .coin("10000")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByPhone(any())).thenReturn(Boolean.TRUE);

        ResponseEntity<Object> response = adminService.updateUser(userUpdateDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserWithBalanceDto data = (UserWithBalanceDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("User with phone 081999888999 exist", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
