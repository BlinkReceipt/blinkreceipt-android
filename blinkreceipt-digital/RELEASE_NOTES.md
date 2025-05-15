[# Release Notes

## 1.0.0

- Outlook Support
  - Requires Blink Receipt Recognizer to be initialized

## 1.0.1

- fix proguard rules
- e-receipts security enhancements, which now requires clients to have a product intelligence key
- microsoft 1.5.2 SDK

## 1.0.2

- generate x86_64 slice
- Bug fixes

## 1.0.3

- sensitive product bug fix

## 1.0.4

- okio 2.7.0
- okhttp 4.8.0
- build tools 30.0.1
- microsoft 1.6.0 SDK
- yahoo and aol IMAP integration
- IMAP requires style parent Theme.MaterialComponents.* for bottom sheet support

## 1.0.5
- Bug Fix for panera bread

## 1.0.6

- outlook client rename me to credentials
- Security: remove credentials interface from outlook and IMAP clients.
- Gmail SDK Integration
- Gmail IMAP support
- Expose receipt email id in scan results
- okhttp 4.9.0
- okio 2.8.0

## 1.0.7

- constraints 2.0.1
- stability fixes
- okio 2.9.0
- webkit 1.3.0
- material 1.2.1

## 1.0.8

- clients implement Closeable to make it friendly with try with resources
- license key is required to initialize the SDK
- return eReceiptOrderStatus in scan results
- downgrade target to api 29, but compiled against api 30
- Android 11 imap messages failed due to missing mail classes on Android 11
- provide your own list of supported merchants
- Add new option for email scanning, `useAggregation` to compress results from the same order into one result

## 1.0.9

- GmailClient provide proper error message when prod intel key isn't provided or expires.
- Debug messages interface to test sender and html parsing.
- Move Password Credentials to core package to share across dfferent SDKs

## 1.1.0

- security: updated Tink to stable release 1.5.0
- Add functionality to all Email clients to specify date window for email searches in terms of Date
- Add functionality for all email clients to contain a search senders list that matches email aliases with their retail
  counterpart
- internal serialization performance
- web kit 1.4.0

## 1.1.1

- stability fixes and improvements
- captcha handling for yahoo & aol
- fix crash when searching users inbox (mail service)
- gms tasks updated to 17.2.1
- upgrade material to 1.3.0
- androidx lifecycle 2.3.0
- Work around a crash in Android 10 and 11 that may be triggered when two threads concurrently close an SSL socket. This would have appeared in crash logs as NullPointerException: bio == null
- Changed Date and Time formatting for ScanResults to match iOS SDK
- kotlin 1.4.31
- gmail client search [gmail]/all mail folder
- - fixed crash related to https://issuetracker.google.com/issues/167977579, https://issuetracker.google.com/issues/175132222

## 1.1.2

- stability fixes and improvements
- harden native context & encryption protocols

## 1.1.3

- stability fixes and improvements
- fix digital receipt date parsing bugs
- fix Yahoo/AOL IMAP setup
- update androidx lifecycle to 2.3.1

## 1.1.4

- stability fixes and improvements
- fix generating passwords for aol accounts

## 1.1.5

- stability fixes and improvements
- add androidx core and kotlin stdlib
- update java mail and activation sdk to 1.6.7
- update google api services gmail to v1-rev20210510-1.31.0
- update google api client android to 1.31.5
- update google http client gson to 1.39.2-sp.1
- provide merchant email, shipping cost and email subject via scan results
- add coroutines core, coroutines android, coroutines play services
- provide currency code via scan results
- add worker manager libraries

## 1.1.6

- stability fixes and improvements
- support for additional provider date formats

## 1.1.7

- stability fixes and improvements

## 1.1.8

- stability fixes and improvements
- fix gradle transform resource api conflicts
- updated google play services authentication to 19.2.0
- update coroutines to 1.5.1
- timber 5.0.0

## 1.1.9

- stability fixes and improvements
- timber 5.0.1
- override post job url
- override post job date
- update coroutines to 1.5.2

## 1.2.0

- stability fixes and improvements
- handle bad email & password for german locales
- okhttp 4.9.2

## 1.2.1

- update google api services gmail to v1-rev20210614-1.32.1
- update google api client android to 1.32.1
- update google http client gson to 1.40.0
- return client merchant name
- **BREAKING CHANGES** multiple email support
- stability fixes and improvements

## 1.2.2

- stability fixes and improvements
- coroutines to 1.6.0
- fix filtering merchant logic in GmailClient
- clear web view cache by default
- add remote messages interface for eml

## 1.2.3

- stability fixes and improvements

## 1.2.4

- stability fixes and improvements
- androidx appcompat updated to 1.3.1
- gms tasks updated to 18.0.1

## 1.2.5

- stability fixes and improvements
- upgraded to okhttp 4.9.3
- link IMAP account with app password
- fix unable to store account when using multiple clients within the same session.

## 1.2.6

- stability fixes and improvements
- enriched mailbox data returned when scrape fails to return structured data.
- added override date time for date and long objects

## 1.2.7

- stability fixes and improvements

## 1.2.8

- stability fixes and improvements
- gmail v3 login support
- remove dialog when bottom sheet closes

## 1.2.9

- stability fixes and improvements

## 1.3.0

- stability fixes and improvements
- update aol create password web context

## 1.3.1

- stability fixes and improvements

## 1.3.2

- stability fixes and improvements
- Remove support for x86 as 32-bit x86 has dropped to a very small number of active devices.
- fix gmail bad password detection

## 1.3.3

- stability fixes and improvements
- increase network timeout for Outlook integration

## 1.3.4

- Stability fixes and improvements

## 1.3.5

- Stability fixes and improvements
- Outlook Client returns the current account signed into the app or in the case of shared device mode. Signed into the device
- update Microsoft SDK to 4.2.0
- Fix crash related to coroutine exception handling in the remote messages api.
- Fixed bug with deserialization where null float values would incorrectly get deserialized to 0 when expecting a float type

## 1.3.6

- Stability fixes and improvements
- Updated to target and compile API Level 33
- Updated the following dependencies:
  - com.google.android.gms:play-services-auth:19.2.0 -> 20.5.0
  - androidx.constraintlayout:constraintlayout:2.0.4 -> 2.1.4
  - androidx.fragment:fragment-ktx:1.3.1 -> 1.5.7
  - androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1 -> 2.6.1
  - androidx.lifecycle:lifecycle-livedata-ktx:2.3.1 -> 2.6.1
  - androidx.lifecycle:lifecycle-common-java8:2.3.1 -> 2.6.1
  - androidx.lifecycle:lifecycle-runtime-ktx:2.3.1 -> 2.6.1
  - androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1 -> 2.6.1
  - androidx.webkit:webkit:1.4.0 -> 1.6.1
  - org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0 -> 1.6.4

## 1.3.7
- Lowercase the email address in `OutlookClient` when executing `remoteMessages` to ensure identical emails with different casing are not treated as new email accounts.
- Added distribution via Maven. You no longer have to declare our transitive dependencies, but can only declare dependencies on our specific maven libraries. Check out the Readme for more details.
- Stability fixes and improvements

## 1.3.8
- Stability fixes and improvements

## 1.3.9
- Changed internal dependencies from api to implementation. Please include the following:
  - implementation "com.google.android.gms:play-services-tasks:18.0.2"
  -  implementation( "com.microsoft.identity.client:msal:5.5.0" ) {
     exclude group: 'com.microsoft.device.display'
     }
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
- Support new Gmail IMAP 2FA flow
- Stability fixes and improvements

## 1.5.0
- Stability fixes and improvements

## 1.5.1
- **BREAKING CHANGE** Remove debug property from public api surfaces
- Stability fixes and improvements

## 1.5.2
- Stability fixes and improvements

## 1.5.3
- Updated to target API Level 34
- Optimization to cookies management
- Stability fixes and improvements

## 1.5.4
- Stability fixes and improvements

## 1.5.5
- **BREAKING CHANGE** Replaced `PasswordCredentials` with `Credentials` in public API surface
- **BREAKING CHANGE** Initiate provider setup fragment with `ProviderSetupFragmentFactory`
- **BREAKING CHANGE** Provider setup callback now returns `ProviderResults` with credentials
- Introduced foundational support for manual Gmail authentication workflow
- Stability fixes and improvements

## 1.5.6
- Fixed an issue in `ImapClient` where calling `verify`, `messages`, `remoteMessages`, and `logout` with credentials that have matching username but mismatching password from the linked credentials causes an `AuthenticationException`.
- Stability fixes and improvements

## 1.5.7
- Stability fixes and improvements
- Fixed `priceAfterCoupons` mapper logic to default to `null` if original derived value is INVALID

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
- **BREAKING CHANGE** Relocated SDK distribution from https://maven.microblink.com to Maven Central.
- Support for 16KB memory page size on Android 15+ required for all app updates starting Nov 1, 2025—ensure native code and SDKs are rebuilt with updated NDK and tools.
- Fixed internal logic to include missing key fields when uploading Scan Results summary
- Stability fixes and improvements

## 1.6.5
- Stability fixes and improvements