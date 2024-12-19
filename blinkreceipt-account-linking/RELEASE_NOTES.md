# Release Notes

## 1.0.0

- initial release

## 1.0.1

- link multiple retailers.
- harden native context & encryption protocols

## 1.0.2

- stability fixes and improvements
- fix sdk initialized when passing license key programmatically.
- coroutines 1.4.3, https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.4.3

## 1.0.3

- stability fixes and improvements
- support for Shipt & Walgreens
- display correct UPC for products

## 1.0.4

- stability fixes and improvements
- kotlin 1.5.10

## 1.0.5

- stability fixes and improvements
- coroutines 1.5.0, https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.5.0
- support for sams club

## 1.0.6

- stability fixes and improvements

## 1.0.7

- stability fixes and improvements
- kotlin 1.5.21
- update coroutines to 1.5.1
- timber 5.0.0

## 1.0.8

- stability fixes and improvements
- timber 5.0.1
- kotlin 1.5.30
- update coroutines to 1.5.2

## 1.0.9

- stability fixes and improvements
- kotlin 1.5.31
- added captcha support for Giant Eagle
- added 2fa support for DoorDash
- fixed returning an empty list of orders for BJ's Wholesale Club
- fixed 2fa issues with Bed Bath & Beyond
- fix user authentication for Chewy, Domino's Pizza, Target, Shipt, Macy's, Nike, Instacart
- fix for no orders returned for Dollar Tree, Giant Eagle, Walmart, Starbucks
- fix order total in ShopRite orders
- overall bug fixes for Meijer
- removed Walmart Grocery
- okhttp 4.9.2

## 1.1.0

- add Walmart in-store order support
- androidx worker manager 2.6.0. performance improvements on older android devices.
- add 2FA support for Target
- added support for drizly, walmart ca, staples ca and seamless
- **BREAKING CHANGE** verification api changed from task to callback
- uuid support for debugging purposes
- stability fixes and improvements

## 1.1.1

- stability fixes and improvements
- coroutines to 1.6.0

## 1.1.2

- stability fixes and improvements

## 1.1.3

- stability fixes and improvements
- kotlin 1.6.10
- moved amazon manager from recognizer sdk to account linking framework
- amazon initialized now throws an exception if initialization fails
- verify returns AccountLinkingException instead of Throwable

## 1.1.4

- stability fixes and improvements
- add support for uber eats
- add support for gap
- add support for sprouts
- add support for ulta
- added support for last orders only
- return shipments for online and products for in store/pickup
- okhttp 4.9.3
- Walmart
  - Authentication improvements
- GAP
  - Improved fetching of orders
- Kroger
  - Authentication improvements
- Amazon
    - proguard fix to prevent crashes while linking accounts
    - Authentication flow improvements
    - Improved analytics
    - Improved 2FA support
    - Fixed bug failing to return prices of some Canadian orders
    - Fixed bug intermittently not completing when no new orders are found

## 1.1.5

- stability fixes and improvements
- proguard optimizations
- added experimental coroutine support for kotlin 1.6

## 1.1.6

- stability fixes and improvements
- kotlin 1.6.21
- clear web cache per retail by default is disabled

## 1.1.7

- stability fixes and improvements

## 1.1.8

