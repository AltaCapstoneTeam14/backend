package com.alterra.capstone14.domain.thirdparty.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
