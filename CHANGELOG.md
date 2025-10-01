# Changelog

## 2.0.0 (2025-10-01)

For full migrate guild see [MIGRATION.md](./MIGRATION.md).

### Features

- **api:** add `/v2/payment-request/invoices`
- **api:** add `/v1/payouts`
- **api:** add `/v1/payouts-account`
- **client:** add default value for credential read from environment variable
- **client:** add `.getCrypto` to calculate signature for payment-requests and payouts signature
- **client:** add additional options to all method
- **client:** add pagination support for get list request
- **client:** add retry for rate limit request
- **client:** add Exception subclass to handle api error, webhook error and signature error for better error handling
- **client:** add support for request download file

### Documentation

- **readme:** update readme
