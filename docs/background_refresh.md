## Overview

BlinkReceipt Background Refresh allows your app to automatically fetch orders without an active user session and without any manual requests. Upon enabling background refresh, our server will send 
periodic FCM push notifications to the app, instructing it to perform actions such as grab orders (Account Linking) and remote scrape (IMAP) in the background depending on the status of the linked 
accounts.

## Messaging SDK

To use background refresh, your app must now additionally depend on the new `blinkreceipt-messaging` SDK. It is possible to use `blinkreceipt-messaging` to fetch orders and initiate remote scrape 
for IMAP without having dependency on `blinkreceipt-account-linking` and `blinkreceipt-digital`; however, we recommend your app continue to support foreground order fetching and to keep these 
dependencies in your app.

Here are the steps to integrate `blinkreceipt-messaging`:

### Include `blinkreceipt-messaging` as a dependency

Include `blinkreceipt-messaging` as a dependency in your `build.gradle` or `build.gradle.kts` files.

### Integrate Firebase Cloud Messaging (FCM)

Your app needs to support Firebase Cloud Messaging (FCM) so that it can receive push notifications from BlinkReceipt. If it is not yet supported on your app, follow Google's official documentation
on Android FCM integration: https://firebase.google.com/docs/cloud-messaging/android/client

## SDK Initialization

The blinkreceipt-messaging SDK needs to be statically initialized before use. `MessagingSDK` is a public singleton object that contains the static initializer `initialize`, which is a Kotlin suspend 
function that returns a Boolean indicating whether the initialization is successful. Internally, this initializes the Account Linking SDK and the Digital SDK as well.

### Initializing with license key
You can pass in your BlinkReceipt license key as an argument to the static initializer, and statically set the `productIntelligenceKey`.
```kotlin
MessagingSDK.productIntelligenceKey = "<your_product_intelligence_key>" // Optional: statically set Product Intelligence key
val success = MessagingSDK.initialize(context, licenseKey)
```

### Initializing without license key
You can also initialize `MessagingSDK` without passing in the license key as an argument, but you need to declare the license key and product intelligence key under `AndroidManifest.xml`. Otherwise, 
the initialization will fail.

- `AndroidManifest.xml`

```xml
<manifest>
    <!-- -->
    <application>
        <!-- -->
        <meta-data
            android:name="com.microblink.ProductIntelligence"
            android:value="<Your Product Intelligence License Key>" />
        <!-- -->
    </application>
</manifest>
```

- Initialization Code
```kotlin
// No need to explicitly indicate Product Intelligence License Key since it was already defined from AndroidManifest.xml
val success = MessagingSDK.initialize(context)
```

### Initialization using AndroidX Startup framework
By default, we provide an AndroidX Startup initializer `SdkInitializer` that automatically initializes the Messaging SDK using license keys defined in your `AndroidManifest.xml`. If you prefer initialing 
yourself or if you don't provide keys in `AndroidManifest.xml`, you can disable the `SdkInitializer` in your app's `AndroidManifest.xml`:
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <!-- remove the BlinkReceipt Messaging default initializer -->
    <meta-data
        android:name="com.microblink.messaging.internal.SdkInitializer"
        tools:node="remove" />
</provider>
```
## Forwarding FCM Messages to the Messaging SDK

Upon reception of a message from FCM, it needs to be forwarded to the Messaging SDK for Background Refresh. Below are the steps to properly forward messages to the Messaging SDK:

### 1. Create MessagingClient instance

`MessagingClient` is the primary entry point for Background Refresh -- it contains necessary methods for processing FCM messages, as well as a SharedFlow for collecting background fetch results. The constructor for
`MessagingClient` requires a `Context` and a `CoroutineScope`. `MessagingClient` is also a `Closeable` -- it cancels the `CoroutineScope` that is used to construct its instance. If no `coroutineScope` is passed in, 
a default scope will be created.
```kotlin
val messagingClient = MessagingClient(
    applicationContext,
    lifecycleScope, // use lifecycleScope as an example
)

// perform background refresh and other activities

messagingClient.close() // do not close if coroutine scope is externally managed
```

### 2. Implement FirebaseMessagingService and delegate into MessagingClient

If you haven't done so as part of your app's FCM integration, implement a subclass of `FirebaseMessagingService` and statically register it to your application. Inside, delegate its methods to `MessagingClient`, such as this example:
```kotlin
open class YourMessagingService: FirebaseMessagingService() {
    
    // an instance of MessagingClient needs to be accessible here -- 
    // whichever way it is provided (singleton, via injection, etc.)
    val messagingClient: MessagingClient by lazy { MessagingClient(applicationContext, scope) }

    override fun onMessageReceived(message: RemoteMessage) {
        messagingClient.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        messagingClient.onNewToken(token)
    }
}
```

- **MessagingClient.onMessageReceived**: This method should be called whenever a push notification message arrives from FCM. Internally, we will filter the messages and only process those sent by BlinkReceipt. This method will then initiate
grab orders or remote scrape in the background, depending on the instruction provided by the notification message.
**Note: if your FCM messages contain sensitive data and you do not want to forward all messages to `MessagingClient`, then forward based on condition `remoteMessage.data["provider"] == "BlinkReceipt"`**

- **MessagingClient.onNewToken**: This method should be called whenever a new token is generated by FCM, so we can keep track of it in our systems.
  
To statically register this service class, declare the below in your app's `AndroidManifest.xml`:
```xml
<service
    android:name="com.example.YourMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

## Collect Results from Background

To collect results from Background Refresh, you should listen to the SharedFlow `results` provided by `MessagingClient`, which produces a stream of `MessagingResult`s:
```kotlin
lifecycleScope.launch {
    messagingClient.results.collect { messagingResult: MessagingResult ->
        when (messagingResult) {
            is MessagingResult.Linking -> {
                val (retailerId, scanResults, remaining, uuid) = result
                // save scanResults to disk, etc.
            }
            is MessagingResult.Digital -> {
                val (retailerId, jobResults) = result
                // perform actions depending whether jobResults is successful
            }
            is MessagingResult.Error -> {
                // Log error -- this does not terminate the results flow
            }
        }
    }   
}
```
Note that this flow normally does not terminate, so it's collection is recommended to be bound to the lifecycle of your application.
