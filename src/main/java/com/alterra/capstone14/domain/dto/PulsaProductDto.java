package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.dao.Provider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PulsaProductDto {
    private Long id;

    @NotBlank(message = "name is required!")
    private String name;

    @NotNull(message = "provider_id is required!")
    @Min(value = 1, message = "denom is at least 1")
    @JsonProperty("provider_id")
    private Long providerId;

    @NotNull(message = "denom is required!")
    @Min(value = 1, message = "denom is at least 1")
    private Long denom;

    @NotNull(message = "gross_amount is required!")
    @Min(value = 1, message = "gross_amount is at least 1")
    @JsonProperty("gross_amount")
    private Long grossAmount;

    @NotNull(message = "stock is required!")
    @Min(value = 1, message = "stock is at least 1")
    private Long stock;

    private Provider provider;

    @JsonProperty("provider_name")
    private String providerName;
}
