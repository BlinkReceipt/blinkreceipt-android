The Recognizer SDK enables you to easily scan digital and physical receipts.

## Integration

1. Add the following maven repository to your build.gradle or settings.gradle, depending on your implementation:

=== "Kotlin"
    ```kotlin
     repositories {
       mavenCentral()
     }
    ```
=== "Groovy"
    ```groovy
     repositories {
       mavenCentral()
     }
    ```

2. Add the following to your dependency section in your app `build.gradle`.

=== "Kotlin"
    ```kotlin
    dependencies {
        implementation(platform("com.actualplatform:receipt-bom:{{ blinkreceipt.release }}"))

        implementation("com.actualplatform:receipt-recognizer")
    }
    ```
=== "Groovy"
    ```groovy
    dependencies {
        implementation platform("com.actualplatform:receipt-bom:{{ blinkreceipt.release }}")

        implementation "com.actualplatform:receipt-recognizer"
    }
    ```

## Setting your license keys

There are two ways you can set the license keys for the Account Linking SDK.

### Android Manifest

In your `AndroidManifest.xml` file, create the following `meta-data` tags, and insert your keys under the `android:value` attribute:

```xml
<meta-data
    android:name="com.actualplatform.receipt.LicenseKey"
    android:value=""
/>

<meta-data
    android:name="com.actualplatform.receipt.ProductIntelligence"
    android:value=""
/>
```

Both the values for `com.actualplatform.receipt.LicenseKey` and `com.actualplatform.receipt.ProductIntelligence` can be obtained by emailing [blinkreceipt@microblink.com](mailto:blinkreceipt@microblink.com).

### Programmatically

If you want to set your keys programmatically, you can do so by setting them on the appropriate `ReceiptSdk` fields in your application class in the `onCreate` method:

=== "Kotlin"
    ```kotlin
    ReceiptSdk.productIntelligenceKey("product_intelligence_license_key")
    ```

## Initializing the SDK

Before you can use the SDK it needs to be initialized. The best way to do this is to call the following function in your application class in the
`onCreate` method:

=== "Kotlin"
    ``` kotlin
    ReceiptSdk.initialize(this, object : InitializeCallback {
        override fun onComplete() {}

            override fun onException(exception: Throwable) {}

        })
    ```

That's it! You're all set up to use the Recognizer SDK. Check out the next chapter for more information on how to use the SDK.
