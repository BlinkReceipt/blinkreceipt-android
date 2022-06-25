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