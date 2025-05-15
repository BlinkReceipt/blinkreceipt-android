# Release Notes

## 1.0.0

- Initial release

## 1.0.1

- Update Play Service Task framework to 17.0.2
- Removed internal location look up services.
- Add product name & image url to corrections on products.
- Blink Receipt Core 1.0.1

## 1.0.2

- blink receipt Core 1.0.2
- blink receipt Camera 1.0.2
- okhttp 4.7.0
- okio : 2.6.0
- retrofit: 2.8.2

## 1.0.3

- bug fixes
- update Play Service Task framework to 17.1.0
- fix proguard rules
- product intelligence required to make corrections.

## 1.0.4

- generate x86_64 slice
- fix overriding corrections with new product intelligence response data.
- Bug fixes

## 1.0.5

- sensitive product bug fix

## 1.0.6

- okio 2.7.0
- okhttp 4.8.1
- retrofit: 2.9.0
- build tools 30.0.1

## 1.0.7

- Bug fix for panera bread

## 1.0.8

- web scan results should use banner id associated with web request and not the retailer.
- okhttp 4.9.0
- okio 2.8.0
- update Play Service Task framework to 17.2.0

## 1.0.9

- stability fixes
- okio 2.9.0

## 1.1.0

- license key is required to initialize the SDK
- downgrade target to api 29, but compiled against api 30
- product intel optimizations (fuzzy)

## 1.1.1

- bug fixes

## 1.1.2

- internal serialization performance

## 1.1.3

- stability fixes and improvements
- okio version 2.10.0
- gms tasks updated to 17.2.1
- Work around a crash in Android 10 and 11 that may be triggered when two threads concurrently close an SSL socket. This would have appeared in crash logs as NullPointerException: bio == null
- kotlin 1.4.31

## 1.1.4

- stability fixes and improvements
- harden native context & encryption protocols

## 1.1.5

- stability fixes and improvements

## 1.1.6

- stability fixes and improvements

## 1.1.7

- stability fixes and improvements

## 1.1.8

- stability fixes and improvements

## 1.1.9

- stability fixes and improvements

## 1.2.0

- stability fixes and improvements
- timber 5.0.0

## 1.2.1

- stability fixes and improvements
- timber 5.0.1

## 1.2.2

- stability fixes and improvements
- okhttp 4.9.2

## 1.2.3

- okio version 2.10.0
- stability fixes and improvements

## 1.2.4

- stability fixes and improvements

## 1.2.5

- stability fixes and improvements

## 1.2.6

- stability fixes and improvements
- androidx appcompat updated to 1.3.1
- okio version 3.0.0
- gms tasks updated to 18.0.1

## 1.2.7

- stability fixes and improvements
- upgraded to okhttp 4.9.3

## 1.2.8

- stability fixes and improvements

## 1.2.9

- stability fixes and improvements

## 1.3.0

- stability fixes and improvements

## 1.3.1

- stability fixes and improvements

## 1.3.2

- stability fixes and improvements

## 1.3.3

- stability fixes and improvements

## 1.3.4

- stability fixes and improvements
- Remove support for x86 as 32-bit x86 has dropped to a very small number of active devices.

## 1.3.5

- Stability fixes and improvements

## 1.3.6

- Stability fixes and improvements

## 1.3.7

- Stability fixes and improvements

## 1.3.8

- Stability fixes and improvements
- Updated to target and compile API Level 33

## 1.3.9
- Added distribution via Maven. You no longer have to declare our transitive dependencies, but can only declare dependencies on our specific maven libraries. Check out the Readme for more details.
- Stability fixes and improvements

## 1.4.0
- Stability fixes and improvements

## 1.4.1
- Changed internal dependencies from api to implementation. Please include the following:
    - implementation "com.google.android.gms:play-services-tasks:18.0.2"
- Stability fixes and improvements

## 1.4.2
- Stability fixes and improvements

## 1.4.3
- Stability fixes and improvements

## 1.4.4
- Stability fixes and improvements

## 1.4.5
- Stability fixes and improvements

## 1.4.6
- Stability fixes and improvements

## 1.4.7
- Stability fixes and improvements

## 1.4.8
- Stability fixes and improvements

## 1.4.9
- Stability fixes and improvements

## 1.5.0
- Stability fixes and improvements

## 1.5.1
- Stability fixes and improvements

## 1.5.2
- Stability fixes and improvements

## 1.5.3
- **BREAKING CHANGE** Remove debug property from public api surfaces
- Stability fixes and improvements

## 1.5.4
- Stability fixes and improvements

## 1.5.5
- Updated to target API Level 34
- Stability fixes and improvements

## 1.5.6
- Stability fixes and improvements

## 1.5.7
- Stability fixes and improvements

## 1.5.8
- Stability fixes and improvements

## 1.5.9
- Stability fixes and improvements

## 1.6.0
- Stability fixes and improvements

## 1.6.1
- Stability fixes and improvements

## 1.6.2
- Stability fixes and improvements

## 1.6.3
- Stability fixes and improvements

## 1.6.4
- Stability fixes and improvements

## 1.6.5
- Stability fixes and improvements

## 1.6.6
- **BREAKING CHANGE** Relocated SDK distribution from https://maven.microblink.com to Maven Central.
- Support for 16KB memory page size on Android 15+ required for all app updates starting Nov 1, 2025—ensure native code and SDKs are rebuilt with updated NDK and tools.
- Stability fixes and improvements

