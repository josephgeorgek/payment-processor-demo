package com.example.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentProcessorService {
    private final PaymentProperties properties;
    private final AuditLogger auditLogger;

    public PaymentProcessorService(PaymentProperties properties, AuditLogger auditLogger) {
        this.properties = properties;
        this.auditLogger = auditLogger;
    }

    public PaymentResponse process(PaymentRequest request) {
        String txType = request.type().trim().toUpperCase();
        double amount = request.amount();

        System.out.println("[PROCESS] Orchestrating " + txType + " transaction for $" + amount);

        try {
            // Intentional demo failure: missing SWIFT_INTL config returns null.
            // Auto-healer should classify this as CONFIG_REFERENCE_DATA_GAP.
            Double feeRate = properties.getFees().get(txType);
            double finalFee = amount * feeRate;
            double tax = amount * properties.getTaxRate();
            double total = amount + finalFee + tax;

            String successMessage = "Integrity Check Passed. Total to SWIFT: " + total;
            System.out.println("[SUCCESS] " + successMessage);
            auditLogger.write("TX_SUCCESS", txType, amount, total, successMessage);

            return new PaymentResponse(
                    "SUCCESS",
                    txType,
                    amount,
                    feeRate,
                    finalFee,
                    properties.getTaxRate(),
                    tax,
                    total,
                    successMessage
            );
        } catch (Exception e) {
            auditLogger.write("TX_CRITICAL_FAULT", txType, amount, 0, e.getClass().getSimpleName() + ": " + e.getMessage());
            System.err.println("[FATAL] Runtime Exception in Settlement Layer:");
            e.printStackTrace(System.err);
            throw e;
        }
    }
}
