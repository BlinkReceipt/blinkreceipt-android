
# Blink Receipt Digital SDK

Blink Receipt Digital SDK for Android is an SDK that enables you to easily add e-receipt functionality to your app with the purpose of scanning receipts. Blink Receipt Digital SDK  supports parsing e-receipts from a growing list of retailers and a specific set of mail providers: Gmail, Outlook, Yahoo, and AOL. The procedure for integrating each of these providers is slightly different. This guide will outline the steps necessary to integrate and authenticate a user account for each mail provider, and then the common methods that you will use to invoke the e-receipt parsing functionality.

See below for more information about how to integrate Blink Receipt Digital SDK into your app.

<br />

## Tasks

The Blink Receipt Digital Sdk heavily leverages Google's [Task](https://developers.google.com/android/guides/tasks) for result and exception handling when interfacing with the sdk. Here is a high level comprehensive guide on how to use the Task framework.

The `Task<T>` object, returned by most functions in the sdk, can be thought of as a reference to a job being done either on the main thread or a background thread. The `T` represents the type of result that task is expected to return when done executing it's logic.

<br />

### Handling Results From A Task

When you have a Task, you are able to apply different sets of listeners that will receive your result or catch your exceptions. There are 2 ways to approach this result handling

<br />

#### First Way (Recommended)

A successful callback listener, `OnSuccessListener<? super TResult>`, can be added to the task by calling `task.addOnSuccessListener(OnSuccessListener<? super TResult>)`. This listener has a single method `onSuccess(T result)` that needs to be implemented. This listener callback will be invoked upon a successful completion of a given Task.

Java
```java
    Task<Foo> exampleTask = repository.fetchFoo();

    exampleTask.addOnSuccessListener( new OnSuccessListener<Foo>() {
        @Override
        public void onSuccess(Foo foo) {
            // Do something
        }
    } );
```

Kotlin
```kotlin
    val exampleTask: Task<Foo> = repository.fetchFoo();

    exampleTask.addOnSuccessListener { foo -> // Do something }
```

Unfortunately, as we know all too well, things do not always go as planned. Exceptions can occur and it is important that we handle those scenarios in the event we wish to provide some sort of recourse for the user. There is a compliment listener that can be added to a `Task` for exception handling. This listener is called a `OnFailureListener`, and it can be added by calling `task.addOnFailureListener(OnFailureListener listener)`. This also has one method, `onFailure(@NonNull Exception e)`, that needs to be implemented. This callback will be invoked in the event any exception is thrown by the task itself.

Java
```java
    Task<Foo> exampleTask = repository.fetchFoo();

    exampleTask.addOnFailureListener( new OnFailureListener() {
        @Override
        public void onFailure(Exception exception) {
            // Do something
        }
    } );
```

Kotlin
```kotlin
    val exampleTask: Task<Foo> = repository.fetchFoo();

    exampleTask.addOnFailureListener { exception -> // Do something }
```

Result listeners can be chained for a cleaner look.

```java
     Task<Foo> exampleTask = repository.fetchFoo();

    exampleTask.addOnSuccessListener( new OnSuccessListener<Foo>() {
        @Override
        public void onSuccess(Foo foo) {
            // Do something
        }
    } ).addOnFailureListener( new OnFailureListener() {
        @Override
        public void onFailure(Exception exception) {
            // Do something
        }
    } );
```

<br />

#### Second Way (Not Recommended)

We believe it is better to separate out the two outcomes of a task, but not all share the same philosophy. This method of result and exception handling combines both eventualities into a single callback, `OnCompleteListener<TResult> listener`. This has a single abstract method which will be implemented `onComplete (Task<TResult> task)`. The invoking of this callback does not signify a successful or a failed task result. Extra logic must be added to make that determination. The `OnCompleteListener` gives you a reference to the task. It is from here that you can do status checks `task.isSuccessful()`, `task.isCompleted()`, or `task.isCanceled()`. The result of the task can be retrieved by calling `task.getResult()`. Alternatively, the thrown exception can be retrieved with a simple `task.getException()` call.

**WARNING** Calling `task.getResult()` while the task is still executing or has already failed will result in a `IllegalStateException` and a `RuntimeException` respectively.

<br />
<br />

