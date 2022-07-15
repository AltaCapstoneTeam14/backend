package com.alterra.capstone14.domain.thirdparty.res;

import com.alterra.capstone14.domain.dto.SubscriberDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllSubscriberRes {
    List<SubscriberDto> contacts;
    int count;
}
