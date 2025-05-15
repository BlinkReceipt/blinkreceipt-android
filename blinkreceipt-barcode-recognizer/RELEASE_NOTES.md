# Release Notes

## 1.0.0
- Support for 16KB memory page size on Android 15+ required for all app updates starting Nov 1, 2025—ensure native code and SDKs are rebuilt with updated NDK and tools.
- Integrated ML Kit's Barcode Scanning API to detect and extract barcode information from receipt images. This enables users to quickly capture and process receipts.

## 1.0.1
- Stability fixes and improvements

## 1.0.2
- Stability fixes and improvements

## 1.0.3
- Upgrade Java compilation target from version 8 to 17 across all modules
    - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Stability fixes and improvements

## 1.0.4
- **BREAKING CHANGE** Relocated SDK distribution from https://maven.microblink.com to Maven Central.
- **BREAKING CHANGE** Refactored `Barcode` and `BarcodeType` models to instead use the ones from `com.microblink.core` package.
- **BREAKING CHANGE** minSDK set to 23 | targetSDK set to 36
- Upgrade Java compilation target from version 8 to 17 across all modules
  - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Removed ~~com.microblink.barcode.recognizer.Barcode~~ and ~~com.microblink.barcode.recognizer.BarcodeType~~ in favor of `com.microblink.core.Barcode` and `com.microblink.core.BarcodeType` models respectively.
  - `com.microblink.barcode.recognizer.BarcodeResults` class now uses `com.microblink.core.Barcode` as its barcodes field type.
- Stability fixes and improvements

## 1.0.5
- Fixed ML Kit's Barcode Integration producing no results caused by the original implementation where models were not downloaded/activated.
- Stability fixes and improvements

## 1.0.6
- Stability fixes and improvements
