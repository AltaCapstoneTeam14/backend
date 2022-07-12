package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.Role;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.EmailDto;
import com.alterra.capstone14.domain.dto.UserNoPwdDto;
import com.alterra.capstone14.domain.dto.UserWithBalanceDto;
import com.alterra.capstone14.repository.RoleRepository;
import com.alterra.capstone14.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SuperAdminService.class)
public class SuperAdminServiceTest {
    @MockBean
    UserRepository userRepository;

    @MockBean
    RoleRepository roleRepository;

    @Autowired
    SuperAdminService superAdminService;

    @Test
    void addAdminRoleSuccess_Test(){
        EmailDto emailDto = EmailDto.builder().email("hamid@gmail.com").build();

        Role userRole = Role.builder().id(1L).name(ERole.USER).build();
        Role adminRole = Role.builder().id(1L).name(ERole.ADMIN).build();

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

        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.ofNullable(adminRole));
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<Object> response = superAdminService.addAdminRole(emailDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        UserNoPwdDto data = (UserNoPwdDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Update user success", apiResponse.getMessage());
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
    void addAdminRoleUserNotFound_Test(){
        EmailDto emailDto = EmailDto.builder().email("hamid@gmail.com").build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = superAdminService.addAdminRole(emailDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("User not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void addAdminRoleRoleAdminNotFound_Test(){
        EmailDto emailDto = EmailDto.builder().email("hamid@gmail.com").build();

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

        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = superAdminService.addAdminRole(emailDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
