package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.CashoutProduct;
import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dto.CashoutProductDto;
import com.alterra.capstone14.domain.dto.ProviderDto;
import com.alterra.capstone14.repository.ProviderRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProviderService.class)
public class ProviderServiceTest {

    @MockBean
    ProviderRepository providerRepository;

    @MockBean
    ModelMapper modelMapper;

    @Autowired
    ProviderService providerService;

    @Test
    void addProviderSuccess_Test(){

        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();
        ProviderDto providerDto = ProviderDto.builder().id(1L).name("Telkomsel").build();

        when(modelMapper.map(any(), eq(Provider.class))).thenReturn(provider);
        when(modelMapper.map(any(), eq(ProviderDto.class))).thenReturn(providerDto);
        when(providerRepository.save(any())).thenReturn(provider);

        ResponseEntity<Object> response = providerService.addProvider(ProviderDto.builder().name("Telkomesl").build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        ProviderDto data = (ProviderDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Add provider success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Telkomsel", data.getName());
    }

    @Test
    void getProviderSuccess_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        List<Provider> providerList = new ArrayList<>();
        providerList.add(provider);

        when(providerRepository.findAll()).thenReturn(providerList);

        ResponseEntity<Object> response = providerService.getProviders();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<ProviderDto> data = (List<ProviderDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get provider success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1, data.size());
    }


    @Test
    void deleteCashoutProductSuccess_Test() {
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        when(providerRepository.findById(any())).thenReturn(Optional.ofNullable(provider));

        ResponseEntity<Object> response = providerService.deleteProvider(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete provider success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteCashoutProductNotFound_Test(){
        when(providerRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = providerService.deleteProvider(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Provider not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
