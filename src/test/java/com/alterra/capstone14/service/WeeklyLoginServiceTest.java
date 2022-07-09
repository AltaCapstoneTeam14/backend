package com.alterra.capstone14.service;


import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dto.WeeklyLoginDto;
import com.alterra.capstone14.repository.CoinRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.repository.WeeklyLoginRepository;
import com.alterra.capstone14.util.DateCurrent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WeeklyLoginService.class)
public class WeeklyLoginServiceTest {
    @MockBean
    WeeklyLoginRepository weeklyLoginRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CoinRepository coinRepository;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    @MockBean
    private DateCurrent dateCurrent;

    @Autowired
    private WeeklyLoginService weeklyLoginService;

    @Test
    void getStatusNotClaimed_Test(){
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        WeeklyLogin weeklyLogin = WeeklyLogin.builder()
                .id(1L)
                .user(user)
                .startDate("2022-07-04")
                .loginCount(3)
                .lastLogin(5)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(dateCurrent.getMonday()).thenReturn("2022-07-04");
        when(dateCurrent.getDay()).thenReturn(6);
        when(weeklyLoginRepository.findByStartDate(any())).thenReturn(Optional.ofNullable(weeklyLogin));

        ResponseEntity<Object> response = weeklyLoginService.getStatus();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        WeeklyLoginDto data = (WeeklyLoginDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get daily login status success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("not_claimed", data.getStatus());
        assertEquals("2022-07-04", data.getStartDate());
        assertEquals(3, data.getLoginCount());
        assertEquals(5, data.getLastLogin());

    }

    @Test
    void getStatusClaimed_Test(){
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        WeeklyLogin weeklyLogin = WeeklyLogin.builder()
                .id(1L)
                .user(user)
                .startDate("2022-07-04")
                .loginCount(3)
                .lastLogin(5)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(dateCurrent.getMonday()).thenReturn("2022-07-04");
        when(dateCurrent.getDay()).thenReturn(5);
        when(weeklyLoginRepository.findByStartDate(any())).thenReturn(Optional.ofNullable(weeklyLogin));

        ResponseEntity<Object> response = weeklyLoginService.getStatus();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        WeeklyLoginDto data = (WeeklyLoginDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get daily login status success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("claimed", data.getStatus());
        assertEquals("2022-07-04", data.getStartDate());
        assertEquals(3, data.getLoginCount());
        assertEquals(5, data.getLastLogin());

    }

    @Test
    void getStatusNewWeek_Test(){
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        WeeklyLogin weeklyLogin = WeeklyLogin.builder()
                .id(1L)
                .user(user)
                .startDate("2022-07-04")
                .loginCount(3)
                .lastLogin(5)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(dateCurrent.getMonday()).thenReturn("2022-07-04");
        when(dateCurrent.getDay()).thenReturn(5);
        when(weeklyLoginRepository.findByStartDate(any())).thenReturn(Optional.empty());
        when(weeklyLoginRepository.save(any())).thenReturn(weeklyLogin);

        ResponseEntity<Object> response = weeklyLoginService.getStatus();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        WeeklyLoginDto data = (WeeklyLoginDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get daily login status success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals("not_claimed", data.getStatus());
        assertEquals("2022-07-04", data.getStartDate());
        assertEquals(0, data.getLoginCount());
        assertEquals(0, data.getLastLogin());
    }

    @Test
    void getBonusSuccess_Test(){
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .balance(Balance.builder().id(1L).amount(0L).build())
                .coin(Coin.builder().id(1L).amount(0L).build())
                .createdAt(LocalDateTime.now())
                .build();


        CustomUserDetails userDetails = CustomUserDetails.build(user);

        WeeklyLogin weeklyLogin = WeeklyLogin.builder()
                .id(1L)
                .user(user)
                .startDate("2022-07-04")
                .loginCount(3)
                .lastLogin(5)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(dateCurrent.getMonday()).thenReturn("2022-07-04");
        when(dateCurrent.getDay()).thenReturn(6);
        when(weeklyLoginRepository.findByStartDate(any())).thenReturn(Optional.ofNullable(weeklyLogin));
        when(weeklyLoginRepository.save(any())).thenReturn(weeklyLogin);
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<Object> response = weeklyLoginService.getBonus();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        WeeklyLoginDto data = (WeeklyLoginDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Claim daily login success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals("claimed", data.getStatus());
        assertEquals("2022-07-04", data.getStartDate());
        assertEquals(4, data.getLoginCount());
        assertEquals(6, data.getLastLogin());
        assertEquals(100L, data.getCoinReward());
    }

    @Test
    void getBonusDay7_Test(){
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .balance(Balance.builder().id(1L).amount(0L).build())
                .coin(Coin.builder().id(1L).amount(0L).build())
                .createdAt(LocalDateTime.now())
                .build();


        CustomUserDetails userDetails = CustomUserDetails.build(user);

        WeeklyLogin weeklyLogin = WeeklyLogin.builder()
                .id(1L)
                .user(user)
                .startDate("2022-07-04")
                .loginCount(6)
                .lastLogin(6)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(dateCurrent.getMonday()).thenReturn("2022-07-04");
        when(dateCurrent.getDay()).thenReturn(7);
        when(weeklyLoginRepository.findByStartDate(any())).thenReturn(Optional.ofNullable(weeklyLogin));
        when(weeklyLoginRepository.save(any())).thenReturn(weeklyLogin);
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<Object> response = weeklyLoginService.getBonus();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        WeeklyLoginDto data = (WeeklyLoginDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Claim daily login success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals("claimed", data.getStatus());
        assertEquals("2022-07-04", data.getStartDate());
        assertEquals(7, data.getLoginCount());
        assertEquals(7, data.getLastLogin());
        assertEquals(500L, data.getCoinReward());
    }

    @Test
    void getBonusDayNewDay_Test(){
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .balance(Balance.builder().id(1L).amount(0L).build())
                .coin(Coin.builder().id(1L).amount(0L).build())
                .createdAt(LocalDateTime.now())
                .build();


        CustomUserDetails userDetails = CustomUserDetails.build(user);

        WeeklyLogin weeklyLogin = WeeklyLogin.builder()
                .id(1L)
                .user(user)
                .startDate("2022-07-04")
                .loginCount(0)
                .lastLogin(0)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(dateCurrent.getMonday()).thenReturn("2022-07-04");
        when(dateCurrent.getDay()).thenReturn(7);
        when(weeklyLoginRepository.findByStartDate(any())).thenReturn(Optional.empty());
        when(weeklyLoginRepository.save(any())).thenReturn(weeklyLogin);
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<Object> response = weeklyLoginService.getBonus();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        WeeklyLoginDto data = (WeeklyLoginDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Claim daily login success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals("claimed", data.getStatus());
        assertEquals("2022-07-04", data.getStartDate());
        assertEquals(1, data.getLoginCount());
        assertEquals(7, data.getLastLogin());
        assertEquals(100L, data.getCoinReward());
    }

}
