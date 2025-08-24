# Release Notes

## 1.0.0
- initial release

## 1.0.1
- Stability fixes and improvements

## 1.0.2
- Stability fixes and improvements

## 1.0.3
- Stability fixes and improvements

## 1.0.4
- Stability fixes and improvements

## 1.0.5
- Stability fixes and improvements

## 1.0.6
- Stability fixes and improvements

## 1.0.7
- Stability fixes and improvements

## 1.0.8
- Stability fixes and improvements

## 1.0.9
- Support for 16KB memory page size on Android 15+ required for all app updates starting Nov 1, 2025—ensure native code and SDKs are rebuilt with updated NDK and tools.
- Stability fixes and improvements

## 1.1.0
- Support for 16KB memory page size on Android 15+ required for all app updates starting Nov 1, 2025—ensure native code and SDKs are rebuilt with updated NDK and tools.
- Stability fixes and improvements

## 1.1.1
- Remote Message Job IDs are now included as part of digital analytics
- Stability fixes and improvements

## 1.1.2
- Upgrade Java compilation target from version 8 to 17 across all modules
    - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Stability fixes and improvements

## 1.1.3
- **BREAKING CHANGE** Relocated SDK distribution from https://maven.microblink.com to Maven Central.
- **BREAKING CHANGE** minSDK set to 23 | targetSDK set to 36
- Upgrade Java compilation target from version 8 to 17 across all modules
  - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Stability fixes and improvements

## 1.1.4
- Stability fixes and improvements

## 1.1.5
- Stability fixes and improvements

## 2.0.0-beta01
- Stability fixes and improvements

## 2.0.0-beta02
- Stability fixes and improvements

## 2.0.0-beta03
- Optimize database usage by storing orders.raw_data payload in a separate file storage to avoid possible OOM issues and better overall performance.
- Refactored Account Linking Analytics implementation, replaced with Digital API `operation_events` implementation.
- Stability fixes and improvements

## 2.0.0-beta04
- Stability fixes and improvements
