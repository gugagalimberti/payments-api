package com.galimberti.paymentsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
        @Schema(example = "150.90") @NotNull @Positive @Digits(integer = 8, fraction = 2) Double amount, // numeric(10,2)
        @Schema(example = "Supermercado") @NotBlank @Size(max = 50) String note,                       // varchar(50)
        @Schema(example = "1577817000000", description = "Data em epoch (milissegundos)") @NotNull Long transactionDate) {}