## AAR
The package contains Android Archive (AAR) that contains everything you need to use Blink Receipt Digital library.

<br />

## Optional Section: Adding An AAR to your project
As Android developers, we have become very reliant on the gradle build system to pull in any dependencies with a few lines changes to our `build.gradle` file. Unfortunately, sometimes we need to add aar files manually to our project. This is done by adding the dependencies as modules within our project. The Blink Receipt Digital Sdk is one of these scenarios. Luckily, adding an AAR via Android Studio has become very straight over the years.

1) Download/Checkout the aar file from the git repo.
2) Open up your development project. Then select `File` -> `New` -> `New Module`
3) This should open up a modal displaying a list of `Module Types` to choose from. There are a variety of types to choose from, but for our purposes we can select `Import .JAR/.AAR Package`
4) The next page should ask you to input two fields
   1) FileName: This is the path to the aar file
   2) Subproject Name: This is the customizable name of the module as it will appear in your project. You can leave it as the default name auto-populated by the selected aar, or you can rename it to whatever is appropriate i.e. `blink-receipt-digital-sdk`
5) Once your configuration is set you can finish and exit the wizard. This action will create a new module and add the selected aar file to that module. It should also modify your `settings.gradle` file which should now include a new line `include ':{newly-named-module}'`. This indicates that the build system will now compile this module when you build your project. However, this does not mean that the sdk has been added to YOUR app module.
6) To add the sdk to your app module you must do the following:
   1) Navigate to your the `build.gradle` file of your app module
   2) add the following line to your `dependencies` block `implementation project( ':{newly-named-module}' )`

Once the project has been synced the AAR should now be accessible via the code in your app module :)

<br />

## Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

***Note: When declaring dependencies for the digital sdk, you must add the :blinkreceipt-recognizer and :blinkreceipt-camera aars to your project. Even if you do not plan on explicitly using the functionality within those sdks, without these aars the license verification process will fail and the digital sdk will fail to work.***

