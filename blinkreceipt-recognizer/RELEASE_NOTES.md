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
- Indicates whether corresponding email is a valid e-receipt or a promotional/marketing message
- fixed potential deadlock in scan session
- optimized merchant delivery in sdk startup

## 1.5.8

- stability fixes and improvements
- kotlin 1.6.21
- paymentTerminalId and paymentTransactionId returned in scan results.

## 1.5.9

- stability fixes and improvements

## 1.6.0

- stability fixes and improvements
- kotlin 1.7.10

## 1.6.1

- expose combined raw text
- expose purchase type
- fix an issue with attaching promotions

## 1.6.2

- stability fixes and improvements

## 1.6.3

- stability fixes and improvements
- Remove support for x86 as 32-bit x86 has dropped to a very small number of active devices.

## 1.6.4

- Fixed a crash related to r8 removing the Emulators class from the classpath
- Deprecated metadata com.microblink.AutoConfiguration. If you manually initialize the SDK, please remove the auto-initialized provider.
    ```xml
      <provider
          android:name="com.microblink.BlinkRecognizerProvider"
          android:authorities="${applicationId}.BlinkRecognizerProvider"
          tools:node="remove" />
    ```
- Fixed regressions with French receipts
  - "1X" left in the RSD in two products with bogus "8" qty because of detached 'B'
  - We fixed an issue with tagging totals related to coupons.
- We fixed an issue with certain receipts missing the last four digits of the credit card.
- We fixed an issue where Pet Supply Plus returned the incorrect date.

## 1.6.5

- Stability fixes and improvements
- Added support for new fuel product properties
- Added support for additional Canadian date formats
- Overall improvements in receipt total detection for top merchants in the UK
- Fixed an issue with some Walgreens product quantities may have been returned incorrectly
- Fixed and issue where purchase type for Panera and Dollar General receipts was incorrectly returned as “Delivery”

## 1.6.6

- Stability fixes and improvements
- Improve accuracy of FullPrice property
- Improve detection of unusual price formats
- Support special date format for some Canada merchants
- Improve fake receipts detection
- Improve total detection on UK receipts
- Fix Walgreens quantities detection

## 1.6.7

- Updated to target and compile API Level 33
- Updated the following dependencies:
  - com.google.android.gms:play-services-auth:19.2.0 -> 20.5.0
  - androidx.webkit:webkit:1.4.0 -> 1.6.1
- Tag long transaction ID for Walmart
- Stability fixes and improvements

## 1.6.8
- Added distribution via Maven. You no longer have to declare our transitive dependencies, but can only declare dependencies on our specific maven libraries. Check out the Readme for more details.
- Stability fixes and improvements

## 1.6.9
- fixed total UI marker to allow users to submit images only when we capture a total
- Stability fixes and improvements

## 1.7.0
- added raw basket property
- added raw trip header property
- added raw trip footer property
- Scan PDFs
- Rename RecognizerClient to ImageClient
- Change internal dependencies from api to implementation
- Stability fixes and improvements

## 1.7.1
- add cashback property detected on the receipt, if any
- Fuel product detection improvements in US, UK
- Discount detection improvements in FR, UK
- Sales Tax detection improvements for US
- Product QTY improvements for US
- Stability fixes and improvements

## 1.7.2
- Merchant detection improvements in DE, ZA
- Total extraction improvements for M&S in the UK
- Improved support for extracting longTransactionId for Walmart, Costco, Target, Walgreens and Sam’s Club US receipts
- Gas product extraction improvements in the US
- Overall extraction improvements

## 1.7.3
- Fix issue with duplication detection results by saving long transaction id
- Improvements to loyaltyProgram detection
- Product QTY improvements for Albertsons banners in the US
- Merchant detection improvements in US, UK and DE
- loyalty member number detected on the receipt, if any
- Stability fixes and improvements

## 1.7.4
- Physical Receipts
  - Product QTY improvements in the US
  - Improvements to tip extraction
- Stability fixes and improvements

## 1.7.5
- Overall extraction improvements in the UK (1.4.0)
- Stability fixes and improvements

## 1.7.6
- Overall extraction improvements in US, including product weights and quantities
- Date extraction improvements in DE & US
- Stability fixes and improvements

## 1.7.7
- Stability fixes and improvements

## 1.7.8
- US Regressions - last 4CC
- ZA - Trip Improvements - Phone- Part 2
- ZA - Trip Improvements - Phone
- Stability fixes and improvements

