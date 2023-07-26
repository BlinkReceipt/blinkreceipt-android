
The Account Linking SDK enables you to easily add user-permissioned merchant connection functionality to your app with the purpose of extracting order history from online retailer accounts.

It supports parsing digital and in-store orders from a growing list of online retailers.

## Integration

1. Download the [LibBlinkReceiptAccountLinking.aar](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-account-linking/LibBlinkReceiptAccountLinking.aar) and [LibBlinkReceiptCore.aar](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-core/LibBlinkReceiptCore.aar) files from the [BlinkReceipt GitHub repository](https://github.com/BlinkReceipt/blinkreceipt-android), which are located in
   their subfolders.

2. Add the `.aar` files to your Android project. We recommend creating a `libs` folder in the root of your project and placing them inside.

3. Reference the `.aar` files in your `build.gradle` dependencies block like this:

    === "Kotlin"
    
        ``` kotlin title="build.gradle.kts"
        implementation(fileTree("./libs"))
        // or reference each file separately, e.g.:
        implementation(files("./libs/LibBlinkReceiptCore.aar"))
        implementation(files("./libs/LibBlinkReceiptAccountLinking.aar"))
        ```
    
    === "Groovy"
    
        ``` groovy title="build.gradle"
        implementation fileTree("./libs")
        // or reference each file separately, e.g.:
        implementation files("./libs/LibBlinkReceiptCore.aar")
        implementation files("./libs/LibBlinkReceiptAccountLinking.aar")
        ```


4. Add our transitive dependencies to your `build.gradle` dependencies block:

    === "Kotlin"
    
        ``` kotlin title="build.gradle.kts"
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21")
        implementation("androidx.core:core-ktx:1.6.0")
    
        implementation("androidx.work:work-runtime:2.6.0")
        implementation("androidx.work:work-runtime-ktx:2.6.0")
    
        implementation("androidx.appcompat:appcompat:1.2.0")
    
        implementation("com.squareup.okhttp3:okhttp:4.9.3")
        implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    
        implementation("com.squareup.okio:okio:3.0.0")
    
        implementation("com.jakewharton.timber:timber:5.0.1")
    
        implementation("com.google.android.gms:play-services-tasks:18.0.1")
    
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
    
        implementation("androidx.webkit:webkit:1.4.0")
    
        implementation("androidx.datastore:datastore-preferences:1.0.0")
        ```
    
    === "Groovy"
    
        ``` groovy title="build.gradle"
        implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21"
        implementation "androidx.core:core-ktx:1.6.0"
    
        implementation "androidx.work:work-runtime:2.6.0"
        implementation "androidx.work:work-runtime-ktx:2.6.0"
    
        implementation "androidx.appcompat:appcompat:1.2.0"
    
        implementation "com.squareup.okhttp3:okhttp:4.9.3"
        implementation "com.squareup.okhttp3:logging-interceptor:4.9.3"
    
        implementation "com.squareup.retrofit2:retrofit:2.9.0"
        implementation "com.squareup.retrofit2:converter-gson:2.9.0"
        implementation "com.squareup.retrofit2:converter-scalars:2.9.0"
    
        implementation "com.squareup.okio:okio:3.0.0"
    
        implementation "com.jakewharton.timber:timber:5.0.1"
    
        implementation "com.google.android.gms:play-services-tasks:18.0.1"
    
        implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1"
        implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1"
        implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1"
    
        implementation "androidx.webkit:webkit:1.4.0"
    
        implementation "androidx.datastore:datastore-preferences:1.0.0"
        ```


5. Since the SDK is written in kotlin, if not already, you'll have to add the kotlin gradle plugin to your classpath in your root `build.gradle`:

    === "Kotlin"
    
        ``` kotlin title="build.gradle.kts"
        buildscript {
            dependencies {
                classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
            }
        }
        ```
    === "Groovy"
        ```groovy title="build.gradle"
        buildscript {
            dependencies {
                classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21"
            }
        }
        ```

## Setting your license keys

There are two ways you can set the license keys for the Account Linking SDK.

### Android Manifest

In your `AndroidManifest.xml` file, create the following `meta-data` tags, and insert your keys under the `android:value` attribute:

```xml
<meta-data
    android:name="com.microblink.LicenseKey"
    android:value=""
/>

<meta-data
    android:name="com.microblink.ProductIntelligence"
    android:value=""
/>
```

### Programmatically

If you want to set your keys programmatically, you can do so by setting them on the appropriate `BlinkReceiptLinkingSdk` fields in your application class in the `onCreate` method:

```kotlin
BlinkReceiptLinkingSdk.licenseKey = "account_linking_license_key"
BlinkReceiptLinkingSdk.productIntelligenceKey = "product_intelligence_license_key"
```

## Initializing the SDK

Before you can use the SDK it needs to be initialized. The best way to do this is to call the following function in your application class in the
`onCreate` method:

``` kotlin
BlinkReceiptLinkingSdk.initialize(this, object: InitializeCallback {
    override fun onComplete() {
    }

    override fun onException(throwable: Throwable) {
    // log exception...
    }
})
```

That's it! You're all set up to use the Account Linking SDK. Check out the next chapter for more information on how to use the SDK.
