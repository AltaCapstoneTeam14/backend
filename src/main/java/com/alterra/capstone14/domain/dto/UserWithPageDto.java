package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWithPageDto {
    List<UserWithBalanceDto> users;

    int page;

    @JsonProperty("page_available")
    int pageAvailable;
}
