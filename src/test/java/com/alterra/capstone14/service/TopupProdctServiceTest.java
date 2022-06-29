package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dto.TopupProductDto;
import com.alterra.capstone14.repository.TopupProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TopupProductService.class)
public class TopupProdctServiceTest {
    @MockBean
    private TopupProductRepository topupProductRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private TopupProductService topupProductService;

    @Test
    void addTopupProductSuccess_Test(){
        TopupProduct topupProduct = TopupProduct.builder()
                .id(1L)
                .amount(100000L)
                .grossAmount(101000L)
                .build();

        TopupProductDto topupProductDto = TopupProductDto.builder()
                .id(1L)
                .amount(100000L)
                .grossAmount(101000L)
                .build();

        when(modelMapper.map(any(), eq(TopupProduct.class))).thenReturn(topupProduct);
        when(modelMapper.map(any(), eq(TopupProductDto.class))).thenReturn(topupProductDto);
        when(topupProductRepository.save(any())).thenReturn(topupProduct);

        ResponseEntity<Object> response = topupProductService.addTopupProduct(
                TopupProductDto.builder().amount(100000L).grossAmount(101000L).build());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        TopupProductDto data = (TopupProductDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Add topup product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(1L, data.getId());
        assertEquals(100000L, data.getAmount());
        assertEquals(101000L, data.getGrossAmount());
    }

    @Test
    void getTopupProductsSuccess_Test(){
        TopupProduct topupProduct = TopupProduct.builder()
                .id(1L)
                .amount(100000L)
                .grossAmount(101000L)
                .build();

        List<TopupProduct> topupProductList = new ArrayList<>();
        topupProductList.add(topupProduct);

        when(topupProductRepository.findAll()).thenReturn(topupProductList);

        ResponseEntity<Object> response = topupProductService.getTopupProducts();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<TopupProductDto> data = (List<TopupProductDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals(1, data.size());
        assertEquals("Get topup product success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
    }

    @Test
    void deleteTopupProductSuccess_Test(){
        TopupProduct topupProduct = TopupProduct.builder()
                .id(1L)
                .amount(100000L)
                .grossAmount(101000L)
                .build();

        when(topupProductRepository.findById(1L)).thenReturn(Optional.ofNullable(topupProduct));

        ResponseEntity<Object> response = topupProductService.deleteTopupProduct(1L);

        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Delete topup product success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
