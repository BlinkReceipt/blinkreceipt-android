You must register an application in the [MS Application Registration Portal](https://apps.dev.microsoft.com/), add a Native Application platform, and obtain an Application ID.

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
=== "Kotlin"
    ```kotlin
    OutlookClient(applicationContext, R.raw.auth_config_single_account, object : InitializeCallback {
    
        override fun onComplete() {
    
        }
    
        override fun onException(throwable: Throwable) {
    
        }
    
    })
    ```

### Outlook Login
=== "Kotlin"
    ```kotlin
        client.login(this).addOnSuccessListener {
    
       }.addOnFailureListener {
    
       }
    ```
### Outlook Logout
=== "Kotlin"
    ```kotlin
    client.logout().addOnSuccessListener {
    
    }.addOnFailureListener {
    
    }
    ```

### Outlook Messages
Messages returns a Task, which allows you to get a list of scan results for messages found in the Outlook mailbox.
=== "Kotlin"
    ```kotlin
     client.messages().addOnSuccessListener {
    
    }.addOnFailureListener {
    
    }
    ```
### Outlook Destroy Client
=== "Kotlin"
    ```kotlin
    override fun onDestroy() {
        super.onDestroy()
    
        client.close()
    }
    ```