Please follow the [Project Integration and Initialization](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-project-integration-and-initialization), [R8/Proguard](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#r8--proguard), and the application class/manifest step in the [Scanning Your First Receipt](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-scanning-your-first-receipt) sections to properly add and initialize recognizer sdk.

```groovy
dependencies {
     api project( ':blinkreceipt-core' )

     api project( ':blinkreceipt-recognizer' )
     api project( ':blinkreceipt-camera' )

    implementation 'com.squareup.okhttp3:okhttp:4.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'

    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.9.0'

    implementation "androidx.constraintlayout:constraintlayout:1.1.3"

    implementation "androidx.core:core:1.3.1"

    implementation 'com.squareup.okio:okio:2.8.0'

    implementation 'com.jakewharton.timber:timber:4.7.1'

    implementation "com.google.android.gms:play-services-tasks:17.2.0"

    implementation "com.google.apis:google-api-services-gmail:v1-rev105-1.25.0" exclude module: 'httpclient'
    implementation "com.google.api-client:google-api-client-android:1.30.10" exclude module: 'httpclient'
    implementation "com.google.http-client:google-http-client-gson:1.36.0" exclude module: 'httpclient'

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
<br />
<br />


## IMAP

IMAP integration requires 9 additional AAR in your build gradle.

```groovy
dependencies {

    implementation "androidx.lifecycle:lifecycle-viewmodel:2.2.0"
    implementation "androidx.lifecycle:lifecycle-livedata:2.2.0"
    implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0"
    implementation "androidx.webkit:webkit:1.4.0"

    implementation "androidx.security:security-crypto:1.1.0-alpha03"
    implementation "com.sun.mail:android-mail:1.6.5"
    implementation "com.sun.mail:android-activation:1.6.5"

    implementation "androidx.fragment:fragment:1.2.5"

    implementation "com.google.android.material:material:1.2.0"

}
```
<br />

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
<br />

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

<br />
<br />

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

<br />
<br />

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
<br />
<br />

### IMAP Login To/Verify Account
The `verify()` function is used to determine if the sdk has any cached credentials that can be used without explicit sign in. This can be called without any parameters or with an `Executor` and `PasswordCredentials`. The empty parameter function call will automatically attempt to fetch the cached credentials within the sdk and verify the credentials against the ImapService. This function call returns a `Task<Boolean>`. When the result emitted is a `true` value, then the credentials that were either passed in or cached in the sdk grant access to a valid account. In the event an exception is thrown, that means that the credentials, either passed in or cached, are not valid credentials to access a specific account.

```kotlin
client.verify().addOnSuccessListener { isVerified ->
   if (isVerified) {
       // We have a valid account, fetch user messages
   } else {
       // We do not have valid credentials, display error or show login flow
   }
}.addOnFailureListener {
   //Something went wrong, but we do not have valid credentials. Display error or show login flow.
}
```
<br />

**NOTE**
If a user has never signed into their IMAP account. Then it is safe to assume that when you call verify the result will be false or throw an exception. If this ends up being the case then you should capture the credentials from your user and recall verify with a passed in `PasswordCredentials` object. IMAP client requires the caller to collect the user's imap credentials that correspond to the selected provider. Credentials will be securely stored using JetPack Security.

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
<br />
<br />

### IMAP Credentials
The `credentials()` function is used to fetch the cached account's `PasswordCredentials` on the sdk. This is usually called AFTER `verify()`, once a client can verify that there is a valid account on the sdk. This does not verify the account credentials. It only fetches them from our encrypted cache and returns them to the caller.

```kotlin
client.credentials().addOnSuccessListener { credentials ->
   textview.text = "Welcome ${credentials.username()}"
}.addOnFailureListener {
   Toast.makeText(context, "Unable to fetch account", Toast.LENGTH_SHORT).show()
}
```

<br />
<br />

### IMAP Messages

After a user has been signed in to their IMAP Account, we can now fetch their emails and find any receipts they may have stored in their email. Before we initiate a search, we want to make sure we have properly configured the `ImapClient`. All sdk email clients have properties that can be configured to optimize searching. Here is a list of the following properties

| Property Name | Type | Default Value | Client Function |                           Description                           |
|---------------|------|---------------|-----------------|-----------------------------------------------------------------|
|  dayCutoff    | Int  |     14        | dayCutoff(int days) | Maximum number of days look back in a users inbox for receipts             |
| filterSensitive | Boolean | false    | filterSensitive(boolean filterSensitive)| When set to true the sdk will not return product results for products deemed to be sensitive i.e. adult products |
| subProducts   | Boolean | false      | subProducts(boolean subProducts)| Enable sdk to return subproducts found on receipts under parent products i.e. "Burrito + Guacamole <- Guac is subproduct" |
| countryCode   | String  |  "US"      | countryCode(String countryCode) | Helps classify products and apply internal product intelligence  |

Once the client is configured then we are ready to start parsing emails. On the `ImapClient` call `messages(@NonNull Activity activity)` to begin the message reading. This call returns a `Task<List<ScanResults>>`. The calling of this function completes a series of tasks internally, before potentially returning a `List<ScanResults>`. Upon a successful execution, the task will emit a result of `List<ScanResults>`. If no results were able to be found, then the list will be empty. If results were found then each item in the list will represent a successfully scanned receipt. Please use the ScanResults data to display information to users or use for internal use.


<br />
**EXAMPLE IMAP READ MESSAGES**

```java
    public void readUserMessages() {
        client.messages(requireActivity()).addOnSuccessListener( new OnSuccessListener<List<ScanResults>>() {
             @Override
             public void onSuccess(List<ScanResults> account) {
                // Successfully fetched messages, do something
                binding.resultsToDisplay.text = "Scanned Results (${results.size})"
            }
        }).addOnFailureListener( new OnFailureListener() {
             @Override
             public void onFailure(Exception exception) {
               //User authentication has failed
             }
        });
    }

```

```kotlin

    fun readUserMessages() {
      client.messages(requireActivity())
                .addOnSuccessListener { results ->
                    // Successfully fetched messages, do something
                    binding.resultsToDisplay.text = "Scanned Results (${results.size})"
                }.addOnFailureListener {
                    // Error fetching/parsing messages
                    Toast.makeText(requireContext(), it.message,Toast.LENGTH_SHORT).show()
                }
    }

```
<br />
<br />

### IMAP Logout
When you wish to sign out from a user's current account use the `logout()` function on the `ImapClient`. This takes in an optional `Boolean clearCache` parameter. The logout function will sign a user out of their account and clear the credentials cached in the sdk. By opting to set the clearCache flag, the logout function will also clear all Cookies stored. The return type is a `Task<Boolean>`. When a successful `true` result is given then it can be assumed that the client has successfully cleared all stored credentials and data for a user. If an exception is thrown then, there could have been an issue with one or more of the tasks executed to complete the logout functionality.

<br />

```kotlin
 client.logout().addOnSuccessListener { successfullyLoggedOut ->
    // check boolean result to see if a user has successfully logged out and show appropriate UI
}.addOnFailureListener {
    // Something has gone wrong the user may or may not be signed out, please check exception.
}
```
<br />
<br />

### IMAP Clear
In order to optimize, fetching and parsing emails, we try to not to duplicate work. One of these optimizations comes in the form of a cached last date search. This cached value allows us to keep track of the last time a search was done, so that we can optimize our search paramters and not fetch duplicate emails that we have already seen. However, there may be a scenario where the client will want to clear this cache and fetch all emails within the `dayCutoff` value. If you fall into this scenario, and wish to clear our cached date flag, then call `clearLastCheckedTime()`. Thi returns a `Task<Boolean>` and will let you know based on the boolean result whether or not we were able to successfully clear our cached date.
<br />

```kotlin
client.clearLastCheckedTime().addOnSuccessListener {

}
```
<br />
<br />

### IMAP Destroy Client
We always want to make sure we are adhereing to any component's lifecycle. Therefore, it is very important to call destroy within the component. This will clean up any pending calls, and allocated resources.
<br />

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

<br />
<br />
<br />
<br />

## **Gmail**

Blink Receipt Digital sdk allows for full Gmail Integration. The following dependencies must be added in order to support Gmail integration.

```groovy
    dependencies {
        implementation "com.google.android.gms:play-services-auth:${latest-version}"
        implementation "com.google.android.gms:play-services-tasks:${latest-version}"

        implementation "com.google.apis:google-api-services-gmail:${latest-version}" exclude module: 'httpclient'

        implementation "com.google.api-client:google-api-client-android:${latest-version}" exclude module: 'httpclient'
    }
```

<br />
<br />

### **Gmail Client**

The `GmailClient` is the corner stone of the gmail sdk integration. It is the access point for reading and parsing emails from clients. It leverages Google's task framework to allow for seamless and clear multi-threadding functionality.

To instantiate the `GmailClient` you must provide the constructor 3 non-null and non-zero arguments.

1. Context: `Context`. When using the client within an Android `Activity` you can pass in `this` for the argument value. If using the client within an Android `Fragment` you can pass in `requireActivity()`.

2. Thread Count: `int` which determines the number of threads to use for processing `e-receipt` emails. The internal default value we use is `4`.

3. Client Id: `String` which comes from  Google's api services. Please refer [here](https://developers.google.com/identity/protocols/oauth2/native-app) for more information on how to create an Android client id.

<br />

#### Code sample for Gmail Client Instantiation: KOTLIN
```kotlin
    // Activity Example
    class MyGmailActivity: Activity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            val gmailClient = GmailClient(this, 4, "android-oauth-client-id-from-google")
        }

    }

    // Fragment Example
    class MyGmailFragment: Fragment() {

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val gmailClient = GmailClient(requireActivity(), 4, "android-oauth-client-id-from-google")
        }

    }
