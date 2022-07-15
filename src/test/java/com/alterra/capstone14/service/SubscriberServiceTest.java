package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.EResponseStatus;
import com.alterra.capstone14.constant.ESIBResponseCode;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.Subscriber;
import com.alterra.capstone14.domain.dto.SubscriberDto;
import com.alterra.capstone14.domain.thirdparty.res.AllSubscriberRes;
import com.alterra.capstone14.domain.thirdparty.res.NegativeResponseSIB;
import com.alterra.capstone14.repository.SubscriberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SubscriberService.class)
public class SubscriberServiceTest {

    @MockBean
    SubscriberRepository subscriberRepository;

    @MockBean
    ModelMapper modelMapper;

    @Autowired
    SubscriberService subscriberService;

    @MockBean
    WebClient webClient;

    @MockBean
    ObjectMapper objectMapper;

    @Test
    void addSubscriberSuccess_Test() throws JsonProcessingException {

        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();
        SubscriberDto subscriberDto = SubscriberDto.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(modelMapper.map(any(), eq(Subscriber.class))).thenReturn(subscriber);
        when(subscriberRepository.save(any())).thenReturn(subscriber);

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

        when(modelMapper.map(any(), eq(SubscriberDto.class))).thenReturn(subscriberDto);


        ResponseEntity<Object> response = subscriberService.addSubscriber(SubscriberDto.builder().email("joe@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        SubscriberDto data = (SubscriberDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Add subscriber success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("joe@gmail.com", data.getEmail());
    }

    @Test
    void addSubscriberResponseNull_Test() throws JsonProcessingException {

        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(modelMapper.map(any(), eq(Subscriber.class))).thenReturn(subscriber);
        when(subscriberRepository.save(any())).thenReturn(subscriber);

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

        ResponseEntity<Object> response = subscriberService.addSubscriber(SubscriberDto.builder().email("joe@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void addSubscriberAlreadySubscribed_Test() throws JsonProcessingException {

        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();
        NegativeResponseSIB negativeResponseSIB = NegativeResponseSIB.builder()
                .code(ESIBResponseCode.DUPLICATE_PARAMETER.value)
                .message("String").build();

        when(subscriberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(modelMapper.map(any(), eq(Subscriber.class))).thenReturn(subscriber);
        when(subscriberRepository.save(any())).thenReturn(subscriber);

        var reqBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);
        var webClientResponse = "Not Success";

        when(webClient.post()).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.uri(anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.header(anyString(), anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.bodyValue(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(webClientResponse);

        when(objectMapper.readValue(anyString(), eq(NegativeResponseSIB.class))).thenReturn(negativeResponseSIB);

        ResponseEntity<Object> response = subscriberService.addSubscriber(SubscriberDto.builder().email("joe@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Already subscribed", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void addSubscriberBadRequest_Test() throws JsonProcessingException {

        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();
        NegativeResponseSIB negativeResponseSIB = NegativeResponseSIB.builder()
                .code("String")
                .message("String").build();

        when(subscriberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(modelMapper.map(any(), eq(Subscriber.class))).thenReturn(subscriber);
        when(subscriberRepository.save(any())).thenReturn(subscriber);

        var reqBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);
        var webClientResponse = "Not Success";

        when(webClient.post()).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.uri(anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.header(anyString(), anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.bodyValue(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(webClientResponse);

        when(objectMapper.readValue(anyString(), eq(NegativeResponseSIB.class))).thenReturn(negativeResponseSIB);

        ResponseEntity<Object> response = subscriberService.addSubscriber(SubscriberDto.builder().email("joe@gmail.com").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Bad request", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void getSubscriberSuccess_Test() throws JsonProcessingException {
        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();
        SubscriberDto subscriberDto = SubscriberDto.builder().id(1L).email("joe@gmail.com").build();

        List<SubscriberDto> subscriberList = new ArrayList<>();
        subscriberList.add(subscriberDto);

        AllSubscriberRes allSubscriberRes = AllSubscriberRes.builder()
                .contacts(subscriberList)
                .count(1)
                .build();

        var reqHeaderUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);
        var webClientResponse = "String";

        when(webClient.get()).thenReturn(reqHeaderUriSpecMock);
        when(reqHeaderUriSpecMock.uri(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.header(anyString(), anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(webClientResponse);
        when(objectMapper.readValue(anyString(), eq(AllSubscriberRes.class))).thenReturn(allSubscriberRes);

        ResponseEntity<Object> response = subscriberService.getSubscribers();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<SubscriberDto> data = (List<SubscriberDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get subscriber success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1, data.size());
    }

    @Test
    void getSubscriberResponseNull_Test() throws JsonProcessingException {

        var reqHeaderUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.get()).thenReturn(reqHeaderUriSpecMock);
        when(reqHeaderUriSpecMock.uri(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.header(anyString(), anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(null);

        ResponseEntity<Object> response = subscriberService.getSubscribers();

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void getSubscriberBadRequest_Test() throws JsonProcessingException {

        var reqHeaderUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.get()).thenReturn(reqHeaderUriSpecMock);
        when(reqHeaderUriSpecMock.uri(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.header(anyString(), anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(EResponseStatus.BAD_REQUEST.value);

        ResponseEntity<Object> response = subscriberService.getSubscribers();

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Bad request", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteSubscriberSuccess_Test() {
        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(subscriber));

        var reqHeaderUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);
        var webClientResponse = EResponseStatus.SUCCESS.value;

        when(webClient.delete()).thenReturn(reqHeaderUriSpecMock);
        when(reqHeaderUriSpecMock.uri(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.header(anyString(), anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(webClientResponse);

        ResponseEntity<Object> response = subscriberService.deleteSubscriber("joe@gmail.com");

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete subscriber success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteSubscriberResponseNull_Test() {
        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(subscriber));

        var reqHeaderUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.delete()).thenReturn(reqHeaderUriSpecMock);
        when(reqHeaderUriSpecMock.uri(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.header(anyString(), anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(null);

        ResponseEntity<Object> response = subscriberService.deleteSubscriber("joe@gmail.com");

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Internal server error", apiResponse.getMessage());
        assertEquals("500", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteSubscriberNotFound_Test() {
        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(subscriber));

        var reqHeaderUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.delete()).thenReturn(reqHeaderUriSpecMock);
        when(reqHeaderUriSpecMock.uri(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.header(anyString(), anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(EResponseStatus.NOT_FOUND.value);

        ResponseEntity<Object> response = subscriberService.deleteSubscriber("joe@gmail.com");

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Subscriber not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteSubscriberBadRequest_Test() {
        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(subscriber));

        var reqHeaderUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.delete()).thenReturn(reqHeaderUriSpecMock);
        when(reqHeaderUriSpecMock.uri(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.header(anyString(), anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.exchangeToMono(any())).thenReturn(monoMock);
        when(monoMock.block()).thenReturn("Bad request");

        ResponseEntity<Object> response = subscriberService.deleteSubscriber("joe@gmail.com");

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Bad request", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
