# Updating Your Barcode Scanning Integration (v1.9.5+)

> Who is this guide for? This guide is for developers using [BlinkReceipt SDK 1.9.5](https://github.com/BlinkReceipt/blinkreceipt-android/releases/tag/1.9.5) or newer, we already migrated to [Google MLKit Barcode](https://developers.google.com/ml-kit/vision/barcode-scanning). 
> If you are implementing barcode scanning for the first time or upgrading from an older version, please read this carefully.

Due to a migration to Google's MLKit for barcode scanning in SDK v1.9.5, there is a fundamental behavioral change in how [RecognizerView](https://htmlpreview.github.io/?https://raw.githubusercontent.com/BlinkReceipt/blinkreceipt-android/master/docs/blinkreceipt-barcode/com/microblink/barcode/RecognizerView.html) 
handles a scanning session. This guide explains the change and how to adapt your implementation.

## TL;DR: The Quick Summary

The barcode scanner now automatically **pauses** after the very first result it produces, which is often an *empty* result right after the camera starts.

**Your Action**: You must now check for empty results and explicitly call [recognizerView.resumeScanning(true)](https://htmlpreview.github.io/?https://raw.githubusercontent.com/BlinkReceipt/blinkreceipt-android/master/docs/blinkreceipt-barcode/com/microblink/barcode/RecognizerView.html#resumeScanning(boolean))
when you are ready to scan again.

---

## Understanding the Change

To build a robust implementation, it's important to understand what is happening behind the scenes.

- **Cause**: We now use Google's MLKit, which begins analyzing frames from the camera immediately upon initialization.
 
- **Effect**: As soon as [RecognizerView](https://htmlpreview.github.io/?https://raw.githubusercontent.com/BlinkReceipt/blinkreceipt-android/master/docs/blinkreceipt-barcode/com/microblink/barcode/RecognizerView.html) 
starts, it analyzes the camera view. Because the user hasn't yet aimed at a barcode, it quickly produces a result: "no barcodes found."

- **The Problem**: The SDK is designed to pause the scanning session after every result to allow your app to process it. 
This means the initial *"empty"* result immediately PAUSES the scanner. 
By the time the user aims their camera at a barcode, the [RecognizerView](https://htmlpreview.github.io/?https://raw.githubusercontent.com/BlinkReceipt/blinkreceipt-android/master/docs/blinkreceipt-barcode/com/microblink/barcode/RecognizerView.html) 
is in a **PAUSED** state and won't detect anything further.

---

## How to Update Your Implementation

You must update your code to handle this *"pause on result"* behavior. The correct approach depends on your app's use case.

### Scenario A: Scan a Single Barcode

*Use this if you want the user to scan one barcode, get the result, and then stop.*

In this scenario, you should ignore the initial empty result and only act when a valid barcode is found. You do not need to resume scanning automatically.

```kotlin

val recognizerView = findViewById<RecognizerView>(R.id.recognizerView)

recognizerView.metadataCallbacks(
    MetadataCallbacks().apply {
        recognizerCallback { results: RecognizerResults ->
            // This callback provides the actual scan data.
            // First, check if the result is valid and not the initial empty one.
            if (results.barcodes().isNotEmpty()) {
                // SUCCESS: A barcode was found!
                // The scanner is now PAUSED automatically.
                
                // Get your barcode data and Process the result (e.g., close this screen, show data)
                // You DO NOT need to call resumeScanning() here if you're done.
                // showResultAndFinish(barcodeText) 
            }
            // If the result is empty, do nothing and let the scanner continue.
            // The resumeScanning() call in scanResultListener will handle restarting it.
        }
        
        failedDetectionCallback {
            // Optional: Handle cases where a barcode-like object was detected but failed to be parsed.
            // You can provide user feedback here, like "Hold the camera steady."
        }
    }
)

// This listener is a generic callback that fires whenever a result is processed.
// It's the perfect place to manage the scanning lifecycle.
recognizerView.scanResultListener {
    // Each time your app receives a result (even an empty one), the scanning session pauses.
    // To ensure the user can scan, we must resume scanning if no valid barcode has been found yet.
    // We pass `true` to reset the internal state for the next scan.
    recognizerView.resumeScanning(true)
})
```

### Scenario B: Continuous Scanning

*Use this for applications like inventory management, where a user might scan multiple barcodes one after another without closing the scanner.*

The logic is similar to the single-scan scenario, but you will call [recognizerView.resumeScanning(true)](https://htmlpreview.github.io/?https://raw.githubusercontent.com/BlinkReceipt/blinkreceipt-android/master/docs/blinkreceipt-barcode/com/microblink/barcode/RecognizerView.html#resumeScanning(boolean)) 
after you have successfully processed a barcode.

```kotlin
val recognizerView = findViewById<RecognizerView>(R.id.recognizerView)

recognizerView.metadataCallbacks(
    MetadataCallbacks().apply {
        recognizerCallback { results: RecognizerResults ->
            // Check if a valid barcode was found.
            if (results.barcodeResults().isNotEmpty()) {
                // SUCCESS: A barcode was found!
                // The scanner is now PAUSED automatically.
                
                // 1. Add the result to your list or process it.
                // 2. Provide feedback to the user (e.g., a "beep" sound or vibration).
                // 3. The scanner is now PAUSED. The scanResultListener below will resume it.
            }
        }
    }
)

recognizerView.scanResultListener {
    // For continuous scanning, we *always* want to resume after any result.
    // This allows the user to immediately point the camera at the next barcode.
    // Consider adding a small delay here if you want to prevent accidental double-scans.
    recognizerView.resumeScanning(true)
})
```

---

## Best Practices

- **Provide User Feedback**: Don't leave the user guessing. When a barcode is successfully scanned, provide immediate feedback like a sound, vibration, or a brief animation on the screen.
- **Handle Empty States**: In your recognizerCallback, always check `results.barcodes().isNotEmpty()` before trying to access the data to avoid crashes.
- **Manual Rescan Button (Optional)**: For some UIs, it's helpful to add a button that allows the user to manually trigger a new scan. This gives them more control.

```kotlin
val resumeBtn = findViewById<Button>(R.id.resumeBtn)

resumeBtn.setOnClickListener {
    // The 'true' parameter resets the recognizer state, which is recommended.
    recognizerView.resumeScanning(true)
}
```
