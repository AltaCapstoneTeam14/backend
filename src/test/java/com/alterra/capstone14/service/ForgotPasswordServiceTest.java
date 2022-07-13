package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.EResponseStatus;
import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.ForgotPasswordToken;
import com.alterra.capstone14.domain.dao.Role;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.EmailDto;
import com.alterra.capstone14.domain.dto.PasswordDto;
import com.alterra.capstone14.domain.dto.SubscriberDto;
import com.alterra.capstone14.domain.reqbody.ForgotPasswordBody;
import com.alterra.capstone14.repository.ForgotPasswordTokenRepository;
import com.alterra.capstone14.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ForgotPasswordService.class)
public class ForgotPasswordServiceTest {
    @MockBean
    UserRepository userRepository;

    @MockBean
    ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    @MockBean
    WebClient webClient;

    @MockBean
    PasswordEncoder encoder;

    @MockBean
    ForgotPasswordBody forgotPasswordBody;

    @Autowired
    ForgotPasswordService forgotPasswordService;

    @Test
    void requestResetPasswordSuccess_Test(){
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

        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder()
                .id(1L)
                .email("hamid@gmail.com")
                .token(UUID.fromString("6c4de93f-b73b-4cd5-8a09-b4e6e98161e9"))
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(forgotPasswordTokenRepository.save(any())).thenReturn(forgotPasswordToken);
        when(forgotPasswordBody.generate(any(), any())).thenReturn("forgotpasswordbody");

        var reqBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);
        var webClientResponse = EResponseStatus.SUCCESS.value;

        when(webClient.post()).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.uri(anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.header(anyString(), anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.bodyValue(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(webClientResponse);

        ResponseEntity<Object> response = forgotPasswordService.requestResetPassword(EmailDto.builder().email("hamid@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        EmailDto data = (EmailDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Request reset password sent", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals("hamid@gmail.com", data.getEmail());
    }

    @Test
    void requestResetPasswordResponseNull_Test(){
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

        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder()
                .id(1L)
                .email("hamid@gmail.com")
                .token(UUID.fromString("6c4de93f-b73b-4cd5-8a09-b4e6e98161e9"))
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(forgotPasswordTokenRepository.save(any())).thenReturn(forgotPasswordToken);
        when(forgotPasswordBody.generate(any(), any())).thenReturn("forgotpasswordbody");

        var reqBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.post()).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.uri(anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.header(anyString(), anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.bodyValue(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(null);

        ResponseEntity<Object> response = forgotPasswordService.requestResetPassword(EmailDto.builder().email("hamid@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void requestResetPasswordResponseBadRequest_Test(){
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

        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder()
                .id(1L)
                .email("hamid@gmail.com")
                .token(UUID.fromString("6c4de93f-b73b-4cd5-8a09-b4e6e98161e9"))
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(forgotPasswordTokenRepository.save(any())).thenReturn(forgotPasswordToken);
        when(forgotPasswordBody.generate(any(), any())).thenReturn("forgotpasswordbody");

        var reqBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.post()).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.uri(anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.header(anyString(), anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.bodyValue(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(EResponseStatus.BAD_REQUEST.value);

        ResponseEntity<Object> response = forgotPasswordService.requestResetPassword(EmailDto.builder().email("hamid@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Bad request", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void requestResetPasswordUserNotFound_Test(){
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = forgotPasswordService.requestResetPassword(
                EmailDto.builder().email("hamid@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("User not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void confirmResetPasswordSuccess_Test(){
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

        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder()
                .id(1L)
                .email("hamid@gmail.com")
                .token(UUID.fromString("6c4de93f-b73b-4cd5-8a09-b4e6e98161e9"))
                .build();

        when(forgotPasswordTokenRepository.findByToken(any())).thenReturn(Optional.ofNullable(forgotPasswordToken));
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(encoder.encode(any())).thenReturn("stringpassword");

        ResponseEntity<Object> response = forgotPasswordService.confirmResetPassword(
                "6c4de93f-b73b-4cd5-8a09-b4e6e98161e9",
                PasswordDto.builder().password("updatedPassword").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Password updated", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void confirmResetPasswordTokenNotFound_Test(){

        when(forgotPasswordTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = forgotPasswordService.confirmResetPassword(
                "6c4de93f-b73b-4cd5-8a09-b4e6e98161e9",
                PasswordDto.builder().password("updatedPassword").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Token not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void confirmResetPasswordUserNotFound_Test(){

        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder()
                .id(1L)
                .email("hamid@gmail.com")
                .token(UUID.fromString("6c4de93f-b73b-4cd5-8a09-b4e6e98161e9"))
                .build();

        when(forgotPasswordTokenRepository.findByToken(any())).thenReturn(Optional.ofNullable(forgotPasswordToken));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = forgotPasswordService.confirmResetPassword(
                "6c4de93f-b73b-4cd5-8a09-b4e6e98161e9",
                PasswordDto.builder().password("updatedPassword").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("User not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