- stability fixes and improvements
- kotlin 1.7.10
- fixed out of memory issue when parsing accounts with large order history
- Add Albertsons support
- Add Jewel Osco support
- Add Safeway support
- Add Vons support
- Add Acme Markets support
- Add Harris Teeter support
- Add Fred Meyer support
- Add Food 4 Less support
- Add Ralphs support
  - Kroger
    - Improved order status support
      - Returning orders from related merchants (Harris Teeter, Pick 'n Save) with propeply mapped merchantName and retailerId
  -CVS, Starbucks
      - User authentication improvements
      - Improved logging

## 1.1.9

- stability fixes and improvements

## 1.2.0

- stability fixes and improvements
- fix a bug where orders only one set of orders is returned

## 1.2.1

- stability fixes and improvements
- Remove support for x86 as 32-bit x86 has dropped to a very small number of active devices.
- map internal exception codes to public account linking codes.

## 1.2.2

- Stability fixes and improvements
- Force window size to default when initializing the account linking SDK.
- Added androidx data store (androidx.datastore:datastore-preferences)
- Added support to return the latest orders only

## 1.2.3

- Stability fixes and improvements

## 1.2.4

- Stability fixes and improvements
- Remove duplicate orders from internal datastore
- Fixed bug with deserialization where null values would incorrectly get deserialized to 0 when expecting a float type
- Started using Kotlin context receivers, which will require you to update your Kotlin version to 1.8.21, as well as coroutines to 1.7.1

## 1.2.5

- Stability fixes and improvements
- Updated to target and compile API Level 33
- Updated the following dependencies:
  - androidx.webkit:webkit:1.4.0 -> 1.6.1
  - org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0 -> 1.6.4

## 1.2.6
- Added distribution via Maven. You no longer have to declare our transitive dependencies, but can only declare dependencies on our specific maven libraries. Check out the Readme for more details.
- added support for Postmates
  - when linking an account for Postmates, the username is the users phone number
- Stability fixes and improvements

## 1.2.7
- Stability fixes and improvements
- **BREAKING CHANGE** Removed legacy Amazon Manager.

## 1.2.8
- Changed internal dependencies from api to implementation. Please include the following:
  - implementation "com.google.android.gms:play-services-tasks:18.0.2"
- Stability fixes and improvements

## 1.2.9
- Added merchant support:
  - Costco CA
  - Asda
  - Sainsbury UK
  - Testco
  - Carrefour ES
  - Shein ES
  - PcExpress
  - Temu
  - Shein UK
  - Ali Express
  - Uber Eats UK
  - Shein
- Stability fixes and improvements

## 1.3.0
- Stability fixes and improvements

## 1.3.1
- Stability fixes and improvements

## 1.3.2
- Stability fixes and improvements

## 1.3.3
- Integrate Account Linking 2.11.0. https://github.com/BlinkReceipt/br_account_linking/blob/master/CHANGELOG.md
- Stability fixes and improvements

## 1.3.4
- Deprecated BlinkReceiptLinkingSdk.debug
- Stability fixes and improvements

## 1.3.5
- Stability fixes and improvements

## 1.3.6
- Addressed a compatibility issue that was causing Amazon to block requests made from the Android WebView, ensuring seamless functionality and improved user experience.
- Stability fixes and improvements

## 1.3.7
- Amazon AU, Coles and Woolworths support
- Stability fixes and improvements

## 1.3.8
- Stability fixes and improvements

## 1.3.9
- Stability fixes and improvements

## 1.4.0
- **BREAKING CHANGE** Removed PasswordCredentials and replaced with Credentials
- **BREAKING CHANGE** Removed retrieval of orders by all retailers
- Changed the behavior of the web view to match parent vs wrap content
- Add Merchant Support for Amazon ES, FR, DE
- Added support for Retailer WebView Authentication
- Added general optimizations and stability improvements

## 1.4.1
- Stability fixes and improvements

## 1.4.2
- Updated to target API Level 34
- Stability fixes and improvements

## 1.4.3
- Verify and orders trigger an AccountLinkingException with error code 1052 for unsupported retailers
- **BREAKING CHANGE** Removed the following Retailers:
  - KOHLS
  - STAPLES
  - MACYS
  - HEB
  - HYVEE
  - RITE_AID
  - GIANT_EAGLE
  - MARSHALLS
  - TJ_MAXX
  - BED_BATH_AND_BEYOND
  - STAPLES_CA
- Stability fixes and improvements

## 1.4.4
- Stability fixes and improvements

## 1.4.5
- **BREAKING CHANGE** Removed the following Retailers:
  - DRIZLY
- Stability fixes and improvements

## 1.4.6
- Stability fixes and improvements
- Fixed `priceAfterCoupons` mapper logic to default to `null` if original derived value is INVALID
