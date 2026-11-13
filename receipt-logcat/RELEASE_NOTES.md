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
- Remove support for armeabi-v7a
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
- Add support for armeabi-v7a
- Stability fixes and improvements

## 1.1.0
- Stability fixes and improvements

## 1.1.1
- Stability fixes and improvements

## 1.1.2
- Upgrade Java compilation target from version 8 to 17 across all modules
    - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Stability fixes and improvements

## 1.1.3
- **BREAKING CHANGE** minSDK set to 23 | targetSDK set to 36
- Upgrade Java compilation target from version 8 to 17 across all modules
  - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Stability fixes and improvements

## 1.1.4
- Stability fixes and improvements

## 1.1.5
- Stability fixes and improvements

## 1.1.6
- Stability fixes and improvements

## 1.1.7
- Stability fixes and improvements

## 1.1.8
- Stability fixes and improvements

## 2.0.0
- Stability fixes and improvements

## 2.0.1
- Stability fixes and improvements

## 2.0.2
- Stability fixes and improvements

## 2.0.3
- Stability fixes and improvements

## 2.1.0
- We’ve increased the minimum supported Android SDK version from API 23 (Android 6.0 Marshmallow) to API 24 (Android 7.0 Nougat). This allows us to take advantage of newer platform capabilities and improve overall app quality.
- Stability fixes and improvements

## 2.1.1
- Stability fixes and improvements

## 2.2.0
- Stability fixes and improvements

## 2.2.1
- Stability fixes and improvements

## 2.2.2
- Stability fixes and improvements

## 2.3.0
- Stability fixes and improvements

## 2.3.1
- Hardened the published R8/ProGuard rules so the `LogEvent` companion object keeps its name in the release AAR, matching the field that references it. Nothing was reachable through this in 2.3.0 — `LogEvent.NONE` is a `@JvmField` member resolved on the outer class — so no integration changes are required.
- Stability fixes and improvements

## 2.3.2
- Stability fixes and improvements

## 2.4.0
- Stability fixes and improvements

## 2.4.1
- Stability fixes and improvements

## 2.5.0
- Stability fixes and improvements

## 2.5.1
- Stability fixes and improvements

## 2.5.2
- Stability fixes and improvements

## 3.0.0
- Stability fixes and improvements
- **BREAKING CHANGE** The Maven coordinates move from `com.microblink.blinkreceipt:blinkreceipt-logcat` to `com.actualplatform:receipt-logcat`. Gradle treats them as different libraries, so a version bump alone doesn't upgrade: change the BOM to `com.actualplatform:receipt-bom` and every SDK dependency. The library declares its old coordinates as a Gradle capability, so a 2.x copy left on the classpath fails dependency resolution with a capability conflict; remove it.
- **BREAKING CHANGE** Packages move from `com.microblink.logcat` to `com.actualplatform.receipt.logcat`, and the AGP namespace with them, so the generated `R` class is `com.actualplatform.receipt.logcat.R`. Update imports, code and app keep rules that name SDK classes; keep rules that still name `com.microblink.logcat` stop matching silently.
