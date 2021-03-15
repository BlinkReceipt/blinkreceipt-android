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