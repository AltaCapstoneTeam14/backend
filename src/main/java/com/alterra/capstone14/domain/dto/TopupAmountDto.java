package com.alterra.capstone14.domain.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopupAmountDto {
    Long id;

    @NotBlank(message = "Amount is required")
    Integer amount;
}
