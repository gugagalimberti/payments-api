package com.galimberti.paymentsapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 20) String firstName,      // varchar(20)
        @NotBlank @Size(max = 20) String lastName,       // varchar(20)
        @NotBlank @Email @Size(max = 30) String email,   // varchar(30)
        @NotBlank String password) {}                    // text (é guardada como hash)

