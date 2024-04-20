# Blink Receipt Earnings SDK

Blink Receipt Earnings SDK for Android is an SDK that enables you to easily add earnings functionality to your app with the purpose of scanning receipts. With provided camera management you can easily create an app that scans receipts. You can also scan images stored as Android Bitmaps that are loaded either from gallery, network or SD card.

Using Blink Receipt Earnings in your app requires a valid license key.  After registering, you will be able to generate a license key for your app. License key is bound to package name of your app, so please make sure you enter the correct package name when asked.

See below for more information about how to integrate Blink Receipt SDK into your app.

## AAR
The package contains Android Archive (AAR) that contains everything you need to use Blink Receipt Earnings library.

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
    implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.7.6"))

    implementation("com.microblink.blinkreceipt:blinkreceipt-earnings")
}
```

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 21+
- Compile SDK: 29+
- Java 8+

### Product Intelligence
If you wish to include product intelligence functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```xml
 <meta-data android:name="com.microblink.ProductIntelligence" android:value="PRODUCT INTELLIGENCE KEY" />
```