## 1.7.9
- Improvements to date accuracy in CA
- Improvements to QTY and weighed product extraction for H-E-B, Walgreens and various US merchants
- Improvements to long_transaction_id extraction for Walmart in US
- Introduce support for various ISO associations for the United Kingdom
- Added support for country code UK and GB. Setting either option from within the SDK will return uniform results

## 1.8.0
- Stability fixes and improvements

## 1.8.1
- Improvements to Tax extraction for Ahold Banners in the US
- Improvements to Last 4 CC extraction
- Improvements to basket/product extraction in US and ZA for various merchants
- Improvements to phone number extraction in DE
- Stability fixes and improvements

## 1.8.2
- **BREAKING CHANGE** Remove debug property from public api surfaces
- Improvements to Last 4 CC extraction

## 1.8.3
- Improvements to basket/product extraction for Loblaws Banners in Canada (CA)
- Improvements to date extraction in CA, US 
- Improvements to Last 4 CC extraction in US 
- Improvements to merchant detection in UK

## 1.8.4
- Improvements to basket/product extraction for Loblaws Banners in Canada (CA)
- Merchant detection improvements in DE 
- Improvements to sales tax extraction if sales tax exemption is found on receipt. 
- Improvements to QTY extraction in US for Albertsons banners and Lowe’s receipts
- Updated to target API Level 34

## 1.8.5
- Improvements to basket/product extraction for Loblaws Banners in Canada (CA)
- Improvements to QTY extraction in US for Albertsons banners 
- Improvements to Last 4 CC extraction in US

## 1.8.6
- **BREAKING CHANGE** Removed `events` constructor parameter from `PdfClient`
- Fixed an issue where onDeviceOcr() was not returned with the correct value
- Improvements to QTY and weight extraction in US for various retailers 
- Improvements to date extraction in CA, US 
- Improvements to store state extraction in the US
- Stability fixes and improvements

## 1.8.7
- Improvements to QTY extraction for various US retailers
- Improvements Taxes extraction
- Improvements to Full Price extraction for various US retailers
- Added support for Returns/ Refund receipts

## 1.8.8
- Added support for Returns/ Refund receipts
- Fixed `priceAfterCoupons` mapper logic to default to `null` if original derived value is INVALID
- Improving data extraction for German receipts, focusing on quantities, weights, and full price.
- Improving basket extraction (QTY, weights) for German receipts.
- Enhancing QTY extraction for various US retailers.
- Improving tax extraction accuracy for various US retailers.
- Enhancing full price extraction for US retailers and for German receipts.

## 1.8.9
- Fee and Bag Extraction in the US: Enhanced ability to extract fee and bag-related information from receipts in the United States, providing more accurate data extraction for these specific items
- QTY and Product Description Extraction for ALDI, Sprouts, Jewel-Osco, and More! Better accuracy in extracting quantity (QTY) and product descriptions for popular retailers such as Alberstons retailers, ALDI, Sprouts, and more, resulting in more precise product data extraction.
- QTY and Phone Number Extraction for German Retailers: Significant strides in extracting quantity (QTY) and phone numbers from receipts issued by German retailers
- Date Extraction in the UK: Refined date extraction capabilities for receipts from the UK, ensuring more consistent and accurate extraction of purchase dates.
- Total Extraction in the UK: Refined total extraction process for receipts in the UK, providing more reliable transaction data with higher accuracy in the total amounts.
- Stability fixes and improvements

## 1.9.0
- Remove support for armeabi-v7a
- Remove the ability to disable networking within the SDK
- US Market: Improved extraction of product descriptions, part numbers, and basket details for better accuracy across various retailers.
- Germany Market: Enhanced capture of product information, including quantity and full price details for German receipts.
- Stability fixes and improvements

## 1.9.1
- Fuel Products: Better accuracy in fuel receipt data, including pricing and transaction details.
- Product Descriptions: Improved accuracy for Hannaford, Meijer, Kroger, and other retailers.
- Merchant Detection: Enhanced recognition of merchants in the US and Germany.
- Receipt Time Capture: More precise extraction of receipt timestamps.
- Stability fixes and improvements

## 1.9.2
- Improvements- UK Merchants: Enhanced product description, quantity (QTY), and price extraction for improved accuracy.
  - Phone Extraction: Improved phone number extraction capabilities for receipts from both Germany and the UK.
  - US Receipts: Enhanced accuracy in capturing transaction IDs and taxes for US-based receipts.
- Receipt Image Quality
  - We've improved how we detect receipts taken from a screen, reducing false positives for more accurate results.

