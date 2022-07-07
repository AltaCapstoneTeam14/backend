package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.CashoutProduct;
import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dto.CashoutProductDto;
import com.alterra.capstone14.domain.dto.TopupProductDto;
import com.alterra.capstone14.repository.CashoutProductRepository;
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
@SpringBootTest(classes = CashoutProductService.class)
public class CashoutProductServiceTest {
    @MockBean
    CashoutProductRepository cashoutProductRepository;

    @MockBean
    ModelMapper modelMapper;

    @Autowired
    CashoutProductService cashoutProductService;

    @Test
    void addCashoutProductServiceSuccess_Test(){
        CashoutProduct cashoutProduct = CashoutProduct.builder()
                .id(1L)
                .name("Cashout 20K")
                .coinAmount(20000L)
                .balanceAmount(20000L)
                .build();

        CashoutProductDto cashoutProductDto = CashoutProductDto.builder()
                .id(1L)
                .name("Cashout 20K")
                .coinAmount(20000L)
                .balanceAmount(20000L)
                .build();

        when(modelMapper.map(any(), eq(CashoutProduct.class))).thenReturn(cashoutProduct);
        when(modelMapper.map(any(), eq(CashoutProductDto.class))).thenReturn(cashoutProductDto);
        when(cashoutProductRepository.save(any())).thenReturn(cashoutProduct);

        ResponseEntity<Object> response = cashoutProductService.addCashoutProduct(
                CashoutProductDto.builder()
                        .name("Cashout 20K")
                        .coinAmount(20000L)
                        .balanceAmount(20000L)
                        .build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        CashoutProductDto data = (CashoutProductDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Add cashout product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Cashout 20K", data.getName());
        assertEquals(20000L, data.getBalanceAmount());
        assertEquals(20000L, data.getCoinAmount());

    }

    @Test
    void getCashoutProductSuccess_Test(){
        CashoutProduct cashoutProduct = CashoutProduct.builder()
                .id(1L)
                .name("Cashout 20K")
                .coinAmount(20000L)
                .balanceAmount(20000L)
                .build();

        List<CashoutProduct> cashoutProductList = new ArrayList<>();
        cashoutProductList.add(cashoutProduct);

        when(cashoutProductRepository.findAllSorted()).thenReturn(cashoutProductList);

        ResponseEntity<Object> response = cashoutProductService.getCashoutProducts();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<CashoutProductDto> data = (List<CashoutProductDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get cashout product success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1, data.size());
    }

    @Test
    void updateCashoutProductSuccess_Test(){
        CashoutProduct cashoutProduct = CashoutProduct.builder()
                .id(1L)
                .name("Cashout 20K")
                .coinAmount(20000L)
                .balanceAmount(20000L)
                .build();

        CashoutProductDto cashoutProductDto = CashoutProductDto.builder()
                .id(1L)
                .name("Cashout 25K")
                .coinAmount(25000L)
                .balanceAmount(25000L)
                .build();

        when(cashoutProductRepository.findById(any())).thenReturn(Optional.ofNullable(cashoutProduct));
        when(cashoutProductRepository.save(any())).thenReturn(cashoutProduct);

        ResponseEntity<Object> response = cashoutProductService.updateCashoutProduct(cashoutProductDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        CashoutProductDto data = (CashoutProductDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Update cashout product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals("Cashout 25K", data.getName());
        assertEquals(25000L, data.getBalanceAmount());
        assertEquals(25000L, data.getCoinAmount());
    }

    @Test
    void updateCashoutProductNotFound_Test(){
        CashoutProductDto cashoutProductDto = CashoutProductDto.builder()
                .name("Cashout 25K")
                .coinAmount(25000L)
                .balanceAmount(25000L)
                .build();

        when(cashoutProductRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = cashoutProductService.updateCashoutProduct(cashoutProductDto, 1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Cashout product not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteCashoutProductSuccess_Test() {
        CashoutProduct cashoutProduct = CashoutProduct.builder()
                .id(1L)
                .name("Cashout 20K")
                .coinAmount(20000L)
                .balanceAmount(20000L)
                .build();


        when(cashoutProductRepository.findById(any())).thenReturn(Optional.ofNullable(cashoutProduct));

        ResponseEntity<Object> response = cashoutProductService.deleteCashoutProduct(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete cashout product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }

    @Test
    void deleteCashoutProductNotFound_Test(){
        when(cashoutProductRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = cashoutProductService.deleteCashoutProduct(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Cashout product not found", apiResponse.getMessage());
        assertEquals("400", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
