package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dao.PulsaProduct;
import com.alterra.capstone14.domain.dto.ProviderDto;
import com.alterra.capstone14.domain.dto.PulsaProductDto;
import com.alterra.capstone14.repository.ProviderRepository;
import com.alterra.capstone14.repository.PulsaProductRepository;
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
@SpringBootTest(classes = PulsaProductService.class)
public class PulsaProductServiceTest {

    @MockBean
    PulsaProductRepository pulsaProductRepository;

    @MockBean
    ProviderRepository providerRepository;

    @MockBean
    ModelMapper modelMapper;

    @Autowired
    PulsaProductService pulsaProductService;

    @Test
    void addPulsaProductServiceSuccess_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();
        ProviderDto providerDto = ProviderDto.builder().id(1L).name("Telkomsel").build();

        PulsaProduct pulsaProduct = PulsaProduct.builder()
                .id(1L)
                .name("Pulsa 20K")
                .denom(20000L)
                .grossAmount(21000L)
                .stock(10L)
                .build();

        when(providerRepository.findById(any())).thenReturn(Optional.ofNullable(provider));
        when(modelMapper.map(any(), eq(PulsaProduct.class))).thenReturn(pulsaProduct);
        when(pulsaProductRepository.save(any())).thenReturn(pulsaProduct);

        ResponseEntity<Object> response = pulsaProductService.addPulsaProduct(
                PulsaProductDto.builder()
                        .name("Pulsa 20K")
                        .providerId(1L)
                        .denom(20000L)
                        .grossAmount(21000L)
                        .stock(10L)
                        .build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        PulsaProductDto data = (PulsaProductDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Add pulsa product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Pulsa 20K", data.getName());
        assertEquals(20000L, data.getDenom());
        assertEquals(21000L, data.getGrossAmount());
        assertEquals(10L, data.getStock());
        assertEquals(1L, data.getProviderId());
        assertEquals("Telkomsel", data.getProviderName());

    }

    @Test
    void addPulsaProductProviderNotFound_Test(){
        when(providerRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = pulsaProductService.addPulsaProduct(
                PulsaProductDto.builder()
                        .name("Pulsa 20K")
                        .providerId(1L)
                        .denom(20000L)
                        .grossAmount(21000L)
                        .stock(10L)
                        .build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Provider not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void getPulsaProductSuccess_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        PulsaProduct pulsaProduct = PulsaProduct.builder()
                .id(1L)
                .name("Pulsa 20K")
                .denom(20000L)
                .grossAmount(21000L)
                .provider(provider)
                .stock(10L)
                .build();

        List<PulsaProduct> pulsaProductList = new ArrayList<>();
        pulsaProductList.add(pulsaProduct);

        when(pulsaProductRepository.findAllSorted()).thenReturn(pulsaProductList);

        ResponseEntity<Object> response = pulsaProductService.getPulsaProducts();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<PulsaProductDto> data = (List<PulsaProductDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get pulsa product success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1, data.size());
    }

    @Test
    void updatePulsaProductSuccess_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();
        ProviderDto providerDto = ProviderDto.builder().id(1L).name("Telkomsel").build();

        PulsaProduct pulsaProduct = PulsaProduct.builder()
                .id(1L)
                .name("Pulsa 20K")
                .denom(20000L)
                .grossAmount(21000L)
                .provider(provider)
                .stock(10L)
                .build();

        PulsaProductDto pulsaProductDto = PulsaProductDto.builder()
                .id(1L)
                .name("Pulsa 25K")
                .denom(25000L)
                .grossAmount(26000L)
                .providerName(providerDto.getName())
                .providerId(providerDto.getId())
                .stock(10L)
                .build();

        when(providerRepository.findById(any())).thenReturn(Optional.ofNullable(provider));
        when(pulsaProductRepository.findById(any())).thenReturn(Optional.ofNullable(pulsaProduct));
        when(pulsaProductRepository.save(any())).thenReturn(pulsaProduct);

        ResponseEntity<Object> response = pulsaProductService.updatePulsaProduct(
                PulsaProductDto.builder()
                        .name("Pulsa 25K")
                        .denom(25000L)
                        .grossAmount(26000L)
                        .providerId(providerDto.getId())
                        .stock(10L)
                        .build()
                , 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        PulsaProductDto data = (PulsaProductDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Update pulsa product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Pulsa 25K", data.getName());
        assertEquals(25000L, data.getDenom());
        assertEquals(26000L, data.getGrossAmount());
        assertEquals(10L, data.getStock());
        assertEquals(1L, data.getProviderId());
        assertEquals("Telkomsel", data.getProviderName());
    }

    @Test
    void updatePulsaProductNotFound_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        PulsaProductDto pulsaProductDto = PulsaProductDto.builder()
                .name("Pulsa 25K")
                .denom(25000L)
                .grossAmount(26000L)
                .stock(10L)
                .build();

        when(providerRepository.findById(any())).thenReturn(Optional.ofNullable(provider));
        when(pulsaProductRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = pulsaProductService.updatePulsaProduct(pulsaProductDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Pulsa product not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void updatePulsaProductProviderNotFound_Test(){
        when(providerRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = pulsaProductService.updatePulsaProduct(
                PulsaProductDto.builder()
                        .name("Pulsa 20K")
                        .providerId(1L)
                        .denom(20000L)
                        .grossAmount(21000L)
                        .stock(10L)
                        .build(), 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Provider not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deletePulsaProductSuccess_Test() {
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        PulsaProduct pulsaProduct = PulsaProduct.builder()
                .id(1L)
                .name("Pulsa 20K")
                .denom(20000L)
                .grossAmount(21000L)
                .provider(provider)
                .stock(10L)
                .build();


        when(pulsaProductRepository.findById(any())).thenReturn(Optional.ofNullable(pulsaProduct));

        ResponseEntity<Object> response = pulsaProductService.deletePulsaProduct(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete pulsa product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deletePulsaProductNotFound_Test(){
        when(pulsaProductRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = pulsaProductService.deletePulsaProduct(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Pulsa product not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
