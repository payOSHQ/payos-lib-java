# Changelog

## 2.0.2 (2026-10-30)

### Bug Fixes

- **webhooks:** stricter validation of raw webhook data before signature verification; constant-time comparison ([GHSA-mc2r-v3x7-j5rv](https://github.com/payOSHQ/payos-lib-java/security/advisories/GHSA-mc2r-v3x7-j5rv))
- **client:** encode identifiers interpolated into API paths (no double encoding) and reject dot segments, empty values and negative/unsafe integers; follow only 301/302/303/307/308 redirects manually (at most 5; exceeding the limit fails without retrying) and, once a redirect chain leaves the API origin, stop sending `x-api-key`/`x-client-id`/`x-partner-code`/`authorization`/`cookie`/`proxy-authorization` headers and `CookieJar` cookies for the rest of the chain, including for user-supplied `OkHttpClient`s that follow redirects (a client built with `followRedirects(false)` is left unchanged); validate body-signed response data before signature verification; async `invoices().get(...)` now calls `/v2/payment-requests/{id}/invoices`

## 2.0.1 (2025-10-10)

### Refactor

- **client:** update `OffsetDatetime` to `String` in models

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
