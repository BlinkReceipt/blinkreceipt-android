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
     mavenCentral()
    ```

2. Add the following to your dependency section in your app `build.gradle`.

```groovy
dependencies {
     implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.9.5"))

     implementation("com.microblink.blinkreceipt:blinkreceipt-barcode")
}
```

## Updating Your Barcode Scanning Integration (v1.9.5+)
- Refer to our [Public Documentation -> Updating Your Barcode Scanning Integration (v1.9.5+)](https://android-beta-documentation.blinkreceipt.com/receipts/barcode/barcode_scanning_integration_guide/)
