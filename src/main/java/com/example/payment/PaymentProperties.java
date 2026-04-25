package com.example.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {
    private Map<String, Double> fees = new HashMap<>();
    private double taxRate = 0.05;
    private Audit audit = new Audit();

    public Map<String, Double> getFees() {
        return fees;
    }

    public void setFees(Map<String, Double> fees) {
        this.fees = fees;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public static class Audit {
        private String logFile = "logs/gateway_audit.log";

        public String getLogFile() {
            return logFile;
        }

        public void setLogFile(String logFile) {
            this.logFile = logFile;
        }
    }
}
