package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionCashoutDto {
    @JsonProperty("product_id")
    @NotBlank(message = "product_id is required!")
    private Long productId;
}
