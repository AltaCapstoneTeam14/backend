package com.alterra.capstone14.domain.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    @NotBlank(message = "name is required!")
    private String name;

    @NotBlank(message = "phone is required!")
    @Pattern(regexp = "^[0-9]+$", message="phone value must be number")
    @Size(min = 10, max = 18, message = "The length of phone must be between 10 and 18 characters.")
    private String phone;

    @NotBlank(message = "balance is required!")
    @Pattern(regexp = "^[0-9]+$", message="balance value must be number")
    private String balance;

    @NotBlank(message = "coin is required!")
    @Pattern(regexp = "^[0-9]+$", message="coin value must be number")
    private String coin;
}
