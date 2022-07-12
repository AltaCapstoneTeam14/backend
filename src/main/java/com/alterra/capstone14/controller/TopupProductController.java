package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.TopupProductDto;
import com.alterra.capstone14.service.TopupProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/products/topup")
public class TopupProductController {
    @Autowired
    TopupProductService topupAmountService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getTopupProductList() {
        return topupAmountService.getTopupProducts();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> addTopupProduct(@Valid @RequestBody TopupProductDto topupProductDto) {
        return topupAmountService.addTopupProduct(topupProductDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> updateTopupProduct(@Valid @RequestBody TopupProductDto topupProductDto, @PathVariable Long id) {
        return topupAmountService.updateTopupProduct(topupProductDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> deleteTopupProduct(@PathVariable Long id) {
        return topupAmountService.deleteTopupProduct(id);
    }
}