```



#### Code sample for Gmail Client Instantiation: JAVA
```java
    //Activity Example
    public class MyGmailActivity extends Activity() {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            GmailClient gmailClient = new GmailClient(this, 4, "android-oauth-client-id-from-google");

        }

    }

    // Fragment Example
    public class MyGmailFragment extends Fragment() {

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {

            GmailClient gmailClient = new GmailClient(this, 4, "android-oauth-client-id-from-google");

        }

    }
```

<br />
<br />

### **Logging In To Gmail**

Users may log in to gmail via the client's `login()` function. There is an overloaded login function which takes in an Android `Activity`, `login(Activity activity)`. Passing the `Activity` allows for our components to be lifecycle aware and not leak memory. This parameter is optional though and not required for any explicit extra functionality.

The login call returns a Google `Task` of type `GoogleSignInAccount`. The `GoogleSignInAccount` is an object which contains the basic account information of the signed in Google user. The reference for GoogleSignInAccount can be found [here](https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInAccount).

`login()` can be called from any thread, because the return type is a `Task<GoogleSignInAccount>`. If you read the previous section of this document on the [Task](#tasks) framework you should have some familiarity with how this process works.

Let's step through the different login scenario results that can occur and how to provide proper handling of each scenario.


<br />

#### **Successful Login Scenario**

A successful login attemt will return a valid `GoogleSignInAccount`. This sign in account can be captured via an `OnSuccessListener` call.


Here is an example of some happy case scenarios when calling the login() function.

```java
    public void loginUser() {
        Task<GoogleSignInAccount> task = gmailClient.login();

        task.addOnSuccessListener(
            new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount account) {
                    // Enable a UI button for reading message now that we have a successful login
                    readMessagesBtn.setEnabled(true);
                }
            }
        );
    }
