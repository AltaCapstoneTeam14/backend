package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.dao.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BalanceDto {
    private Long id;

    private User user;

    private Long amount;
}
