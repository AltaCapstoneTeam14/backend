package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.PulsaProductDto;
import com.alterra.capstone14.service.PulsaProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Path;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/products/pulsa")
public class PulsaProductController {
    @Autowired
    PulsaProductService pulsaProductService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getPulsaProductList() {
        return pulsaProductService.getPulsaProducts();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> addPulsaProduct(@Valid @RequestBody PulsaProductDto pulsaProductDto) {
        return pulsaProductService.addPulsaProduct(pulsaProductDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> updatePulsaProduct(@Valid @RequestBody PulsaProductDto pulsaProductDto, @PathVariable Long id) {
        return pulsaProductService.updatePulsaProduct(pulsaProductDto, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> deletePulsaProduct(@PathVariable Long id) {
        return pulsaProductService.deletePulsaProduct(id);
    }
}