```

```kotlin
    fun loginUser() {
        val task: Task<GoogleSignInAccount> = gmailClient.login()

        task.addOnSuccessListener {
             // Enable a UI button for reading message now that we have a successful login
             readMessagesBtn.enabled = true
        }
    }
```

<br />
<br />

#### **Unsuccessful Login Scenario**

A login attempt can fail for a number of reasons. Google has integrated its sign in flow into the Android system. For safety reasons, calling login does not always automatically sign the default user in to your application. It is possible that Google may require extra authentication when you attempt to signIn. The Gmail Integration of the sdk provides an easy to use wrapper around Google's authentication handling. When calling login it is possible for exceptions to be thrown. We try to make exception handling as easy as possible for you. Therefore, we have created our own easy to use exceptions that will allow your app proper recourse in the event of a failure.


GmailAuthException

One of the most important exceptions to look out for is the `GmailAuthException`. This exception occurs in the event of any silent authentication exceptions. This exception is extremely important, because it could potentially contain a recourse for a user to take upon an unsuccessful sign in. The exception potentially contains an Android `Intent` this is an intent that has been provided by Google and meant to be launched by the app developer to obtain an explicit approval from the app user. The intent must be triggered with a `startActivityForResults()` call. This call is a method within Android components (Activity, Fragment). It takes in an `Intent` as well as an `int` which denotes the identifying `requestCode`. Starting this activity for result will display an overlaying window that will display the user's registered accounts on the device.

Once a user has selected their desired account the overlaying screen will automatically dismiss and the `onActivityResult(int requestCode, int resultCode, Intent data)` callback overridden within your Android component (Activity or Fragment) will be invoked. There is no need to handle the result and data yourself, the gmail client provides an easy to use function to handle this. Simply call `gmailClient.onAccountAuthorizationActivityResult()`, passing in the `requestCode`, `resultCode`, and `data` respectively.

The `onAccountAuthorizationActivityResult` function also returns a task of type `GoogleSignInAccount`. If the sign is successful then the Intent data passed in usually contains the desired `GoogleSignInAccount` which the client parses and returns to you in the form of a result. If the sign in is NOT successful the returned task will throw an exception notifying your app of the failed result.

**NOTE** Most common cause for an exception in this scenario is the user, when presented with the Google Sign In Screen Overlay, opted not to choose any account and clicked the cancel option in the modal.

**NOTE** THIS `GmailAuthException` SCENARIO WILL MOST LIKELY BE THE USER EXPERIENCE FLOW THE FIRST TIME A USER SIGNS IN TO YOUR APP

<br />

**EXAMPLE GMAIL LOGIN IMPLEMENTATION**

```java
    public class MyGmailActivity extends Activity {

        private GmailClient client;

        //...Instantiate GmailClient in one of the lifecycle methods

        // Attempt to login
        public void login() {
            Task<GoogleSignInAccount> task = client.login();

            task.addOnSuccessListener( new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount account) {
                    //User has successfully signed in do something
                    textView.setText("Welcome " + account.getEmail() )
                }
            })
            .addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    //User authentication has failed

                  if (e instanceOf GmailAuthException) {
                        //Exception is a Gmail Auth Exception with recourse for the user

                        GmailAuthException authException = (GmailAuthException) e;

                        // authException contains specific intent to launch as well as a bundled request code used by client later in the onActivityResult callback
                        startActivityForResult(authException.signInIntent, authException.requestCode)
                    } else {
                        // User auth failed, no recourse show user message
                    }
                }
              } );
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data)() {
            Task<GoogleSignInAccount> authResultTask = gmailClient.onAccountAuthorizationActivityResult(requestCode, resultCode, data)

            authResultTask.addOnSuccessListener( new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount account) {
                    //User has successfully signed in after recourse do something
                    textView.setText("Welcome " + account.getEmail() )
                }
            })
            .addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // User auth failed, no recourse show user message
                }
              } );
        }
    }
