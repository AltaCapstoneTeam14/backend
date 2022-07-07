package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashoutProductDto {
    private Long id;

    @NotBlank(message = "name is required!")
    private String name;

    @NotNull(message = "coin_amount is required!")
    @JsonProperty("coin_amount")
    private Long coinAmount;

    @NotNull(message = "balance_amount is required!")
    @JsonProperty("balance_amount")
    private Long balanceAmount;

}
