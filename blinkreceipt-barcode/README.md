# Blink Receipt Barcode SDK

Blink Receipt Barcode SDK for Android is an SDK that enables you to easily add barcode functionality to your app with the purpose of scanning receipts. With provided camera management you can easily create an app that scans receipts. You can also scan images stored as Android Bitmaps that are loaded either from gallery, network or SD card.

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 23+
- Compile SDK: 36+
- Java 17+

## <a name=intro></a> Project Integration and Initialization
To add the sdk to your android project please follow these steps:

1. Add the following maven repository to your build.gradle or settings.gradle, depending on your implementation:

    ```groovy
    repositories {
      maven { url  "https://maven.microblink.com" }
    }
    ```

2. Add the following to your dependency section in your app `build.gradle`.

```groovy
dependencies {
     implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.3"))

     implementation("com.microblink.blinkreceipt:blinkreceipt-barcode")
}
```

## Improve Barcode Results Accuracy 
Barcode scanning performance can vary across Android devices due to differences in camera hardware. 
Devices with less reliable autofocus, in particular, may produce less accurate results.

To improve camera focus on select devices, you may need to apply:
```java
RecognizerView recognizerView = findViewById(R.id.recognizer);
recognizerView.setOptimizeCameraForNearScan(true);
```
- Enabling this setting helps capture clearer frames, leading to more accurate barcode results.
