package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionPulsaDto {
    @JsonProperty("product_id")
    @NotBlank(message = "product_id is required!")
    private Long productId;

    @NotBlank(message = "phone is required!")
    @Pattern(regexp = "^[0-9]+$", message="phone value must be number")
    @Size(min = 10, max = 18, message = "The length of phone must be between 10 and 18 characters.")
    private String phone;
}
