# Blink Receipt Earnings SDK

Blink Receipt Earnings SDK for Android is an SDK that enables you to easily add earnings functionality to your app with the purpose of scanning receipts. With provided camera management you can easily create an app that scans receipts. You can also scan images stored as Android Bitmaps that are loaded either from gallery, network or SD card.

Using Blink Receipt Earnings in your app requires a valid license key.  After registering, you will be able to generate a license key for your app. License key is bound to package name of your app, so please make sure you enter the correct package name when asked.

See below for more information about how to integrate Blink Receipt SDK into your app.

## AAR
The package contains Android Archive (AAR) that contains everything you need to use Blink Receipt Earnings library.

## <a name=intro></a> Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

```
dependencies {
    implementation 'androidx.appcompat:appcompat:1.0.0'

    implementation "com.google.android.gms:play-services-tasks:17.0.2"

    implementation 'com.squareup.okhttp3:okhttp:4.5.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.5.0'
    
    implementation 'com.squareup.retrofit2:retrofit:2.8.1'
    implementation 'com.squareup.retrofit2:converter-gson:2.8.1'
    implementation 'com.squareup.retrofit2:converter-scalars:2.8.1'
    
    implementation 'com.squareup.okio:okio:2.4.3'

    implementation 'com.jakewharton.timber:timber:4.7.1'

    api project( ':blinkreceipt-core' )
}
```

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 21+
- Compile SDK: 29+
- Java 8+