
# Blink Account Linking SDK

Blink Receipt Account Linking SDK for Android is an SDK that enables you to easily add e-receipt functionality to your app with the purpose of scanning receipts. Blink Receipt Account Linking SDK supports parsing e-receipts from a growing list of retailers( [Supported Retailers](https://blinkreceipt.github.io/blinkreceipt-android/blinkreceipt-account-linking/com/microblink/linking/RetailerIdKt.html)). This guide will outline the steps necessary to integrate and authenticate a user account for each provider, and then the common methods that you will use to invoke the e-receipt parsing functionality.

See below for more information about how to integrate Blink Receipt Account Linking SDK into your app.

## Setup

To setup the `Blink Receipt Account Linking SDK` then please pull in the aar file into your project. Use of this SDK will also depend on the `Blink Receipt Core SDK` sdk module as well.

Dependencies

In addition to those 2 modules you will need to pull in the following as well.

```groovy
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10"
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

    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0"

    implementation "androidx.webkit:webkit:1.4.0"

    implementation "androidx.datastore:datastore-preferences:1.0.0"
```

You may notice that there are Kotlin dependencies as part of this list. This is because the SDK is written in Kotlin. If your app does not use Kotlin that is ok you will just need to configure the app to use kotlin, but this will not require you to change programming languages as Kotlin is compatible with java.

Project build.gradle

```groovy
buildscript {
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10"
    }
}
```

## Initialize the Account Linking SDK

In you application class override the `onCreate` method and initialize the SDK.
```kotlin

BlinkReceiptLinkingSdk.initialize( this, object: InitializeCallback {
    override fun onComplete() {
    }

    override fun onException(throwable: Throwable) {
    }
} )

```

## Android Manifest

In order to access linked retailers you needs to provide your license and product intelligence key. The keys can be configured in either the manifest or programmatically.

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

```kotlin
BlinkReceiptLinkingSdk.licenseKey = ""
BlinkReceiptLinkingSdk.productIntelligenceKey = ""
```

## How to Access

`AccountLinkingClient` is the entry point into the different retailers.

```kotlin
private lateinit var client: AccountLinkingClient

client = AccountLinkingClient(applicationContext)
```

## Link Retailer

Before parsing retailer information you should capture the users credentials and link it through the client. Note** failing to link a retailer will throw an exception if attempting to retrieve orders or verify the account.

```kotlin
val account = Account(
    WALMART,
    PasswordCredentials(
        "",
        ""
    )
)

client.link(account).addOnSuccessListener {

}.addOnFailureListener {

}
```

## Verify Account

Linked accounts can be verified by calling `verify` on the `AccountLinkingClient` and passing in the linked retailer id.

```kotlin
val account = Account(
    WALMART,
    PasswordCredentials(
        "",
        ""
    )
)

client.verify(account.retailerId) {
    //Preview should only be used in development.
}.addOnSuccessListener {

}.addOnFailureListener {

}
```

## Orders

Locate orders by passing in the linked retailer id.

```kotlin
val account = Account(
    WALMART,
    PasswordCredentials(
        "",
        ""
    )
)

client.orders(account.retailerId,
    { retailerId, results, remaining ->

    },
    { retailerId, throwable ->

    },
    {
        //preview is only available in development
    }
)
```

## All Orders

Locate orders for all linked retailers.

```kotlin

client.orders({ retailerId, results, remaining ->

}, { retailerId, throwable ->

})

```

## Unlink Retailers

By calling `unlink` on the `AccountLinkingClient` will remove all linked retailers.

```kotlin
client.unlink().addOnSuccessListener {

}.addOnFailureListener {

}
```

## Unlink Retailer

By calling `unlink` and passing in an `Account` on the `AccountLinkingClient` will remove the linked retailer.

```kotlin
val account = Account(
    WALMART,
    PasswordCredentials(
        "",
        ""
    )
)

client.unlink(account).addOnSuccessListener {

}.addOnFailureListener {

}
```

## Reset Retailers History

By calling `resetHistory` on the `AccountLinkingClient` removes the history for all linked retailers.

```kotlin
client.resetHistory().addOnSuccessListener {

}.addOnFailureListener {

}
```

## Reset Retailer History

By calling `resetHistory` and passing in an `Account` retailer id on the `AccountLinkingClient` removes the history for the linked retailer.

```kotlin
client.resetHistory().addOnSuccessListener {

}.addOnFailureListener {

}
```

## Lifecycle

In `onDestroy` of your fragment or activity close the `AccountLinkingClient` to reclaim resources and prevent memory leaks.

```kotlin
override fun onDestroy() {
    client.close()

    super.onDestroy()
}
```
