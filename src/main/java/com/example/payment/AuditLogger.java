package com.example.payment;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Component
public class AuditLogger {
    private final Path auditLogPath;

    public AuditLogger(PaymentProperties properties) throws IOException {
        this.auditLogPath = Path.of(properties.getAudit().getLogFile());
        Path parent = auditLogPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(auditLogPath)) {
            Files.createFile(auditLogPath);
        }
    }

    public synchronized void write(String status, String type, double amount, double total, String message) {
        String line = "%s | STATUS: %s | TYPE: %s | AMOUNT: %.2f | VAL: %.2f | MSG: %s%n"
                .formatted(LocalDateTime.now(), status, type, amount, total, sanitize(message));
        try {
            Files.writeString(
                    auditLogPath,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("[AUDIT_LOG_FAILURE] " + e.getMessage());
        }
    }

    private String sanitize(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("\n", " ").replace("\r", " ");
    }
}
