# Payment Processor Demo

Spring Boot 3.5 / Java 21 demo app for an auto-healing scenario.

## Scenario

`payment-ms` processes payment transactions. `SWIFT_INTL` is deliberately missing from fee configuration, causing a runtime `NullPointerException` during fee calculation.

The auto-healer should classify this as:

```text
CONFIG_REFERENCE_DATA_GAP
```

## Run

```bash
mvn clean spring-boot:run
```

Service starts on:

```text
http://localhost:9088
```

## APIs

### Health

```bash
curl http://localhost:9088/actuator/health
```

### List Fee Configuration

```bash
curl http://localhost:9088/api/payments/fees
```

### Successful Domestic Payment

```bash
curl -X POST http://localhost:9088/api/payments/process \
  -H 'Content-Type: application/json' \
  -d '{"type":"DOMESTIC","amount":5000}'
```

Expected total:

```text
5000 + 2% fee + 5% tax = 5350.0
```

### Failing SWIFT Payment

```bash
curl -i -X POST http://localhost:9088/api/payments/process \
  -H 'Content-Type: application/json' \
  -d '{"type":"SWIFT_INTL","amount":5000}'
```

Expected failure:

```text
500 Internal Server Error
classificationHint: CONFIG_REFERENCE_DATA_GAP
```

## File Audit Log

Audit log is written to:

```text
logs/gateway_audit.log
```

View it:

```bash
tail -f logs/gateway_audit.log
```

## Manual Heal for Demo

Add this to `src/main/resources/application.properties`:

```properties
payment.fees.SWIFT_INTL=0.03
```

Restart:

```bash
mvn spring-boot:run
```

Then test again:

```bash
curl -X POST http://localhost:9088/api/payments/process \
  -H 'Content-Type: application/json' \
  -d '{"type":"SWIFT_INTL","amount":5000}'
```

Expected:

```text
5000 + 3% fee + 5% tax = 5400.0
```

## Auto-Healer Demo Output

```text
[DETECT] Runtime fault detected in payment-ms
[ANALYZE] NullPointerException during fee calculation
[CLASSIFY] CONFIG_REFERENCE_DATA_GAP
[ROOT_CAUSE] Missing fee mapping for SWIFT_INTL
[PLAN] Add SWIFT_INTL fee config = 3%
[FIX] Config patch applied in Pre-Prod
[RESTART] payment-ms restarted
[TEST] SWIFT_INTL transaction replayed
[SUCCESS] Integrity Check Passed. Total to SWIFT: 5400.0
[STATUS] HEALED
[PROMOTE] Awaiting PROD approval
```
