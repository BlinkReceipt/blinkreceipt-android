
The Account Linking SDK enables you to easily add user-permissioned merchant connection functionality to your app with the purpose of extracting order history from online retailer accounts.

It supports parsing digital and in-store orders from a growing list of online retailers.

## Integration

1. Add the following maven repository to your build.gradle or settings.gradle, depending on your implementation:

=== "Kotlin"
    ```kotlin
     repositories {
       maven { url = uri("https://maven.microblink.com") }
     }
    ```
=== "Groovy"
    ```groovy
     repositories {
       maven { url "https://maven.microblink.com" }
     }
    ```

2. Add the following to your dependency section in your app `build.gradle`.

=== "Kotlin"
    ```kotlin
    dependencies {
    implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.7.5"))

    implementation("com.microblink.blinkreceipt:blinkreceipt-account-linking")
    }
    ```
=== "Groovy"
    ```groovy
    dependencies {
    implementation platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.7.5")

    implementation "com.microblink.blinkreceipt:blinkreceipt-account-linking"
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

=== "Kotlin"
    ```kotlin
    BlinkReceiptLinkingSdk.licenseKey = "account_linking_license_key"
    BlinkReceiptLinkingSdk.productIntelligenceKey = "product_intelligence_license_key"
    ```
=== "Java"
    ```java
    BlinkReceiptLinkingSdk.licenseKey("account_linking_license_key");
    BlinkReceiptLinkingSdk.productIntelligenceKey("product_intelligence_license_key");
    ```

## Initializing the SDK

Before you can use the SDK it needs to be initialized. The best way to do this is to call the following function in your application class in the
`onCreate` method:

=== "Kotlin"
    ``` kotlin
    BlinkReceiptLinkingSdk.initialize(this, object: InitializeCallback {
        override fun onComplete() {
        }

        override fun onException(throwable: Throwable) {
        // log exception...
        }
    })
    ```
=== "Java"
    ```java
    BlinkReceiptLinkingSdk.initialize(this, new InitializeCallback() {
        @Override
        public void onComplete() {

        }

        @Override
        public void onException(@NonNull Throwable throwable) {
            // log exception
        }
    });
    ```

That's it! You're all set up to use the Account Linking SDK. Check out the next chapter for more information on how to use the SDK.
