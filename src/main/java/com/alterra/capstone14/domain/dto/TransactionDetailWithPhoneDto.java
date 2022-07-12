package com.alterra.capstone14.domain.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransactionDetailWithPhoneDto extends TransactionDetailDto{

    @NotBlank(message = "phone is required!")
    @Pattern(regexp = "^[0-9]+$", message="Phone value must be number")
    @Size(min = 10, max = 18, message = "The length of phone must be between 10 and 18 characters.")
    private String phone;

}
