package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.dao.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {
    private Long id;

    private User user;

    private Long amount;
}
