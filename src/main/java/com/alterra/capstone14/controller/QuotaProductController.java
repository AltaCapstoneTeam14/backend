package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.QuotaProductDto;
import com.alterra.capstone14.service.QuotaProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/products/quota")
public class QuotaProductController {
    @Autowired
    QuotaProductService quotaProductService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getQuotaProductList() {
        return quotaProductService.getQuotaProducts();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> addQuotaProduct(@Valid @RequestBody QuotaProductDto quotaProductDto) {
        return quotaProductService.addQuotaProduct(quotaProductDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> updateQuotaProduct(@Valid @RequestBody QuotaProductDto quotaProductDto, @PathVariable Long id) {
        return quotaProductService.updateQuotaProduct(quotaProductDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> deleteQuotaProduct(@PathVariable Long id) {
        return quotaProductService.deleteQuotaProduct(id);
    }
}
