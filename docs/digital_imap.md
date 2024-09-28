### Android Manifest

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".BlinkApplication"
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

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

=== "Kotlin"
    ```kotlin
    ImapClient( applicationContext, object : InitializeCallback {
    
        override fun onComplete() {
    
        }
    
        override fun onException(throwable: Throwable) {
    
        }
      }
    )
    ```
## Credentials
A `Credentials` object defines the credentials being used to login to a specific IMAP provider. `Credentials` is a sealed class that contains 2 subtypes: `None` and `Password`, each of which contains a collection of subtypes corresponding to the IMAP provider.

=== "Kotlin"
    ```kotlin
        sealed class Credentials {
            sealed class None {
                data class Gmail(val appPassword: AppPassword)  
            }
            sealed class Password {
                data class Gmail(val username: String, val password: String)
                data class Yahoo(val username: String, val password: String)
                data class Aol(val username: String, val password: String)
            }
        }
    ```

`Credentials.Password` accepts username and password inputs from the application UI. `Credentials.None` allows for IMAP web authentication (where the user inputs credentials via the provider's WebView login page), which is functionally analogous to the BlinkReceipt Account Linking SDK where Retailer Web Auth is enabled using `Credentials.NONE`. The `AppPassword` enum parameter in `None` is used to specify whether the user has to manually input a Gmail app password name during the authentication process (`AppPassword.MANUAL`) or the Gmail app password name should be automatically injected using your application's name (`AppPassword.AUTOMATIC`). If this enum flag is not specified, `AppPassword.AUTOMATIC` will be used by default.

**Note: `Credentials` replaces `PasswordCredentials` in the IMAP public API surface starting from version 1.8.6.**

**Note: Linking with `Credentials.None.Gmail` will cause a `Credentials.Password.Gmail` to be saved to disk if login is successful. The saved `Credentials.Password.Gmail` object can be retrieved for other operations.**

## Provider Setup
To start the IMAP account linking process, we first create a `Credentials` object, then call `ProviderSetupFragmentFactory.create(credentials)` that returns a `ProviderFragment`. The fragment accepts a callback that receives a `ProviderResults` object containing a `Credentials` and a `ProviderSetupResults` enum. The callback will be invoked after the authentication process is finished.

Example: Gmail (web authentication) workflow

=== "Kotlin"
    ```kotlin
    val credentials: Credentials = Credentials.None.Gmail()

    ProviderSetupFragmentFactory.create(credentials).callback { result: ProviderResult ->
        // do something upon receiving provider result
    }.show(supportFragmentManager, TAG)
    ```

Example: Gmail (traditional) authentication workflow

=== "Kotlin"
    ```kotlin
    val credentials: Credentials = Credentials.Password.Gmail(
        "email@microblink.com", 
        "account password"
    )

    ProviderSetupFragmentFactory.create(credentials).callback { result: ProviderResult ->
        // do something upon receiving provider result
    }.show(supportFragmentManager, TAG)
    ```

**Note: `ProviderSetupDialogFragment.newInstance(provider)` is no longer supported since version 1.8.6.**

**Note: The `ProviderResults` callback result will only contain valid and authenticated credentials if `ProviderSetupResults` is `CREATED_APP_PASSWORD`. Otherwise, `ProviderResults` contains the original credentials you created.**

**Note: `Credentials.None` should only be used here at account login, and not at any other public API.**

### IMAP Login To/Verify Account
The `verify()` function is used to determine if the sdk has any cached `Credentials.Password` that can be used without explicit sign in. This can be called without any parameters or with an `Executor` and `Credentials.Password`. The empty parameter function call will automatically attempt to fetch the cached `Credentials.Password` within the sdk and verify it against the ImapService. This function call returns a `Task<Boolean>`. When the result emitted is a `true` value, then the `Credentials.Password` that were either passed in or cached in the sdk grant access to a valid account. In the event an exception is thrown, that means that the `Credentials.Password`, either passed in or cached, are not valid to access a specific account.

=== "Kotlin"
    ```kotlin
    val credentials: Credentials.Password = Credentials.Password.Gmail("email@microblink.com", "account password")
    
    client.verify(credentials).addOnSuccessListener { isVerified: Boolean ->
    
    }.addOnFailureListener {
    
    }
    ```

### IMAP Credentials
The `accounts()` function is used to fetch the cached account's `Credentials.Password` on the sdk. This is usually called AFTER `verify()`, once a client can verify that there is a valid account on the sdk. This does not verify the account credentials. It only fetches them from our encrypted cache and returns them to the caller.

=== "Kotlin"
    ```kotlin
    client.accounts().addOnSuccessListener { accounts: List<Credentials.Password> ->
    
    }.addOnFailureListener {
    
    }
    ```

### IMAP Messages

After a user has been signed in to their IMAP Account, we can now fetch their emails and find any receipts they may have stored in their email. Before we initiate a search, we want to make sure we have properly configured the `ImapClient`. All sdk email clients have properties that can be configured to optimize searching. Here is a list of the following properties

| Property Name | Type                                    | Default Value | Client Function | Description                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|---------------|-----------------------------------------|---------------|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  dayCutoff    | Int                                     |     14        | dayCutoff(int days) | Maximum number of days look back in a users inbox for receipts                                                                                                                                                                                                                                                                                                                                                                                  |
| filterSensitive | Boolean                                 | false    | filterSensitive(boolean filterSensitive)| When set to true the sdk will not return product results for products deemed to be sensitive i.e. adult products                                                                                                                                                                                                                                                                                                                                |
| subProducts   | Boolean                                 | false      | subProducts(boolean subProducts)| Enable sdk to return subproducts found on receipts under parent products i.e. "Burrito + Guacamole <- Guac is subproduct"                                                                                                                                                                                                                                                                                                                       |
| countryCode   | String                                  |  "US"      | countryCode(String countryCode) | Helps classify products and apply internal product intelligence                                                                                                                                                                                                                                                                                                                                                                                 |
| sendersToSearch | Map\<String, Merchant\> | null | sendersToSearch(Map\<String, Merchant\> sendersToSearch) | This allows clients to search for merchants that may have sent receipts under a different email. For example, Target may have sent an email from "receipts@uniquetarget.com". It is still a Target receipt, but under a different email. Therefore, the client can provide a Merchant like `mapOf( "receipts@uniquetarget.com" to  Merchant( "Target.com", "receipts@uniquetarget.com"))`. |

Once the client is configured then we are ready to start parsing emails. On the `ImapClient` call `messages(@NonNull MessagesCallback callback)` to begin the message reading. The calling of this function completes a series of tasks internally, before potentially returning a list of `List<ScanResults>` via the `MessagesCallback` parameter. Upon a successful execution, the callback will emit a result of `List<ScanResults>` from the overriden onComplete() function. The number of `onComplete()` emissions depends on the number IMAP accounts you have credentials for. The `messages(...)` function will attempt to read messages based on the specified configuration set on the client for each account logged in. In addition to a List<ScanResults>, each emission of onComplete will give you the `Credentials.Password` of the corresponding account from which the scan results were derived from. Within the callback, there is an onException interface method. This will be triggered in the event of an error fetching messages from an account. The account e-mail retrieval process is segregated from each other. Therefore, failure to retrive messages from one account doesn't mean a failure to retrieve messages from all accounts. It is entirely possible, to receive an onException callback AND an onComplete callback within a single `messages(...)` call. If results were found then each item in the list will represent a successfully scanned receipt. Please use the ScanResults data to display information to users or use for internal use.

**EXAMPLE IMAP READ MESSAGES**

=== "Kotlin"
    ```kotlin
        fun messages() {
          client.messages(
              object: MessagesCallback {
                  override fun onComplete(credentials: Credentials.Password, result: List<ScanResults>) {
                      // do stuff with scan results
                      // see credentials of account
                  }
    
                  override fun onException(throwable: Throwable) {
                      Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_SHORT).show
                  }
              }
          )
        }
    ```

### IMAP Remote Messages

The `messages()` function is responsible for fetching emails and parsing those emails on the device. This is the normal behavior of the sdk. However, we now have `remoteMessages(@NonNull JobResultsCallback callback)`. This function is similar to messages, but instead of parsing the emails on the device, it will parse the emails on the server. The JobResultsCallback.onComplete(...) function will trigger upon a completed operation. Within the callback users will receive `credentials: Credentials.Password` and `result: JobResults`. The `Credentials.Password` is covered in other parts of the documentation. The `JobResults` parameter will give you a reference to the server job. In addition to the server job id, it will also let you know if the job was successful, or if there were any errors with your request.


### IMAP Logout
When you wish to sign out from a user's current account use the `logout()` function on the `ImapClient`. The logout function will sign a user out of their account and clear the credentials cached in the sdk. The return type is a `Task<Boolean>`. When a successful `true` result is given then it can be assumed that the client has successfully cleared all stored credentials and data for a user. If an exception is thrown then, there could have been an issue with one or more of the tasks executed to complete the logout functionality.

=== "Kotlin"
    ```kotlin
    val credentials: Credentials.Password = Credentials.Password.Gmail(
        "email@microblink.com",
        "account password"
    )

    client.logout(Credentials).addOnSuccessListener {
    
    }.addOnFailureListener {
    
    }
    ```


### IMAP Clear
In order to optimize, fetching and parsing emails, we try to not to duplicate work. One of these optimizations comes in the form of a cached last date search. This cached value allows us to keep track of the last time a search was done, so that we can optimize our search paramters and not fetch duplicate emails that we have already seen. However, there may be a scenario where the client will want to clear this cache and fetch all emails within the `dayCutoff` value. If you fall into this scenario, and wish to clear our cached date flag, then call `clearLastCheckedTime()`. Thi returns a `Task<Boolean>` and will let you know based on the boolean result whether or not we were able to successfully clear our cached date.

=== "Kotlin"
    ```kotlin
    client.clearLastCheckedTime().addOnSuccessListener {
    
    }
    ```

### IMAP Destroy Client
We always want to make sure we are adhering to any component's lifecycle. Therefore, it is very important to call destroy within the component. This will clean up any pending calls, and allocated resources.

=== "Kotlin"
    ```kotlin
       override fun onDestroy() {
           super.onDestroy()
    
           client.close()
       }
    ```

### Exception Handling

The sdk will throw exceptions in the event it runs into an error while fetching emails. Here are some helpful explanations of common exception messages you may run into while using the sdk.

| Function | Exception Message |    Explanation    |
|----------|-------------------|-------------------|
| messages() | "unable to find provider accounts" | While fetching the cached email accounts on the device, there were no accounts found in the cache. These could have been removed by the `logout()` function, or some other data clearing action. The only recourse is to readd the accounts.  |
|  logout()  | "unable to store this provider " + provider | While attempting to logout of a provided account, the specified account was not found in the cache |
|  verify()  | "unable to connect to imap service!" | There was an error in connecting to the IMAP client. It could be an issue with the server, or the credentials. You can try again later or have a user reinput their credentials. |
| remoteMessages() | "Unable to encrypt credentials "+ credentials.username() | Credentials are encrypted and stored for easy access. When provided we attempt to encrypt the provided credentials. In the event of a failure this exception will be thrown informing you of the error. |
