# Release Notes

## 1.0.0

- Initial release

## 1.0.1

- Support for product intelligence.

## 1.0.2

- Support for merchant (Google and Yelp integration)
- Bug fixes

## 1.0.3

- Bug fixes

## 1.0.4

- Bug fixes
- JNI Improvements

## 1.0.5

- Bug fixes
    - Summary mapping fix
    - Camera scan activity tool tip intent fix
    - Populate total for products
- Updated third part libraries ( retrofit, scandit, okio )
- Network optimizations
- Additional lines added to Scan Results
- Changed store name to merchant name
- Map payments from string to a payment object.
- Camera frame characteristics store() to storeFrames()

## 1.0.6

- Bug fixes
- Logo Detection

## 1.0.7

- Bug fixes

## 1.0.8

- Bug fixes

## 1.0.9

- Bug fixes

## 1.1.0

- Bug fixes

## 1.1.2

- Memory Optimizations
- Bug fixes

## 1.1.3

- Bug fixes

## 1.1.4

- Voided Products
- Duplicate Detection
- Bug fixes

# 1.1.5

- Auto configuration can now be disabled by adding a meta data id to the manifest.
- Duplicate blink receipt id for duplicate receipts.
- Merchant guess
- Payment method returns Payment Method, Card Type, Card Issuer

# 1.1.6

- The OkHttp 3.12.x branch supports Android 2.3+ (API level 9+) and Java 7+. These platforms lack support for TLS 1.2 and should not be used. But because upgrading is difficult we will backport critical fixes to the 3.12.x branch through December 31, 2020.