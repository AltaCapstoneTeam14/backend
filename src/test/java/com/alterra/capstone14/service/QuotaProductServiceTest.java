package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dao.QuotaProduct;
import com.alterra.capstone14.domain.dto.ProviderDto;
import com.alterra.capstone14.domain.dto.QuotaProductDto;
import com.alterra.capstone14.repository.ProviderRepository;
import com.alterra.capstone14.repository.QuotaProductRepository;
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
@SpringBootTest(classes = QuotaProductService.class)
public class QuotaProductServiceTest {

    @MockBean
    QuotaProductRepository quotaProductRepository;

    @MockBean
    ProviderRepository providerRepository;

    @MockBean
    ModelMapper modelMapper;

    @Autowired
    QuotaProductService quotaProductService;

    @Test
    void addQuotaProductServiceSuccess_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();
        ProviderDto providerDto = ProviderDto.builder().id(1L).name("Telkomsel").build();

        QuotaProduct quotaProduct = QuotaProduct.builder()
                .id(1L)
                .name("5GB")
                .description("Quota 5GB berlaku 1 bulan")
                .grossAmount(21000L)
                .stock(10L)
                .build();

        when(providerRepository.findById(any())).thenReturn(Optional.ofNullable(provider));
        when(modelMapper.map(any(), eq(QuotaProduct.class))).thenReturn(quotaProduct);
        when(quotaProductRepository.save(any())).thenReturn(quotaProduct);

        ResponseEntity<Object> response = quotaProductService.addQuotaProduct(
                QuotaProductDto.builder()
                        .name("5GB")
                        .providerId(1L)
                        .description("Quota 5GB berlaku 1 bulan")
                        .grossAmount(21000L)
                        .stock(10L)
                        .build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        QuotaProductDto data = (QuotaProductDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Add quota product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("5GB", data.getName());
        assertEquals("Quota 5GB berlaku 1 bulan", data.getDescription());
        assertEquals(21000L, data.getGrossAmount());
        assertEquals(10L, data.getStock());
        assertEquals(1L, data.getProviderId());
        assertEquals("Telkomsel", data.getProviderName());

    }

    @Test
    void addQuotaProductProviderNotFound_Test(){
        when(providerRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = quotaProductService.addQuotaProduct(
                QuotaProductDto.builder()
                        .name("5GB")
                        .providerId(1L)
                        .description("Quota 5GB berlaku 1 bulan")
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
    void getQuotaProductSuccess_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        QuotaProduct quotaProduct = QuotaProduct.builder()
                .id(1L)
                .name("5GB")
                .description("Quota 5GB berlaku 1 bulan")
                .grossAmount(21000L)
                .provider(provider)
                .stock(10L)
                .build();

        List<QuotaProduct> quotaProductList = new ArrayList<>();
        quotaProductList.add(quotaProduct);

        when(quotaProductRepository.findAllSorted()).thenReturn(quotaProductList);

        ResponseEntity<Object> response = quotaProductService.getQuotaProducts();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<QuotaProductDto> data = (List<QuotaProductDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get quota product success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1, data.size());
    }

    @Test
    void updateQuotaProductSuccess_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();
        ProviderDto providerDto = ProviderDto.builder().id(1L).name("Telkomsel").build();

        QuotaProduct quotaProduct = QuotaProduct.builder()
                .id(1L)
                .name("5GB")
                .description("Quota 5GB berlaku 1 bulan")
                .grossAmount(21000L)
                .provider(provider)
                .stock(10L)
                .build();

        when(providerRepository.findById(any())).thenReturn(Optional.ofNullable(provider));
        when(quotaProductRepository.findById(any())).thenReturn(Optional.ofNullable(quotaProduct));
        when(quotaProductRepository.save(any())).thenReturn(quotaProduct);

        ResponseEntity<Object> response = quotaProductService.updateQuotaProduct(
                QuotaProductDto.builder()
                        .name("6GB")
                        .description("Quota 6GB berlaku 1 bulan")
                        .grossAmount(30000L)
                        .providerId(providerDto.getId())
                        .stock(10L)
                        .build()
                , 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        QuotaProductDto data = (QuotaProductDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Update quota product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("6GB", data.getName());
        assertEquals("Quota 6GB berlaku 1 bulan", data.getDescription());
        assertEquals(30000L, data.getGrossAmount());
        assertEquals(10L, data.getStock());
        assertEquals(1L, data.getProviderId());
        assertEquals("Telkomsel", data.getProviderName());
    }

    @Test
    void updateQuotaProductNotFound_Test(){
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        QuotaProductDto quotaProductDto = QuotaProductDto.builder()
                .name("5GB")
                .description("Quota 5GB berlaku 1 bulan")
                .grossAmount(21000L)
                .stock(10L)
                .build();

        when(providerRepository.findById(any())).thenReturn(Optional.ofNullable(provider));
        when(quotaProductRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = quotaProductService.updateQuotaProduct(quotaProductDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Quota product not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void updateQuotaProductProviderNotFound_Test(){
        QuotaProductDto quotaProductDto = QuotaProductDto.builder()
                .name("5GB")
                .description("Quota 5GB berlaku 1 bulan")
                .grossAmount(21000L)
                .stock(10L)
                .build();

        when(providerRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = quotaProductService.updateQuotaProduct(quotaProductDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Provider not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteQuotaProductSuccess_Test() {
        Provider provider = Provider.builder().id(1L).name("Telkomsel").build();

        QuotaProduct quotaProduct = QuotaProduct.builder()
                .id(1L)
                .name("5GB")
                .description("Quota 5GB berlaku 1 bulan")
                .grossAmount(21000L)
                .provider(provider)
                .stock(10L)
                .build();

        when(quotaProductRepository.findById(any())).thenReturn(Optional.ofNullable(quotaProduct));

        ResponseEntity<Object> response = quotaProductService.deleteQuotaProduct(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete quota product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteQuotaProductNotFound_Test(){
        when(quotaProductRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = quotaProductService.deleteQuotaProduct(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Quota product not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
