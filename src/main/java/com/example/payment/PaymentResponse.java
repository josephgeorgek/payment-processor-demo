package com.example.payment;

public record PaymentResponse(
        String status,
        String type,
        double amount,
        double feeRate,
        double fee,
        double taxRate,
        double tax,
        double total,
        String message
) {}
