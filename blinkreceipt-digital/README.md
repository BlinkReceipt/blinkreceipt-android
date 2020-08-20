
# Blink Receipt Digital SDK

Blink Receipt Digital SDK for Android is an SDK that enables you to easily add e-receipt functionality to your app with the purpose of scanning receipts. Blink Receipt Digital SDK  supports parsing e-receipts from a growing list of retailers and a specific set of mail providers: Gmail, Outlook, Yahoo, and AOL. The procedure for integrating each of these providers is slightly different. This guide will outline the steps necessary to integrate and authenticate a user account for each mail provider, and then the common methods that you will use to invoke the e-receipt parsing functionality.

See below for more information about how to integrate Blink Receipt Digital SDK into your app.

## AAR
The package contains Android Archive (AAR) that contains everything you need to use Blink Receipt Digital library.

## Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

```groovy
dependencies {
     api project( ':blinkreceipt-core' )

    implementation 'com.squareup.okhttp3:okhttp:4.8.1'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.8.1'

    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.9.0'

    implementation "androidx.constraintlayout:constraintlayout:1.1.3"

    implementation "androidx.core:core:1.3.1"

    implementation 'com.squareup.okio:okio:2.7.0'

    implementation 'com.jakewharton.timber:timber:4.7.1'

    implementation "com.google.android.gms:play-services-tasks:17.1.0"
}
```

Initialize the `BlinkReceiptDigitalSdk` in your application class.
 
```kotlin
class BlinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
            }

            override fun onException(e: Throwable) {
            }

        })
    }
}
```

## IMAP

IMAP integration requires 9 additional AAR in your build gradle.

```groovy
dependencies {

    implementation "androidx.lifecycle:lifecycle-viewmodel:2.2.0"
    implementation "androidx.lifecycle:lifecycle-livedata:2.2.0"
    implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0"
    implementation "androidx.webkit:webkit:1.3.0-rc02"
    
    implementation "androidx.security:security-crypto:1.1.0-alpha02"
    implementation "com.sun.mail:android-mail:1.6.5"
    implementation "com.sun.mail:android-activation:1.6.5"
    
    implementation "androidx.fragment:fragment:1.2.5"
    
    implementation "com.google.android.material:material:1.2.0"

}
```

### Android Manifest

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <uses-feature
        android:name="android.hardware.camera.autofocus"
        android:required="false" />

    <uses-feature
        android:name="android.hardware.camera.flash"
        android:required="false" />

    <application
        android:name=".BlinkApplication"
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        tools:ignore="GoogleAppIndexingWarning">

        <meta-data
            android:name="com.microblink.ProductIntelligence"
            android:value="KEY" />

    </application>

</manifest>
```

### Themes & Styles

IMAP authorization UI uses the material bottom sheet. This requires your theme parent to extend `Theme.MaterialComponents.*`

```xml
<resources>

    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

</resources>

```

### IMAP Client
IMAP client is the main entry point which allows the SDK to connect to imap accounts. Initializing the client is asynchronous and requires the caller to wait until its complete before accessing the imap messages or account information. ***Note: if you use lazy this will cause exceptions until the client has been initialized. This is on a per instance basis.***

```kotlin
private val options by lazy {
    ProviderSetupOptions.newBuilder(Provider.YAHOO)
        .build()
}

ImapClient( applicationContext, options.provider(), object : InitializeCallback {

    override fun onComplete() {

    }

    override fun onException(throwable: Throwable) {

    }
  }
)
```

## Credentials

IMAP client requires the caller to collect the user's imap credentials that correspond to the selected provider. Credentials will be securely stored using JetPack Security.

```kotlin
client.credentials(
        PasswordCredentials(
                "EMAIL",
                "PASSWORD"
        )
).addOnSuccessListener {
    
}.addOnFailureListener {

}
```

## Provider Setup
After collecting the users credentials initiate the provider setup workflow, which will walk the user through linking their account to the provider (Yahoo, AOL, Gmail). 

```kotlin
 ProviderSetupDialogFragment.newInstance(options)
    .callback {
        when (it) {
            ProviderSetupResults.BAD_PASSWORD -> Timberland.e("BAD_PASSWORD")
            ProviderSetupResults.BAD_EMAIL -> Timberland.e("BAD_EMAIL")
            ProviderSetupResults.CREATED_APP_PASSWORD -> Timberland.d("CREATED_APP_PASSWORD")
            ProviderSetupResults.NO_CREDENTIALS -> Timberland.e("NO_CREDENTIALS")
            ProviderSetupResults.UNKNOWN -> Timberland.e("UNKNOWN")
        }
    }.show(supportFragmentManager, TAG)
