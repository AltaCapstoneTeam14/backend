package com.alterra.capstone14.service;

import com.alterra.capstone14.config.CustomAuthentication;
import com.alterra.capstone14.config.security.JwtUtils;
import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dto.LoginDto;
import com.alterra.capstone14.domain.dto.TokenDto;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.domain.dto.UserNoPwdDto;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.CoinRepository;
import com.alterra.capstone14.repository.RoleRepository;
import com.alterra.capstone14.repository.UserRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthService.class)
public class AuthServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private BalanceRepository balanceRepository;

    @MockBean
    private CoinRepository coinRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Test
    void registerUserSuccess_Test() {
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

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .build();

        Balance balance = Balance.builder().user(user).amount(0L).build();
        Coin coin = Coin.builder().id(1L).user(user).amount(0L).build();

        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByPhone(anyString())).thenReturn(Boolean.FALSE);
        when(modelMapper.map(any(), eq(User.class))).thenReturn(user);
        when(roleRepository.findByName(ERole.USER)).thenReturn(Optional.ofNullable(userRole));
        when(userRepository.save(user)).thenReturn(user);
        when(balanceRepository.save(balance)).thenReturn(balance);
        when(coinRepository.save(coin)).thenReturn(coin);

        ResponseEntity<Object> response = authService.registerUser(userDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserNoPwdDto data = (UserNoPwdDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Register success", apiResponse.getMessage());
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
    void registerUserEmailExist_Test() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .build();

        when(userRepository.existsByEmail("hamid@gmail.com")).thenReturn(Boolean.TRUE);

        ResponseEntity<Object> response = authService.registerUser(userDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("User with email hamid@gmail.com exist", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void registerUserPhoneExist_Test() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .build();

        when(userRepository.existsByEmail("hamid@gmail.com")).thenReturn(Boolean.FALSE);
        when(userRepository.existsByPhone(any())).thenReturn(Boolean.TRUE);

        ResponseEntity<Object> response = authService.registerUser(userDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserNoPwdDto data = (UserNoPwdDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("User with phone 081999888999 exist", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void registerRoleUserNotFound_Test() {
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

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByPhone(anyString())).thenReturn(Boolean.FALSE);
        when(modelMapper.map(any(), eq(User.class))).thenReturn(user);
        when(roleRepository.findByName(ERole.USER)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authService.registerUser(userDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginUserSuccess_Test() {
        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(encoder.matches(any(), any())).thenReturn(Boolean.TRUE);
        when(authenticationManager.authenticate(any())).thenReturn(new CustomAuthentication());
        when(jwtUtils.generateJwtToken(any())).thenReturn("token");

        ResponseEntity<Object> response = authService.loginUser(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        TokenDto data = (TokenDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Login success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertNotNull(data.getToken());
    }

    @Test
    void loginUserEmailNotFound_Test() {

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        Mockito.when(userRepository.existsByEmail(any())).thenReturn(Boolean.FALSE);

        ResponseEntity<Object> response = authService.loginUser(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginUserPasswordNotMatch_Test() {
        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        Mockito.when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(encoder.matches(any(), any())).thenReturn(Boolean.FALSE);

        ResponseEntity<Object> response = authService.loginUser(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginAdminSuccess_Test() {
        Role adminRole = Role.builder().id(1L).name(ERole.ADMIN).build();
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.ofNullable(adminRole));
        when(encoder.matches(any(), any())).thenReturn(Boolean.TRUE);
        when(authenticationManager.authenticate(any())).thenReturn(new CustomAuthentication());
        when(jwtUtils.generateJwtToken(any())).thenReturn("token");

        ResponseEntity<Object> response = authService.loginAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        TokenDto data = (TokenDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Login success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertNotNull(data.getToken());
    }

    @Test
    void loginAdminEmailNotFound_Test() {
        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        Mockito.when(userRepository.existsByEmail(any())).thenReturn(Boolean.FALSE);

        ResponseEntity<Object> response = authService.loginAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginAdminRoleNotFound_Test() {
        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authService.loginAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginAdminPasswordNotMatch_Test() {
        Role adminRole = Role.builder().id(1L).name(ERole.ADMIN).build();
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .roles(roles)
                .phone("081999888999")
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        Mockito.when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(roleRepository.findByName(any())).thenReturn(Optional.ofNullable(adminRole));
        Mockito.when(encoder.matches(any(), any())).thenReturn(Boolean.FALSE);

        ResponseEntity<Object> response = authService.loginAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginAdminUserNotAdmin_Test() {
        Role adminRole = Role.builder().id(1L).name(ERole.ADMIN).build();
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

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.ofNullable(adminRole));

        ResponseEntity<Object> response = authService.loginAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }


    @Test
    void loginSuperAdminSuccess_Test() {
        Role superAdminRole = Role.builder().id(1L).name(ERole.SUPER_ADMIN).build();
        Set<Role> roles = new HashSet<>();
        roles.add(superAdminRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.SUPER_ADMIN)).thenReturn(Optional.ofNullable(superAdminRole));
        when(encoder.matches(any(), any())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(new CustomAuthentication());
        when(jwtUtils.generateJwtToken(any())).thenReturn("token");

        ResponseEntity<Object> response = authService.loginSuperAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        TokenDto data = (TokenDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Login success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertNotNull(data.getToken());
    }

    @Test
    void loginSuperAdminEmailNotFound_Test() {
        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        Mockito.when(userRepository.existsByEmail(any())).thenReturn(Boolean.FALSE);

        ResponseEntity<Object> response = authService.loginSuperAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginSuperAdminRoleNotFound_Test() {
        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authService.loginSuperAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void loginSuperAdminPasswordNotMatch_Test() {
        Role superAdminRole = Role.builder().id(1L).name(ERole.SUPER_ADMIN).build();
        Set<Role> roles = new HashSet<>();
        roles.add(superAdminRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        Mockito.when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(roleRepository.findByName(any())).thenReturn(Optional.ofNullable(superAdminRole));
        Mockito.when(encoder.matches(any(), any())).thenReturn(Boolean.FALSE);

        ResponseEntity<Object> response = authService.loginSuperAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }


    @Test
    void loginAdminUserNotSuperAdmin_Test() {
        Role superAdminRole = Role.builder().id(1L).name(ERole.SUPER_ADMIN).build();
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

        LoginDto loginDto = LoginDto.builder()
                .email("hamid@gmail.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(Boolean.TRUE);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.SUPER_ADMIN)).thenReturn(Optional.ofNullable(superAdminRole));

        ResponseEntity<Object> response = authService.loginSuperAdmin(loginDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Email or password incorrect", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
