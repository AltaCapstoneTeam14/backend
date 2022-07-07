package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.Subscriber;
import com.alterra.capstone14.domain.dto.SubscriberDto;
import com.alterra.capstone14.repository.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void addSubscriberSuccess_Test(){

        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();
        SubscriberDto subscriberDto = SubscriberDto.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(any())).thenReturn(Optional.ofNullable(subscriber));
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
    void addSubscriberNewSubscriber_Test(){

        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();
        SubscriberDto subscriberDto = SubscriberDto.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(modelMapper.map(any(), eq(Subscriber.class))).thenReturn(subscriber);
        when(subscriberRepository.save(any())).thenReturn(subscriber);
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
    void getSubscriberSuccess_Test(){
        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();

        List<Subscriber> subscriberList = new ArrayList<>();
        subscriberList.add(subscriber);

        when(subscriberRepository.findAll()).thenReturn(subscriberList);

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
    void deleteCashoutProductSuccess_Test() {
        Subscriber subscriber = Subscriber.builder().id(1L).email("joe@gmail.com").build();

        when(subscriberRepository.findById(any())).thenReturn(Optional.ofNullable(subscriber));

        ResponseEntity<Object> response = subscriberService.deleteSubscriber(1L);


        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete subscriber success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteCashoutProductNotFound_Test(){
        when(subscriberRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = subscriberService.deleteSubscriber(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Subscriber not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