```

### IMAP Verify Account
```kotlin
client.verify().addOnSuccessListener {
    
}.addOnFailureListener {
    
}
```

### IMAP Messages
```kotlin
client.messages().addOnSuccessListener {
  
}.addOnFailureListener {
    
}
```

### IMAP Logout
```kotlin
 client.logout().addOnSuccessListener {
   
}.addOnFailureListener {
    
}
```
### IMAP Clear
```kotlin
client.clearLastCheckedTime().addOnSuccessListener {
    
}
```

### IMAP Destroy Client
```kotlin
override fun onDestroy() {
    super.onDestroy()

    client.destroy()
}
```

## Outlook

Outlook integration requires 1 additional AAR in your build gradle.

```groovy
dependencies {

    implementation( "com.microsoft.identity.client:msal:1.6.0" ) {
        exclude group: 'com.microsoft.device.display'
    }
}
```

You must also register an application in the [MS Application Registration Portal](https://apps.dev.microsoft.com/), add a Native Application platform, and obtain an Application ID.

### Android Manifest

```xml
<activity android:name="com.microsoft.identity.client.BrowserTabActivity">
     <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

         <data android:host="com.blinkreceipt.development"
          android:path="/[Signature Hash]"
          android:scheme="msauth" />
     </intent-filter>
</activity>
```

### Authentication Configuration
Outlook client will reference the authentication configuration file location in resources raw folder. Create **auth_config_single_account.json** under res/raw

**Single Account**
```json
{
  "client_id" : "[CLIENT ID]",
  "authorization_user_agent" : "DEFAULT",
  "redirect_uri" : "[REDIRECT URI]",
  "account_mode" : "SINGLE",
  "broker_redirect_uri_registered": true,
  "authorities" : [
    {
      "type": "AAD",
  "authority_url": "https://login.microsoftonline.com/common"
  }
  ]
}
```
### Outlook Client
Outlook client is the main entry point which allows the SDK to connect to outlook accounts. Initializing the client is asynchronous and requires the caller to wait until its complete before accessing the outlook messages or account information. ***Note: if you use lazy this will cause exceptions until the client has been initialized. This is on a per instance basis.***
```kotlin
OutlookClient(applicationContext, R.raw.auth_config_single_account, object : InitializeCallback {

    override fun onComplete() {

    }

    override fun onException(throwable: Throwable) {

    }

})
```
### Outlook Login
```kotlin
client.login(this).addOnSuccessListener(
        object : OnSuccessListener<Account> {

            override fun onSuccess(data: Account?) {

            }

        }).addOnFailureListener(object : OnFailureListener {

    override fun onFailure(e: Exception) {

    }

})
```
### Outlook Logout
```kotlin
client.logout()
        .addOnSuccessListener(object : OnSuccessListener<Boolean> {

            override fun onSuccess(data: Boolean?) {

            }

        }).addOnFailureListener(object : OnFailureListener {

            override fun onFailure(e: Exception) {

            }

        })
```

### Outlook Messages
Messages returns a Task, which allows you to get a list of scan results for messages found in the Outlook mailbox.
```kotlin
client.messages().addOnSuccessListener(object : OnSuccessListener<List<ScanResults>> {

    override fun onSuccess(data: List<ScanResults>?) {

    }

}).addOnFailureListener(object : OnFailureListener {

    override fun onFailure(e: Exception) {

    }

})
```
### Outlook Destroy Client
```kotlin
override fun onDestroy() {
    super.onDestroy()

    client.destroy()
}
```
##  Requirements
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