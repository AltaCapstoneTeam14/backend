package com.alterra.capstone14.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberDto {
    private Long id;

    @NotBlank(message = "Email is required!")
    @Email(message = "The email address is invalid.")
    private String email;

}
