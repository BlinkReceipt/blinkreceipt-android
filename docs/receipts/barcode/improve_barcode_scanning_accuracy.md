# Improve Barcode Scanning Accuracy

Barcode scanning performance can vary across Android devices due to differences in camera hardware. Devices with less reliable autofocus, in particular, may produce less accurate results.

To address this, we've introduced `setOptimizeCameraForNearScan(boolean)` in our `RecognizerView`. Enabling this setting helps the camera focus better for near-distance scanning, which is typical for barcodes on receipts.

## How to use it
To improve camera focus on select devices, apply the following setting:

```java
RecognizerView recognizerView = findViewById(R.id.recognizer);
recognizerView.setOptimizeCameraForNearScan(true);
```

Enabling this setting helps capture clearer frames, leading to more accurate barcode results.

## Comparison
Here's a visual comparison of a captured frame with and without this optimization on a device with autofocus challenges:

| `setOptimizeCameraForNearScan(false)`                             | `setOptimizeCameraForNearScan(true)`                            |
|-------------------------------------------------------------------|-----------------------------------------------------------------|
| ![](../../mkdocs/images/barcode_frame_blurred.png){ width="200" } | ![](../../mkdocs/images/barcode_frame_optimized.png){ width="200" } |

**Note:** This setting may not be necessary for all devices. We recommend enabling it for devices known to have issues with close-up focus.
