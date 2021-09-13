# Blink Receipt Core SDK

Blink Receipt Core SDK for Android is an SDK that enables you to easily add near real time OCR functionality to your app with the purpose of scanning receipts. With provided camera management you can easily create an app that scans receipts. You can also scan images stored as Android Bitmaps that are loaded either from gallery, network or SD card.

Using Blink Receipt Core in your app requires a valid license key.  After registering, you will be able to generate a license key for your app. License key is bound to package name of your app, so please make sure you enter the correct package name when asked.

See below for more information about how to integrate Blink Receipt SDK into your app.

## AAR
The package contains Android Archive (AAR) that contains everything you need to use Blink Receipt Core library.

## <a name=intro></a> Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

```groovy
dependencies {
    implementation "androidx.appcompat:appcompat:1.2.0"

    implementation "androidx.core:core-ktx:1.6.0"

    implementation "androidx.work:work-runtime:2.5.0"
    implementation "androidx.work:work-runtime-ktx:2.5.0"

    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2"

    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.30"

    implementation 'com.squareup.okhttp3:okhttp:4.9.1'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'

    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.9.0'

    implementation "com.google.android.gms:play-services-tasks:17.2.1"

    implementation 'com.squareup.okio:okio:2.10.0'

    implementation 'com.jakewharton.timber:timber:5.0.1'

    implementation "androidx.preference:preference-ktx:1.1.1"
}
```

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 21+
- Compile SDK: 29+
- Java 8+
