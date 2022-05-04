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

## 1.1.5

- Auto configuration can now be disabled by adding a meta data id to the manifest.
- Duplicate blink receipt id for duplicate receipts.
- Merchant guess
- Payment method returns Payment Method, Card Type, Card Issuer

## 1.1.6

- The OkHttp 3.12.x branch supports Android 2.3+ (API level 9+) and Java 7+. These platforms lack support for TLS 1.2 and should not be used. But because upgrading is difficult we will backport critical fixes to the 3.12.x branch through December 31, 2020.

## 1.1.7

- minSDK 21
- Target Java 8
- Migrated to the latest receipt model
- Improved purchase validation
- Edge detection rect is now available in the public interface

## 1.1.8

- Purchase validation improvements
- Bug Fixes

## 1.1.9

- Amazon account manager bug fix
- Improved duplicate search engine
- Bug Fixes

## 1.2.0

- Promotion Bug Fixes

## 1.2.1

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

## 1.2.2

- Bug Fixes

## 1.2.3

- Edge detection fixes
- Incorporate full price into product intelligence
- Incorporate fuzzy search into product intelligence
- Merchant detection fixes
- Amazon improvements
- Support for purchase type
- Parser improvements

## 1.2.4

- Fix crash in edge detection

## 1.2.5

- AndroidX support is now a requirement
- Edge detection performance improvements
- Camera management performance and stability improvements
- Amazon streaming parsing and prime support
- Security stability improvements
- Camera capture listener change interface exception to throwable

## 1.2.6

- Amazon bug fixes
- Fix for certain 2020 dates

## 1.2.7

- Breaking interface changes for CameraDataListener.
    - Removed onDebugResults callback
    - Renamed onCameraFrameError to onCameraFrameException
    - Renamed onEdgeDetectionResult to onEdgeDetectionResults
- Camera management thread improvements
- Added blurry & receipt property to TakePictureResult
- Add channel to scan results
- Samsung camera permission issue & aspect ratio fix.
- Gmail
    - onSignInCancelled added for when a user cancels sign in requests

## 1.2.8

- Merchant improvements & bug fixes

## 1.2.9

- Remove support for x86_64 (  abiFilters 'x86', 'armeabi-v7a', 'arm64-v8a' )
- Added Timber Logging Support ( "com.jakewharton.timber:timber" )
- Modularized Recognizer SDK, which now depends on BlinkReceiptCore & BlinkReceiptCamera
- Renamed ReceiptSdk to BlinkReceiptSdk
- Class Repackaging
    - Product
        - com.microblink.Product
        - com.microblink.core.Product
    - ScanResults
        - com.microblink.ScanResults
        - com.microblink.core.ScanResults
    - Retailer
        - com.microblink.Retailer
        - com.microblink.core.Retailer
- Dependencies
    - Build Tools: 29.0.3
    - Okhttp: 4.4.1
    - Retrofit : 2.8.1
    - Okio : 2.4.3
    - Tasks : 17.0.1
    - Timber: 4.7.1

## 1.3.0

Blink Receipt Recognizer
    - fix silent crash on Gmail for invalid parsing of merchant dates when loading inbox on api < 24
    - Update Play Service Auth framework to 18.0.0
    - Removed internal location look up services.

- Dependencies
    - okhttp 4.5.0
    - Play Service Task 17.0.2
    - Play Service Auth 18.0.0
    - Blink Receipt Camera 1.0.1
    - Blink Receipt Core 1.0.1

## 1.3.1

- duplicate search bug fix
- ocr confidence bug fix
- support for subproducts
- improved blur & receipt detection
- process camera frames when in the resume state only.
- improved native memory model
- e-receipt
    - introduced the following new properties:
        - pos system
        - sub merchant
- Dependencies
      - blink receipt Core 1.0.2
      - blink receipt Camera 1.0.2
      - okhttp 4.7.0
      - okio : 2.6.0
      - retrofit: 2.8.2

## 1.3.2

- report proper edge results for bottom and right properties.
- EdgeDetectionResult edges Rect to RectF types. ***Breaking Change***
- EdgeDetectionResult returns edges as percentages as the underlying frame dimensions can change depending on the device.
- Play Service Task 17.1.0
- Bugfix to determine correct top and bottom edges.
- e-receipts security enhancements, which now requires clients to have a product intelligence key
- ***Breaking Change*** Recognizer getInstance() requires a context.

## 1.3.3

- generate x86_64 slice
- Bug fixes

## 1.3.4

- sensitive product bug fix

## 1.3.5

- okio 2.7.0
- okhttp 4.8.1
- retrofit: 2.9.0
- build tools 30.0.1
- attach screen detection properties on take picture results
- Amazon fixed memory leak in manager. AmazonManager.getInstance().destroy() in onDestroy() of activity to release callbacks.
- Gmail fixed memory leak in manager. GmailManager.getInstance().destroy() in onDestroy() of activity to release callbacks.
- ocrConfidence NAN bug fix.
- Migrated the GmailInboxManager functionality to the blinkreceipt-digital and converted it GmailClient
- Modified internal recognition processor lifecycle to be tightly coupled to RecognizerView lifecycle.

## 1.3.6

