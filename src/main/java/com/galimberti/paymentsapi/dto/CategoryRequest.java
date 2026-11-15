package com.galimberti.paymentsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest( 
        @Schema(example = "Mercado") @NotBlank @Size(max = 20) String title,             // varchar(20)
        @Schema(example = "Compras do mês") @NotBlank @Size(max = 50) String description) {} // varchar(50)
