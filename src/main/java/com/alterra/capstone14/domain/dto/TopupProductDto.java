package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopupProductDto {
    Long id;

    @NotBlank(message = "name is required")
    String name;

    @NotNull(message = "amount is required")
    @Min(value = 1, message = "amount is at least 1")
    Long amount;

    @NotNull(message = "gross_amount is required")
    @Min(value = 1, message = "gross_amount is at least 1")
    @JsonProperty("gross_amount")
    Long grossAmount;

}