- Bug Fix for panera bread
- Pass more data from session to duplicate detection endpoint
- Return channel from all merchant sources that support it
- Send fraud detection data to duplicate detection endpoint
- Pass frame result data to frame upload endpoint
- Implement new merchant detection mechanism via longtail lookup

## 1.3.7

- ***Breaking Change*** Removed access to the Recognizer class all recognition should go through _RecognizerView_
- Security: remove credentials interface from amazon manager.
- Product extended fields from bundle to map.
- okhttp 4.9.0
- okio 2.8.0
- Update Play Service Auth framework to 18.1.0
- Play Service Task 17.2.0
- Google Api Services 1.30.10
- Google Api Client 1.36.0
- Renamed ReceiptSdkInitProvider to BlinkRecognizerProvider. if you are overriding this entry in the manifest please be aware of this name change.
- augmented internal bitmap scale detection to improve OCR results.
- don't return null for totals that are 0.00

## 1.3.8

- constraints 2.0.1
- http retry bug fixes while scanning receipts
- okio 2.9.0
- initialize performance improvements around camera management
- enable edges from start of scan session. Edge configuration access through builder interface
- merchant configuration access through builder interface

## 1.3.9

- amazon country code support
- improved receipt screen detection
- downgrade target to api 29, but compiled against api 30
- amazon secure internal stored files. Requires "androidx.security:security-crypto:1.1.0-alpha02"
- enhanced security for users stored credentials using jetpack security instead of standard encryption algorithms.

## 1.4.0

- fix amazon manager threading model to prevent ANR
- scan results total, subTotal, tax returned as null if not found on receipt

## 1.4.1

- amazon secure internal stored files. Requires "androidx.security:security-crypto:1.1.0-alpha03"
- security: updated Tink to stable release 1.5.0
- add ability to specify a cutoff Date instead of cut off number of days
- performance improvements across all non-Apple platforms (primarily newer Android phones)
- internal serialization performance
- AmazonManager ***Breaking Change***
    - must call initialize before accessing orders, verify, credentials
    - clearOrders() now returns Task<Boolean>
    - clearCredentials() now returns Task<Boolean>
    - storeCredentials now returns Task<Boolean>
- fix incorrect handling for bad password in Amazon
- web kit 1.4.0
- parsing bug fixes

## 1.4.2

- stability fixes and improvements
- recognizerCallback CameraRecognizerCallback now nullable
- okio version 2.10.0
- Make RecognizerView lifecycle aware
- gms tasks updated to 17.2.1
- Work around a crash in Android 10 and 11 that may be triggered when two threads concurrently close an SSL socket. This would have appeared in crash logs as NullPointerException: bio == null
- Add Inverted Text scan ability for Basket scanning
- Make Recognizer View lifecycle aware
- Modified Product Intelligence call to dedupe multiple products of the same type
- Added Logo merchant detection threshold for Merchant determination
- With the introduction of NDK r22, receipt recognition is disabled in the emulator environment
- kotlin 1.4.31
- fixed crash in amazon related to https://issuetracker.google.com/issues/167977579, https://issuetracker.google.com/issues/175132222

## 1.4.3

- harden native context & encryption protocols

## 1.4.4

- stability fixes and improvements
- add api for direct recognition of images
- added kotlin, coroutines, androidx core and worker manager (required) dependencies for more efficent internal processing
- direct api support available on supported licenses
- coroutines 1.4.3, https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.4.3

## 1.4.5

- stability fixes and improvements

## 1.4.6

- stability fixes and improvements
- kotlin 1.5.10

## 1.4.7

- stability fixes and improvements
- coroutines 1.5.0, https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.5.0

## 1.4.8

- stability fixes for possible products & qualified promotions
- fix KOHL'S payment method
- fixed rogue quantity issue

## 1.4.9

- stability fixes and improvements
- kotlin 1.5.21
- fix gradle transform resource api conflicts
- fix amazon parsing issue for UK customers
- fix amazon parsing issue verified accounts
- updated google play services authentication to 19.2.0
- update coroutines to 1.5.1
- amazon duplicate sign in detection bug fix.
- timber 5.0.0

## 1.5.0

- fix preliminary results crash
- stability fixes and improvements
- timber 5.0.1
- kotlin 1.5.30
- update coroutines to 1.5.2
- remove support for amazon prime now as its shutting down

## 1.5.1

- stability fixes and improvements
- kotlin 1.5.31
- removed retailer from frame results as its always set to UNKNOWN
- okhttp 4.9.2

## 1.5.2

- androidx worker manager 2.6.0. performance improvements on older android devices.
- fix parsing dates for Makro receipts.
- okio version 2.10.0
- amazon support for CA
- stability fixes and improvements

## 1.5.3

- stability fixes and improvements
- coroutines to 1.6.0
- amazon fix 2fa sign in issues

## 1.5.4

- stability fixes and improvements
- Support for data extraction for additional Canadian merchants

## 1.5.5

- stability fixes and improvements
- androidx appcompat updated to 1.3.1
- kotlin 1.6.10
- okio version 3.0.0
- gms tasks updated to 18.0.1
- ***BREAKING CHANGES*** move Amazon to the account linking sdk.

## 1.5.6

- stability fixes and improvements
- okhttp 4.9.3
- upgraded to okhttp 4.9.3
- refactored merchant fetching
- reimplemented timer to fix dead lock bug

## 1.5.7

- stability fixes and improvements

