package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankChargeDto {
    @JsonProperty("order_id")
    private Long orderId;

    private String bank;

    private String status;

    @JsonProperty("va_number")
    private String vaNumber;
}
