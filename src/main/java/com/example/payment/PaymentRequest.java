package com.example.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record PaymentRequest(
        @NotBlank String type,
        @DecimalMin(value = "0.01", message = "amount must be greater than zero") double amount
) {}
