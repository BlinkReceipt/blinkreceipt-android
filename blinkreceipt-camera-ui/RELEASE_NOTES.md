# Release Notes

## 1.0.0

- Initial release

## 1.0.1

- stability fixes and improvements
- Chip UI optimizations

## 1.0.2

- stability fixes and improvements
-
## 1.0.3

- stability fixes and improvements

## 1.0.4

- stability fixes and improvements

## 1.0.5

- stability fixes and improvements

## 1.0.6

- stability fixes and improvements

## 1.0.7

- stability fixes and improvements

## 1.0.8

- stability fixes and improvements
- Remove support for x86 as 32-bit x86 has dropped to a very small number of active devices.

## 1.0.9

- Stability fixes and improvements

## 1.1.0

- Stability fixes and improvements

## 1.1.1

- Stability fixes and improvements

## 1.1.2

- Stability fixes and improvements
- Updated to target and compile API Level 33
- Updated the following dependencies:
  - com.google.android.material:material:1.4.0 -> 1.8.0
  - androidx.fragment:fragment:1.3.6 -> 1.5.7
  - androidx.fragment:fragment-ktx:1.3.6 -> 1.5.7

## 1.1.3
- Stability fixes and improvements
- Added distribution via Maven. You no longer have to declare our transitive dependencies, but can only declare dependencies on our specific maven libraries. Check out the Readme for more details.

## 1.1.4
- Stability fixes and improvements

## 1.1.5
- Stability fixes and improvements

## 1.1.6
- Stability fixes and improvements

## 1.1.7
- Stability fixes and improvements

## 1.1.8
- Stability fixes and improvements

## 1.1.9
- Stability fixes and improvements

## 1.2.0
- Stability fixes and improvements

## 1.2.1
- Camera support for coroutines 1.8.0
- Stability fixes and improvements

## 1.2.2
- Stability fixes and improvements

## 1.2.3
- Stability fixes and improvements

## 1.2.4
- Removed legacy static camera implementation ***Breaking Change***
- Stability fixes and improvements

## 1.2.5
- Stability fixes and improvements

## 1.2.6
- Out of the Box experience: Added support for localization to allow language customization for data “chips” that indicate to the user whether the date, total, or merchant has been successfully detected in the receipt.
- Resolved issues with camera UI
  - Where shutter effect triggered twice
  - Removed artificial delay on missing data tooltip display
  - Confirm button visibility fixes while CapturedFrame receipt data is in-progress
  - Added Scan Results feature to include to output media items count and frames scanned
  - Also fixed issue where frameScanned is NOT incremented on finishScan()
- Resolved an issue with whereCameraRecognizerContract  media items did not correspond with the scan results count
- Stability fixes and improvements