## 1.9.3
- UK Merchants: Improved extraction of product descriptions, quantities (QTY), and dates for enhanced accuracy across top UK retailers.
- US Receipts - Fuel Products: Resolved an issue where fuel items were incorrectly flagged, ensuring more reliable product categorization for US-based receipts.

## 1.9.4
- Added support for Priceless receipts from Sephora (DoorDash) (US)
- Improved extraction of the Total property (US)
- Improved support for receipt formats (general parsing improvements) (UK)
- Enhanced detection of Gas purchases when no products are listed
- Added support for Costco receipts, including GST handling and more (NZ)
- Stability fixes and improvements

## 1.9.5
- Support for 16KB memory page size on Android 15+ required for all app updates starting Nov 1, 2025—ensure native code and SDKs are rebuilt with updated NDK and tools.
- Full Price and Discount Improvements - US 
  - Improvements on Full Price extraction (price before any discounts)
  - Enhanced detection, extraction and mapping of discounts to individual products. 
- Improvements to Australian Receipts
  - Enhanced receipt extraction for Australian transactions. 
  - Optimized handling of GST and other tax-related fields.
- Add support for armeabi-v7a
- ***Breaking Change*** Changed ScanResults barcode type from String to a List
- Auto configuration has been replaced with Androidx Startup. For details on how to disable auto initialization, please refer to the README.
- Integrated ML Kit's Barcode Scanning API to detect and extract barcode information from receipt images. This enables users to quickly capture and process receipts.
- Stability fixes and improvements

## 1.9.6
- Resolved a intermittent crash affecting certain receipt image edge cases.
- Stability fixes and improvements

## 1.9.7
- Accuracy improvement to priceAfterCoupon, we now return null if not applicable, rather than 0
- ***Breaking Change*** The scanRegion option has been removed to prevent the bottom of the receipt from being cut off during scanning.
- More accurate extraction of product quantities, even when listed in less common formats
- Improved detection of discounts and promotions
- Enhanced capture of the last 4 digits of credit card numbers across a wider range of receipt layouts

## 1.9.8
- Unsupported PDF files now return a failure code instead of an empty response, enabling applications to inform the user.
- Merchant Detection Improvements 
  - Enhanced merchant detection models for the US, The Netherlands, and Germany, improving accuracy and consistency across regions. 
- Stability Improvements 
  - Addressed an issue that could cause unexpected crashes in certain scenarios, improving overall reliability 
- Basket Data Enhancements – Germany 
  - Improved extraction accuracy for basket-level details, including product quantities, promotions, and pricing.

## 1.9.9
- ***Breaking Change*** minSDK set to 23 | targetSDK set to 36
- Upgrade Java compilation target from version 8 to 17 across all modules
    - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- French Canadian Date Support 
  - Enhanced date recognition to support French Canadian formats in Canada. 
- Promotion Detection in German Receipts 
  - Improved accuracy when identifying promotions on German receipts. 
- General Stability 
  - Additional stability improvements across all platforms.

## 1.9.10
- Extraction Enhancements (United States)
  - Improved handling of fuel receipts from gas and convenience retailers, increased accuracy of item and quantity extraction (including Dollar General), and overall improvements to product data consistency. 
- Promotion Detection (Germany)
  - Refinements to promotion matching and validation workflows to improve detection accuracy across receipt types. 
- Sales Tax Extraction (Australia)
  - Improved parsing of sales tax fields for better consistency.
- Optimized `ImageClient.recognize()` implementation by removing redundant License Check call to make it faster and more reliable.
  - This avoids race condition issues where the check would fail intermittently, hence throwing "Not Authorized" Exception.
- Added Mobile Payment as a recognized method of payment for U.S. receipts. 
- Implemented overall extraction and stability improvements for enhanced accuracy and reliability.
- Overall Stability Improvements 
  - General enhancements to improve reliability and performance across extraction workflows

## 1.9.11
- Extraction Enhancements (United States, Germany, France)
- Germany
  - Improved how German addresses are recognized and formatted. 
  - Better detection of taxes and discounts on German and French receipts. 
  - Added support for identifying fuel products.
- United States & Canada
  - United States & Canada
  - Better handling of fuel purchases within the receipt basket.
  - More reliable detection of sales tax.
- Stability Improvements
- Enhanced extraction behavior to support a more stable experience.
- Stability fixes and improvements

## 1.9.12
- Improved scan performance to reduce processing time in certain international use cases
- Improved overall stability during receipt scanning and OCR to provide a smoother experience

## 1.9.13
- time_seconds
  - Returns the purchase time in seconds for reporting and analysis. 
- purchase_country 
  - Indicates the country where the purchase occurred.
