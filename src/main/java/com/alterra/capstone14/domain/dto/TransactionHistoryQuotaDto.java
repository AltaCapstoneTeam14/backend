package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryQuotaDto {
    private Long id;

    private String provider;

    private String description;

    private String phone;
}
