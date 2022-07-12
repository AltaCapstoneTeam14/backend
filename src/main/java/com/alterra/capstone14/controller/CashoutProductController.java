package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.CashoutProductDto;
import com.alterra.capstone14.service.CashoutProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/products/cashout")
public class CashoutProductController {
    @Autowired
    CashoutProductService cashoutProductService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getCashoutProductList() {
        return cashoutProductService.getCashoutProducts();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> addCashoutProduct(@Valid @RequestBody CashoutProductDto cashoutProductDto) {
        return cashoutProductService.addCashoutProduct(cashoutProductDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> updateCashoutProduct(@Valid @RequestBody CashoutProductDto cashoutProductDto, @PathVariable Long id) {
        return cashoutProductService.updateCashoutProduct(cashoutProductDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> deleteCashoutProduct(@PathVariable Long id) {
        return cashoutProductService.deleteCashoutProduct(id);
    }
}
