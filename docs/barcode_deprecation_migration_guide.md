# Deprecating Barcode Scanning Feature

This guide explains the deprecation of the **barcode scanning feature** in the BlinkReceipt Android SDK **2.0.0 and newer** and how to migrate if your application currently relies on it.

## Who this applies to 
This change applies only to Android applications that:

- Use the `blinkreceipt-barcode` module
- Launch a barcode scanner as part of the receipt capture or receipt correction flow

If your application does not use barcode scanning, no action is required.

## What’s changing
Starting with **BlinkReceipt SDK 2.0.0 and newer**, the built-in barcode scanning module has been **removed**.

- Receipt scanning and OCR functionality are **not affected**
- Scan API and core receipt extraction remain unchanged
- Only the standalone barcode scanning component is impacted

## Why barcode scanning was removed
As part of our migration to CameraX, we evaluated long-term stability, maintainability, and performance across all camera-dependent features.

Barcode scanning was tightly coupled to our legacy camera framework. Maintaining it alongside CameraX would have required additional complexity without improving receipt scanning outcomes.

## Recommended replacement

We recommend using **Google ML Kit Barcode Scanning**, which provides:

- Broad barcode format support (UPC, EAN, QR, and more)
- Better performance and accuracy
- Ongoing maintenance and updates from Google

This approach gives you full control over your barcode scanning UX while ensuring long-term stability.

## Breaking API Changes
The `:blinkreceipt-barcode` module has been completely removed. The following classes and all other public APIs within the `com.microblink.barcode.*` package are no longer available:

-   `com.microblink.barcode.BlinkReceiptBarcodeSdk`
-   `com.microblink.barcode.RecognizerView`
-   `com.microblink.barcode.RecognizerResults`
-   `com.microblink.barcode.RecognizerClient`
-   And all other public APIs within the `com.microblink.barcode.*` package.

---

## Migration Steps

### 1. Add Dependencies (i.e. using Google ML Kit)
Add the BlinkReceipt SDK and Google ML Kit Barcode Scanning library to your `build.gradle` or `build.gradle.kts` file:

```groovy
// build.gradle
dependencies {
    implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:2.0.0"))
    implementation("com.microblink.blinkreceipt:blinkreceipt-core")
    // ...
    
    implementation 'com.google.mlkit:barcode-scanning:17.2.0'
}
```

### 2. Implement the Barcode Scanner
You will need to create a new UI for the barcode scanner. This typically involves using a `TextureView` or `SurfaceView` to display the camera preview and overlaying a view to indicate the scanning area.

You can find a complete, official sample implementation in the [Google ML Kit Vision Quickstart](https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart) repository.

### 3. UI/UX Considerations
When implementing your new barcode scanner, consider the following:

-   **Scanning Area:** Clearly indicate the area where the user should place the barcode.
-   **User Guidance:** Provide instructions on how to scan a barcode (e.g., "Center the barcode in the frame").
-   **Feedback:** Provide feedback when a barcode is detected (e.g., a vibration or a sound).

### 4. Is ML Kit a 1:1 Replacement?
Google ML Kit provides all the functionality that was available in the `:blinkreceipt-barcode` module, and more. It supports a wider range of barcode formats and offers better performance and accuracy. There are no known feature gaps. 

### 5. Testing Checklist
- ✅ Verify that the new barcode scanning dependency has been added correctly and the project builds without errors.
- ✅ Test the new barcode scanning UI to ensure the camera preview displays correctly.
- ✅ Confirm that the scanner successfully detects and decodes the various barcode formats that your app supports (e.g., UPC, QR Code).
- ✅ Check that user guidance and feedback (e.g., scanning area indicator, vibration on detection) work as expected.
- ✅ Test the entire user flow, from launching the scanner to processing the scanned data.
- ✅ Verify the scanner's performance in different lighting conditions and with barcodes of varying quality.
- ✅ Ensure the camera lifecycle is handled correctly (e.g., the camera is properly released when the app is backgrounded and re-initialized when foregrounded).
- ✅ Test on a variety of devices with different camera capabilities and Android versions.
- ✅ Confirm that any business logic that previously relied on the `:blinkreceipt-barcode` module now correctly uses the data from the new scanner implementation.

## Get Help
If you encounter any issues during the migration or have questions about these changes, please do not hesitate to reach out to our support team.