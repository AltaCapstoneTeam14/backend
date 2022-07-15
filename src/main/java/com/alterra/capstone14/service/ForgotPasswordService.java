package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.EResponseStatus;
import com.alterra.capstone14.domain.dao.ForgotPasswordToken;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.EmailDto;
import com.alterra.capstone14.domain.dto.PasswordDto;
import com.alterra.capstone14.domain.thirdparty.req.ForgotPasswordBody;
import com.alterra.capstone14.repository.ForgotPasswordTokenRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ForgotPasswordService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    @Autowired
    WebClient webClient;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    ForgotPasswordBody forgotPasswordBody;

    @Value("${sendinblue.baseurl}")
    private String sibBaseUrl;

    @Value("${sendinblue.api.key}")
    private String sibApiKey;

    @Transactional
    public ResponseEntity<Object> requestResetPassword(EmailDto emailDto) {
        Optional<User> user = userRepository.findByEmail(emailDto.getEmail());
        if(user.isEmpty()){
            return Response.build(Response.notFound("User"), null, null, HttpStatus.BAD_REQUEST);
        }

        forgotPasswordTokenRepository.deleteByEmail(emailDto.getEmail());

        ForgotPasswordToken resetPasswordToken = forgotPasswordTokenRepository.save(ForgotPasswordToken.builder().email(emailDto.getEmail()).build());

        String forgotPasswordBodyString = forgotPasswordBody.generate(
                resetPasswordToken.getEmail(),
                resetPasswordToken.getToken().toString());

        String response = webClient.post()
                .uri(sibBaseUrl + "/v3/smtp/email")
                .header("api-key", sibApiKey)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(forgotPasswordBodyString)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(EResponseStatus.SUCCESS.value);
                    }
                    return clientResponse.bodyToMono(String.class);
                }).block();

        if(response == null ){
            log.error("response is null");
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(!response.equals(EResponseStatus.SUCCESS.value)){
            return Response.build("Bad request", null, null, HttpStatus.BAD_REQUEST);
        }

        return Response.build("Request reset password sent to your email", emailDto, null, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Object> confirmResetPassword(String token, PasswordDto passwordDto) {
        UUID tokenUUID = UUID.fromString(token);
        Optional<ForgotPasswordToken> forgotPasswordToken = forgotPasswordTokenRepository.findByToken(tokenUUID);
        if(forgotPasswordToken.isEmpty()){
            return Response.build(Response.notFound("Token"), null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByEmail(forgotPasswordToken.get().getEmail());
        if(user.isEmpty()){
            return Response.build(Response.notFound("User"), null, null, HttpStatus.BAD_REQUEST);
        }
        user.get().setPassword(encoder.encode(passwordDto.getPassword()));

        forgotPasswordTokenRepository.deleteByEmail(forgotPasswordToken.get().getEmail());

        return Response.build("Password updated", null, null, HttpStatus.CREATED);
    }
}
