package com.alterra.capstone14.config;

import com.alterra.capstone14.domain.reqbody.ForgotPasswordBody;
import com.alterra.capstone14.util.DateCurrent;
import com.alterra.capstone14.util.Encryptor;
import com.alterra.capstone14.util.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Configuration
public class BeanConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public DateCurrent dateCurrent() {
        return  new DateCurrent();
    }

    @Bean
    public RandomString randomString(){
        return new RandomString();
    }

    @Bean
    public WebClient webClient(){
        return WebClient.create();
    }

    @Bean
    public Encryptor encryptor(){
        return new Encryptor();
    }

    @Value("${reset.password.sender}")
    private String resetPasswordSender;

    @Value("${reset.password.baseurl}")
    private String resetPasswordBaseUrl;

    @Bean
    public ForgotPasswordBody forgotPasswordBody(){
        return new ForgotPasswordBody(resetPasswordSender, resetPasswordBaseUrl);
    }

}