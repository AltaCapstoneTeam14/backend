package com.alterra.capstone14.domain.resBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VANumber {
    private String bank;

    @JsonProperty("va_number")
    private String vaNumber;

    @Override
    public String toString() {
        return "[" +
                "bank='" + bank + '\'' +
                ", vaNumber='" + vaNumber + '\'' +
                "]";
    }
}
