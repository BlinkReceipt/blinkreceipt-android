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

## 1.2.7
- Stability fixes and improvements

## 1.2.8
- Stability fixes and improvements

## 1.2.9
- Updated to target API Level 34
- Stability fixes and improvements

## 1.3.0
- Resolved the issue of the UI freezing when all scan characteristics are disabled
- Fix Receipt Data collection behavior to avoid scan characteristic chips from appearing while in the Scanning state
- Stability fixes and improvements

## 1.3.1
- Stability fixes and improvements

## 1.3.2
- Addressed an issue where the out of the box UI incorrectly displayed the "Total" field as found, even when the returned value was 0.0
- Stability fixes and improvements

## 1.3.3
- Stability fixes and improvements

## 1.3.4
- Stability fixes and improvements

## 1.3.5
- Stability fixes and improvements

## 1.3.6
- Stability fixes and improvements

## 1.3.7
- Stability fixes and improvements

## 1.3.8
- Stability fixes and improvements

## 1.3.9
- Stability fixes and improvements

## 1.4.0
- Support for 16KB memory page size on Android 15+ required for all app updates starting Nov 1, 2025—ensure native code and SDKs are rebuilt with updated NDK and tools.
- Stability fixes and improvements

## 1.4.1
- Stability fixes and improvements

## 1.4.2
- ***Breaking Change*** The scanRegion option has been removed to prevent the bottom of the receipt from being cut off during scanning.
- Stability fixes and improvements

## 1.4.3
- Upgrade Java compilation target from version 8 to 17 across all modules
  - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Stability fixes and improvements

## 1.4.4
- **BREAKING CHANGE** minSDK set to 23 | targetSDK set to 36
- Upgrade Java compilation target from version 8 to 17 across all modules
  - Remove manual toString() implementations to leverage Java 17's string concatenation optimizations
- Stability fixes and improvements

## 1.4.5
- This version introduces a significant overhaul of the theming and styling system for the Camera UI. The primary goal is to make customization simpler and more robust by moving away from deeply nested styles and toward a more streamlined set of attributes on the main theme.
  - ***Breaking Changes***: Simplified Theming API
    - To streamline customization, some nested widget style attributes are now ignored in favor of new, more specific attributes. You will need to update your custom styles to use the new attributes.
      1. Top-Level Theme (BlinkRecognizerStyle)
         - Deprecated & Ignored: ~~`tooltipTextColor`~~, ~~`android:enforceStatusBarContrast`~~, ~~`android:enforceNavigationBarContrast`~~, ~~`android:navigationBarColor`~~, ~~`android:statusBarColor`~~. 
           - These attributes were often redundant or better handled by the application's base theme.
      2. Tooltip Style (blinkTooltipStyle)
         - Deprecated & Ignored: ~~`tooltipDirection`~~ and ~~`tooltipPlacement`~~.
           - These are now controlled internally for a more consistent UI.
      3. General Button Styles (`torchButtonStyle`, `cancelButtonStyle`, `finishButtonStyle`, `retakeButtonStyle`, `captureButtonStyle`, & `confirmButtonStyle`)
        - ~~`android:background`~~ is deprecated across all buttons.
          - **REPLACEMENT**: Use the new, more specific color attributes to control button states: `android:colorAccent` (default state), `android:colorPressedHighlight` (pressed state), and `android:colorActivatedHighlight` (activated/selected state).
        - ~~`android:src`~~ is deprecated for `torchButtonStyle`.
          - **REPLACEMENT**: Use the new `torchOnSrc` and `torchOffSrc` attributes for more explicit control over the flashlight icons.
        - ~~`android:adjustViewBounds`~~ and ~~`android:scaleType`~~ are deprecated for `cancelButtonStyle` as they are now managed internally.
  - ***New Additions & Improvements***
    - New attributes have been added to provide more granular control over UI elements:
      1. For All Buttons (`torchButtonStyle`, `cancelButtonStyle`, `finishButtonStyle`, `retakeButtonStyle`, `captureButtonStyle`, & `confirmButtonStyle`):
         - `android:padding`: Provides direct control over the padding within each button.
         - `android:contentDescription`: Allows setting a custom content description for accessibility. 
         - For the torch button, use `torchOnContentDescription` and `torchOffContentDescription`.
      2. For the Torch Button (`torchButtonStyle`):
           - `torchOnSrc` / `torchOffSrc`: Set distinct drawables for the torch's on and off states.
           - `torchOnContentDescription` / `torchOffContentDescription`: Set distinct accessibility strings for each state.
- Stability fixes and improvements

## 1.4.6
- Stability fixes and improvements

## 1.4.7
- **BREAKING CHANGE** Fixed Parcelable implementation issues in `CameraRecognizerResults`. The `Exception` state now exposes a specific `CameraException` instead of a generic `Throwable` to ensure safe serialization used by the Camera Contract.
- Stability fixes and improvements

## 1.4.8
- Stability fixes and improvements

## 1.4.9
- Stability fixes and improvements

## 2.0.0
- Migrated to Android CameraX to improve camera stability and reliability during receipt capture
- More consistent camera behavior across a wide range of Android devices and OS versions
- Provides a more robust foundation for ongoing performance and camera enhancements
- Stability fixes and improvements

## 2.0.1
- Stability fixes and improvements

## 2.0.2
- Stability fixes and improvements

## 2.0.3
- Stability fixes and improvements

## 2.1.0
- We’ve increased the minimum supported Android SDK version from API 23 (Android 6.0 Marshmallow) to API 24 (Android 7.0 Nougat). This allows us to take advantage of newer platform capabilities and improve overall app quality.
- Stability fixes and improvements
