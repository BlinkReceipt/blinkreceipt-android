
# Blink Receipt Digital SDK

Blink Receipt Digital SDK for Android is an SDK that enables you to easily add e-receipt functionality to your app with the purpose of scanning receipts. Blink Receipt Digital SDK  supports parsing e-receipts from a growing list of retailers and a specific set of mail providers: Gmail, Outlook, Yahoo, and AOL. The procedure for integrating each of these providers is slightly different. This guide will outline the steps necessary to integrate and authenticate a user account for each mail provider, and then the common methods that you will use to invoke the e-receipt parsing functionality.

See below for more information about how to integrate Blink Receipt Digital SDK into your app.

## AAR
The package contains Android Archive (AAR) that contains everything you need to use Blink Receipt Digital library.

##Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

```
dependencies {
     api project( ':blinkreceipt-core' )

    implementation 'com.squareup.okhttp3:okhttp:4.7.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.7.0'
    
    implementation 'com.squareup.retrofit2:retrofit:2.8.2'
    implementation 'com.squareup.retrofit2:converter-gson:2.8.2'
    implementation 'com.squareup.retrofit2:converter-scalars:2.8.2'
    
    implementation 'com.squareup.okio:okio:2.6.0'

    implementation 'com.jakewharton.timber:timber:4.7.1'

    implementation "com.google.android.gms:play-services-tasks:17.0.2"

    //Outlook
    implementation( "com.microsoft.identity.client:msal:${versions.msal}" ) {
        exclude group: 'com.microsoft.device.display'
    }
}
```

## Outlook

Outlook integration requires 1 additional AAR in your build gradle.

```
dependencies {

    implementation( "com.microsoft.identity.client:msal:1.5.1" ) {
        exclude group: 'com.microsoft.device.display'
    }
}
```

You must also register an application in the [MS Application Registration Portal](https://apps.dev.microsoft.com/), add a Native Application platform, and obtain an Application ID.

### Android Manifest 

```
<activity android:name="com.microsoft.identity.client.BrowserTabActivity">  
 <intent-filter> <action android:name="android.intent.action.VIEW" />  
  
 <category android:name="android.intent.category.DEFAULT" />  
 <category android:name="android.intent.category.BROWSABLE" />  
  
 <data android:host="com.blinkreceipt.development"  
  android:path="/[Signature Hash]"  
  android:scheme="msauth" />  
 </intent-filter></activity>
```
### Authentication Configuration
Outlook client will reference the authentication configuration file location in resources raw folder. Create **auth_config_single_account.json** under res/raw

**Single Account**
```
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
```
OutlookClient(applicationContext, R.raw.auth_config_single_account, object : InitializeCallback {  
  
    override fun onComplete() {  
          
    }  
  
    override fun onException(throwable: Throwable) {  
        
    }  
  
}) 
```
### Outlook Login
```
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
```
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
```
client.messages().addOnSuccessListener(object : OnSuccessListener<List<ScanResults>> {  
  
    override fun onSuccess(data: List<ScanResults>?) {  
         
    }  
  
}).addOnFailureListener(object : OnFailureListener {  
  
    override fun onFailure(e: Exception) {  
          
    }  
  
})
```
### Outlook Client Terminate
```
override fun onDestroy() {  
    super.onDestroy()  
  
    client.terminate()  
}
```
##  Requirements
- AndroidX
- Min SDK 21+
- Compile SDK: 29+
- Java 8+