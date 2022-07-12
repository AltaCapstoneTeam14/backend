package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.dao.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeeklyLoginDto {
    private Long id;

    private User user;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("login_count")
    private Integer loginCount;

    @JsonProperty("last_login")
    private Integer lastLogin;

    @JsonProperty("coin_reward")
    private Long coinReward;

    private String status;
}
