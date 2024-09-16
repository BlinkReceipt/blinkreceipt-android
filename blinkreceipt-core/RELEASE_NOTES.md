# Release Notes

## 1.0.0

- Initial release

## 1.0.1

- Update Play Service Task framework to 17.0.2
- Removed internal location look up services.

## 1.0.2

- Don't pass empty or null message to internal logger.

## 1.0.3

- bug fixes
- Update Play Service Task framework to 17.1.0

## 1.0.4

- generate x86_64 slice
- Bug fixes

## 1.0.5

- sensitive product bug fix

## 1.0.6

- okio 2.7.0
- okhttp 4.8.1
- build tools 30.0.1
- Androidx crypto library to encrypt local storage( required )
    -implementation "androidx.security:security-crypto:1.1.0-alpha02"

## 1.0.7

- Bug Fix for panera bread

## 1.0.8

- okhttp 4.9.0
- okio 2.8.0
- override internal logger instance
- Update Play Service Task framework to 17.2.0

## 1.0.9

- core stability fixes
- okio 2.9.0

## 1.1.0

-***Breaking Change*** Product totalPrice type changed from float to FloatType
- downgrade target to api 29, but compiled against api 30

## 1.1.1

- PasswordCredentials moved to core package to share across different sdk surfaces

## 1.1.2

- security: updated Tink to stable release 1.5.0
- internal serialization performance

## 1.1.3

- stability fixes and improvements
- okio version 2.10.0
- gms tasks updated to 17.2.1
- Work around a crash in Android 10 and 11 that may be triggered when two threads concurrently close an SSL socket. This would have appeared in crash logs as NullPointerException: bio == null
- Add PVP Activation caching ability
- kotlin 1.4.31

## 1.1.4

- stability fixes and improvements

## 1.1.5

- stability fixes and improvements
- New Product Intelligence fields added
    - sector
    - department
    - majorCategory
    - subCategory
    - itemType
    - attributes: Zero or more attributes related to the product. Each attribute is a dictionary with a single key-value pair, representing the name of the attribute and its value

## 1.1.6

- stability fixes and improvements
- added kotlin, coroutines, androidx core, worker manager and preference dependencies for more efficent internal processing

## 1.1.7

- stability fixes and improvements
- kotlin 1.5.10
- provide merchant email, shipping cost and email subject via scan results
- provide currency code via scan results

## 1.1.8

- stability fixes and improvements
- support for additional date formats

## 1.1.9

- stability fixes and improvements

## 1.2.0

- stability fixes and improvements
- kotlin 1.5.21
- update banner id for specific retailers
- fix extended field mapping
- timber 5.0.0

## 1.2.1

- stability fixes and improvements
- timber 5.0.1
- kotlin 1.5.30
- update coroutines to 1.5.2

## 1.2.2

- stability fixes and improvements
- kotlin 1.5.31
- remove ability to set retailer in scan options
- okhttp 4.9.2

## 1.2.3

- androidx worker manager 2.6.0. performance improvements on older android devices.
- okio version 2.10.0
- stability fixes and improvements

## 1.2.4

- stability fixes and improvements
- coroutines to 1.6.0

## 1.2.5

- stability fixes and improvements
- gms tasks updated to 18.0.1

## 1.2.6

- stability fixes and improvements
- okio version 3.0.0
- kotlin 1.6.10
- androidx appcompat updated to 1.3.1

## 1.2.7

- stability fixes and improvements
- upgraded to okhttp 4.9.3
- Scan Results extended field changed from a Bundle to a map to fix parcel crashes on some Samsung devices.

## 1.2.8

- stability fixes and improvements

## 1.2.9

- stability fixes and improvements
- kotlin 1.6.21

## 1.3.0

- stability fixes and improvements

## 1.3.1

- stability fixes and improvements
- kotlin 1.7.10

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
- Fixed bug with deserialization where null values would incorrectly get deserialized to 0 when expecting a float type
- Updated to Kotlin 1.8.21 and Kotlin Coroutines 1.7.1

## 1.3.8

- Updated to target and compile API Level 33
- Updated the following dependencies:
  - androidx.appcompat:appcompat: 1.3.1 -> 1.6.1
  - androidx.core:core-ktx:1.6.0 -> 1.10.0
  - org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0 -> 1.6.4
  - org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0 -> 1.6.4
  - com.squareup.okhttp3:okhttp:4.9.3 -> 4.11.0
  - com.squareup.okhttp3:logging-interceptor:4.9.0 -> 4.11.0
  - com.google.android.gms:play-services-tasks:18.0.1 -> 18.0.2
  - com.squareup.okio:okio:3.0.0 -> 3.3.0
  - androidx.preference:preference-ktx:1.1.1 -> 1.2.0
- Stability fixes and improvements

## 1.3.9
- Added distribution via Maven. You no longer have to declare our transitive dependencies, but can only declare dependencies on our specific maven libraries. Check out the Readme for more details.
- Stability fixes and improvements

## 1.4.0
- Stability fixes and improvements

## 1.4.1
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
- Stability fixes and improvements

## 1.5.4
- Stability fixes and improvements

## 1.5.5
- Add price post fix to the scan results product
- Updated to target API Level 34
- Stability fixes and improvements

## 1.5.6
- Stability fixes and improvements

## 1.5.7
- Stability fixes and improvements
