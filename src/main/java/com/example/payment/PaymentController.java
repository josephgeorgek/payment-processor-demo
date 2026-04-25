package com.example.payment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentProcessorService processorService;
    private final PaymentProperties properties;

    public PaymentController(PaymentProcessorService processorService, PaymentProperties properties) {
        this.processorService = processorService;
        this.properties = properties;
    }

    @GetMapping("/fees")
    public ResponseEntity<Map<String, Double>> fees() {
        return ResponseEntity.ok(properties.getFees());
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> process(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(processorService.process(request));
    }
}
