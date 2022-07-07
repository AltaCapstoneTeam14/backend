package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dto.PasswordDto;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.domain.dto.UserNoPwdDto;
import com.alterra.capstone14.domain.dto.UserWithBalanceDto;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.CoinRepository;
import com.alterra.capstone14.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserService.class)
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private BalanceRepository balanceRepository;

    @MockBean
    private CoinRepository coinRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;


    @Test
    void getProfileSuccess_Test() {
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

        Balance balance = Balance.builder().id(1L).user(user).amount(0L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(0L).build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(balanceRepository.findByUserId(any())).thenReturn(Optional.ofNullable(balance));
        when(coinRepository.findByUserId(any())).thenReturn(Optional.ofNullable(coin));

        ResponseEntity<Object> response = userService.getProfile();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserWithBalanceDto data = (UserWithBalanceDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get user data success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Hamid", data.getName());
        assertEquals("hamid@gmail.com", data.getEmail());
        assertEquals("081999888999", data.getPhone());
        assertEquals(1L, data.getBalance().getId());
        assertEquals(0L, data.getBalance().getAmount());
        assertEquals(1L, data.getCoin().getId());
        assertEquals(0L, data.getCoin().getAmount());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void getProfileNoBalanceSuccess_Test() {
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

        Balance balance = Balance.builder().id(1L).user(user).amount(0L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(0L).build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(balanceRepository.findByUserId(any())).thenReturn(Optional.empty());
        when(balanceRepository.save(any())).thenReturn(balance);
        when(coinRepository.findByUserId(any())).thenReturn(Optional.ofNullable(coin));

        ResponseEntity<Object> response = userService.getProfile();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserWithBalanceDto data = (UserWithBalanceDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get user data success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Hamid", data.getName());
        assertEquals("hamid@gmail.com", data.getEmail());
        assertEquals("081999888999", data.getPhone());
        assertEquals(0L, data.getBalance().getAmount());
        assertEquals(1L, data.getCoin().getId());
        assertEquals(0L, data.getCoin().getAmount());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void getProfileNoCoinSuccess_Test() {
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

        Balance balance = Balance.builder().id(1L).user(user).amount(0L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(0L).build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(balanceRepository.findByUserId(any())).thenReturn(Optional.ofNullable(balance));
        when(coinRepository.findByUserId(any())).thenReturn(Optional.empty());
        when(coinRepository.save(any())).thenReturn(coin);

        ResponseEntity<Object> response = userService.getProfile();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserWithBalanceDto data = (UserWithBalanceDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get user data success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Hamid", data.getName());
        assertEquals("hamid@gmail.com", data.getEmail());
        assertEquals("081999888999", data.getPhone());
        assertEquals(1L, data.getBalance().getId());
        assertEquals(0L, data.getBalance().getAmount());
        assertEquals(0L, data.getCoin().getAmount());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void updateUserDataSuccess_Test() {
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

        UserNoPwdDto userNoPwdDto = UserNoPwdDto.builder()
                .name("Hamidi")
                .email("hamidi@gmail.com")
                .phone("081999888990")
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByPhone(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<Object> response = userService.updateUserData(userNoPwdDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserNoPwdDto data = (UserNoPwdDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Update user success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Hamidi", data.getName());
        assertEquals("hamidi@gmail.com", data.getEmail());
        assertEquals("081999888990", data.getPhone());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void updateUserDataPhoneExist_Test() {
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

        UserNoPwdDto userNoPwdDto = UserNoPwdDto.builder()
                .name("Hamidi")
                .email("hamid@gmail.com")
                .phone("081999888990")
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByPhone(anyString())).thenReturn(Boolean.TRUE);

        ResponseEntity<Object> response = userService.updateUserData(userNoPwdDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("User with phone 081999888990 exist", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void updateUserDataEmailExist_Test() {
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

        UserNoPwdDto userNoPwdDto = UserNoPwdDto.builder()
                .name("Hamidi")
                .email("hamidi@gmail.com")
                .phone("081999888990")
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByPhone(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.TRUE);

        ResponseEntity<Object> response = userService.updateUserData(userNoPwdDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("User with email hamidi@gmail.com exist", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void updateUserPasswordSuccess_Test() {
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

        PasswordDto passwordDto = PasswordDto.builder().password("passwordd").build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<Object> response = userService.updateUserPassword(passwordDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserNoPwdDto data = (UserNoPwdDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Update password success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Hamid", data.getName());
        assertEquals("hamid@gmail.com", data.getEmail());
        assertEquals("081999888999", data.getPhone());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void deleteUserSuccess_Test() {
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

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.deleteUser();

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete user success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
