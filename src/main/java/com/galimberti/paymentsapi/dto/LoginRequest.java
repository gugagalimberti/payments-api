package com.galimberti.paymentsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "usuario@exemplo.com") @NotBlank @Email String email,
        @Schema(example = "123456") @NotBlank String password) {}