```

```kotlin
class GmailInboxFragment : Fragment() {

    private lateinit var gmailClient: GmailClient

    //...Instantiate GmailClient in one of the lifecycle methods

    // Attempt to login
    fun login() {
        gmailClient.login()
                .addOnSuccessListener { signInAccount ->
                    val email = signInAccount.email

                    textView.text = "Welcome $email"
                }.addOnFailureListener { e ->
                    if (e is GmailAuthException) {
                        startActivityForResult(e.signInIntent, e.requestCode)
                    } else {
                        //Set error display
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        gmailClient.onAccountAuthorizationActivityResult(requestCode, resultCode, data).addOnSuccessListener { signInAccount ->
            val email = signInAccount.email

            textView.text = "Welcome $email"
        }.addOnFailureListener {
            //Set error display
        }
    }
}
```

Once a user has successfully signed in we are good to go! We can now move on to retrieving emails from a signed in user.

<br />
<br />

### **Verifying Gmail Log In And Retrieving Already Signed In Users**

So as not to constantly bombard users with a typical sign in flow, we provide the `verify()` function on the `GmailClient`. The `verify()` call returns a `Task<Boolean>`. This boolean result returnes either `true` or `false`. If the result is `false` then this indicates that the sdk has no record of a signed in user. As a result you must take the user through the original sign in flow mentioned in the previous section.

In the event, the result returns a `true` value, this indicates that we have an account signed in with Google. In which case, you can call `credentials()` on the `GmailClient`. The `credentials()` call returns a `Task<GoogleSignInAccount>`. This will silently fetch the signed in user's account and return it to the caller via the `Task`.

<br />

**EXAMPLE GMAIL VERIFY AND CREDENTIALS IMPLEMENTATION**

```java
    private void verifyUser() {
        gmailClient.verify().addOnSuccessListener( new OnSuccessListener<Boolean>() {
             @Override
             public void onSuccess(Boolean account) {
                //User has signed in before and there is a cached account if true, no user signed in if false
                Toast.makeText(requireContext(),"ACCOUNT VERIFIED $it",Toast.LENGTH_SHORT).show()
            }
        })..addOnFailureListener( new OnFailureListener() {
             @Override
             public void onFailure(Exception e) {
                //Exception has occurred while verifying, logout and try calling the login function
                Toast.makeText( requireContext(), "ACCOUNT EXCEPTION $it",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private void credentials() {
        gmailClient.credentials().addOnSuccessListener( new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount account) {
                    //User has successfully signed in do something
                    textView.setText("Welcome " + account.getEmail() )
                }
            })
            .addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    //User authentication has failed

                  if (e instanceOf GmailAuthException) {
                        //Exception is a Gmail Auth Exception with recourse for the user

                        GmailAuthException authException = (GmailAuthException) e;

                        // authException contains specific intent to launch as well as a bundled request code used by client later in the onActivityResult callback
                        startActivityForResult(authException.signInIntent, authException.requestCode)
                    } else {
                        // User auth failed, no recourse show user message
                    }
                }
              } );
    }

```

```kotlin
     private fun verifyUser() {
        gmailClient.verify()
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"ACCOUNT VERIFIED $it",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText( requireContext(), "ACCOUNT EXCEPTION $it",Toast.LENGTH_SHORT).show()
            }
    }

