# Release Notes

## 1.0.0

- Initial release

## 1.0.1

- stability fixes and improvements
- okio version 2.10.0
- androidx worker manager 2.5.0. performance improvements on older android devices.
- kotlin 1.4.30
- gms tasks updated to 17.2.1
- upgrade material to 1.3.0
- androidx lifecycle 2.3.0
- Work around a crash in Android 10 and 11 that may be triggered when two threads concurrently close an SSL socket. This would have appeared in crash logs as NullPointerException: bio == null
- kotlin 1.4.31
- minor bug fixes

## 1.0.2

- stability fixes and improvements
- harden native context & encryption protocols

## 1.0.3

- stability fixes and improvements
- coroutines 1.4.3, https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.4.3
- update androidx lifecycle to 2.3.1

## 1.0.4

- stability fixes and improvements

## 1.0.5

- stability fixes and improvements
- kotlin 1.5.10

## 1.0.6

- stability fixes and improvements
- coroutines 1.5.0, https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.5.0

## 1.0.7

- stability fixes and improvements

## 1.0.8

- stability fixes and improvements
- kotlin 1.5.21
- update coroutines to 1.5.1
- timber 5.0.0

## 1.0.9

- stability fixes and improvements
- timber 5.0.1
- kotlin 1.5.30
- update coroutines to 1.5.2

## 1.1.0

- stability fixes and improvements
- kotlin 1.5.31
- okhttp 4.9.2

## 1.1.1

- androidx worker manager 2.6.0. performance improvements on older android devices.
- okio version 2.10.0
- stability fixes and improvements

## 1.1.2

- stability fixes and improvements
- coroutines to 1.6.0

## 1.1.3

- stability fixes and improvements

## 1.1.4

- stability fixes and improvements
- androidx appcompat updated to 1.3.1
- androidx fragment updated to 1.3.6
- kotlin 1.6.10
- okio version 3.0.0
- gms tasks updated to 18.0.1

## 1.1.5

- stability fixes and improvements
- upgraded to okhttp 4.9.3

## 1.1.6

- stability fixes and improvements

## 1.1.7

- stability fixes and improvements
- kotlin 1.6.21

## 1.1.8

- stability fixes and improvements

## 1.1.9

- stability fixes and improvements
- kotlin 1.7.10

## 1.2.0

- stability fixes and improvements

## 1.2.1

- stability fixes and improvements

## 1.2.2

- stability fixes and improvements
- Remove support for x86 as 32-bit x86 has dropped to a very small number of active devices.

## 1.2.3

- Stability fixes and improvements

## 1.2.4

- Stability fixes and improvements

## 1.2.5

- Stability fixes and improvements
- Fix crash related to coroutine exception handling in the fetching surveys.

## 1.2.6

- Stability fixes and improvements
- Updated to target and compile API Level 33
- Updated the following dependencies:
    - org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0 -> 1.6.4
    - com.google.android.material:material:1.4.0 -> 1.8.0
    - androidx.constraintlayout:constraintlayout:2.0.4 -> 2.1.4
    - androidx.fragment:fragment-ktx:1.3.6 -> 1.5.7
    - androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1 -> 2.6.1
    - androidx.lifecycle:lifecycle-runtime-ktx:2.3.1 -> 2.6.1

## 1.2.7
- Added distribution via Maven. You no longer have to declare our transitive dependencies, but can only declare dependencies on our specific maven libraries. Check out the Readme for more details.
- Stability fixes and improvements

## 1.2.8
- Stability fixes and improvements

## 1.2.9
- Changed internal dependencies from api to implementation. Please include the following:
  - implementation "androidx.appcompat:appcompat:1.6.1"
  - implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2"
  - implementation "com.google.android.gms:play-services-tasks:18.0.2"
  - implementation "androidx.constraintlayout:constraintlayout:2.1.4"
  - implementation "androidx.fragment:fragment-ktx:1.6.2"
- Stability fixes and improvements

## 1.3.0
- Stability fixes and improvements

## 1.3.1
- Stability fixes and improvements

## 1.3.2
- Stability fixes and improvements

## 1.3.3
- Stability fixes and improvements

## 1.3.4
- Stability fixes and improvements
