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

# 1.1.7

- minSDK 21
- Target Java 8
- Migrated to the latest receipt model
- Improved purchase validation
- Edge detection rect is now available in the public interface

# 1.1.8

- Purchase validation improvements
- Bug Fixes

# 1.1.9

- Amazon account manager bug fix
- Improved duplicate search engine
- Bug Fixes

# 1.2.0

- Promotion Bug Fixes

# 1.2.1

Product Updates
    - Improved memory management
    - Updated OCR models
    - Costco Date improvements
    - Overall parser improvements 
Bug Fixes    
    - Detect internal banner id
    - E-Receipts pass description from native context
Third Party Libraries
    - Retrofit 2.6.0
    - OkHttp 3.14.2
    - Gmail v1-rev105-1.25.0
    - Google Api Client 1.30.1
    - Google Api Services 1.29.2

# 1.2.2

- Bug Fixes

# 1.2.3

- Edge detection fixes 
- Incorporate full price into product intelligence
- Incorporate fuzzy search into product intelligence 
- Merchant detection fixes
- Amazon improvements 
- Support for purchase type
- Parser improvements

# 1.2.4

- Fix crash in edgee detection 

# 1.2.5

- AndroidX support is now a requirement
- Edge detection performance improvements
- Camera management performance and stability improvements
- Amazon streaming parsing and prime support
- Security stability improvements
- Camera capture listener change interface exception to throwable