    private fun userCredentials() {
        gmailClient.userCredentials().addOnSuccessListener { signInAccount ->
                    val email = signInAccount.email

                    textView.text = "Welcome $email"
                }.addOnFailureListener { e ->
                    if (e is GmailAuthException) {
                        startActivityForResult(e.signInIntent, e.requestCode)
                    } else {
                        //Set error display
                    }
                }
    }
```

<br />
<br />

### **Logging Out of Gmail**

Users may log out of gmail via the client's `logout()` function. The logout function will return a `Task<Boolen>`. This function completes 2 objectives. First, it signs the currently signed in user out from Gmail/Google SDK. This means that the user can no longer be "silently" signed in to Google. The next time the `verify()` is called a `false` result should be returned via the `Task` return object. This also means that the `credentials()` call will not return a `GoogleSignInAccount` result, but instead throw an exception (not unlike the initial user flow for sign in we covered before). Secondly, the `logout()` call will clear any cached "date" threshold we use for email searching. You should never receive a `Boolean` result of `false` for the signout task. It will always either be true or throw an exception in the unlikely event of an error.

<br />

**EXAMPLE GMAIL LOGOUT IMPLEMENTATION**

```java
    private void logoutUser() {
        gmailClient.logout().addOnSuccessListener( new OnSuccessListener<Boolean>() {
             @Override
             public void onSuccess(Boolean account) {
                //User has successfully signed out
            Toast.makeText(context, "onSignedOut()", Toast.LENGTH_SHORT).show()
            }
        })
    }
```

```kotlin
    private fun logoutUser() {
        gmailClient.logout().addOnSuccessListener {
            //User has successfully signed out
            Toast.makeText(this@GmailFragment.context, "onSignedOut()", Toast.LENGTH_SHORT).show()
        }
    }
```
<br />
<br />

### **Reading Messages And Getting Results**

After a user has been signed in to their Gmail Account, we can now fetch their emails and find any receipts they may have stored in their email. Before we initiate a search, we want to make sure we have properly configured the `GmailClient`. All sdk email clients have properties that can be configured to optimize searching. Here is a list of the following properties

| Property Name | Type | Default Value | Client Function |                           Description                           |
|---------------|------|---------------|-----------------|-----------------------------------------------------------------|
|  dayCutoff    | Int  |     14        | dayCutoff(int days) | Maximum number of days look back in a users inbox for receipts             |
| filterSensitive | Boolean | false    | filterSensitive(boolean filterSensitive)| When set to true the sdk will not return product results for products deemed to be sensitive i.e. adult products |
| subProducts   | Boolean | false      | subProducts(boolean subProducts)| Enable sdk to return subproducts found on receipts under parent products i.e. "Burrito + Guacamole <- Guac is subproduct"  |
| countryCode   | String  |  "US"      | countryCode(String countryCode) | Helps classify products and apply internal product intelligence  |

Once the client is configured then we are ready to start parsing emails. On the `GmailClient` call `messages(@NonNull Activity activity)` to begin the message reading. This call returns a `Task<List<ScanResults>>`. The calling of this function completes a series of tasks internally, before potentially returning a `List<ScanResults>`. Upon a successful execution, the task will emit a result of `List<ScanResults>`. If no results were able to be found, then the list will be empty. If results were found then each item in the list will represent a successfully scanned receipt. Please use the ScanResults data to display information to users or use for internal use.

<br />

**EXAMPLE GMAIL READ MESSAGES**
```java
    public void readUserMessages() {
        client.messages(requireActivity()).addOnSuccessListener( new OnSuccessListener<List<ScanResults>>() {
             @Override
             public void onSuccess(List<ScanResults> account) {
                // Successfully fetched messages, do something
                binding.resultsToDisplay.text = "Scanned Results (${results.size})"
            }
        }).addOnFailureListener( new OnFailureListener() {
             @Override
             public void onFailure(Exception exception) {
               //User authentication has failed
             }
        });
    }

```

```kotlin

    fun readUserMessages() {
      client.messages(requireActivity())
                .addOnSuccessListener { results ->
                    // Successfully fetched messages, do something
                    binding.resultsToDisplay.text = "Scanned Results (${results.size})"
                }.addOnFailureListener {
                    // Error fetching/parsing messages
                    Toast.makeText(requireContext(), it.message,Toast.LENGTH_SHORT).show()
                }
    }

```

<br />
<br />

### GmailClient Destroy Client
We always want to make sure we are adhereing to any component's lifecycle. Therefore, it is very important to call destroy within the component. This will clean up any pending calls, and allocated resources.

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
