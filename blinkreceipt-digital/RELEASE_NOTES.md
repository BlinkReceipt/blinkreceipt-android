# Release Notes

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
