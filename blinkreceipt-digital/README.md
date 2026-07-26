# Module blinkreceipt-digital

# Package com.microblink.digital

# Blink Receipt Digital SDK

Blink Receipt Digital SDK for Android is an SDK that enables you to easily add e-receipt functionality to your app with the purpose of scanning receipts. Blink Receipt Digital SDK  supports parsing e-receipts from a growing list of retailers and a specific set of mail providers: Gmail, Outlook, Yahoo, and AOL. The procedure for integrating each of these providers is slightly different. This guide will outline the steps necessary to integrate and authenticate a user account for each mail provider, and then the common methods that you will use to invoke the e-receipt parsing functionality.

See below for more information about how to integrate Blink Receipt Digital SDK into your app.

# Table of Contents

* [Tasks](#tasks)
  * [Handling Results From A Task](#results)
    * [Recommended Way](#recommended_way)
    * [Unrecommended Way](#unrecommended_way)
* [Project Integration and Initialization](#project_integration)
* [IMAP](#imap_client)
* [Automated Scrape](#auto_scrape)
  * [Quick Start](#auto_scrape_quick_start)
  * [Arming and Re-Arming](#auto_scrape_arming)
  * [Configuration](#auto_scrape_configuration)
  * [Callbacks and Threading](#auto_scrape_callbacks)
  * [How The Schedule Behaves](#auto_scrape_schedule)
  * [Stopping Auto-Scrape](#auto_scrape_stopping)
  * [Best Practices](#auto_scrape_best_practices)
  * [Testing and QA](#auto_scrape_testing)
  * [Troubleshooting](#auto_scrape_troubleshooting)
* [Outlook](#outlook)
* [Gmail](#gmail)


<br />

# Tasks <a name=tasks></a>

The Blink Receipt Digital Sdk heavily leverages Google's [Task](https://developers.google.com/android/guides/tasks) for result and exception handling when interfacing with the sdk. Here is a high level comprehensive guide on how to use the Task framework.

The `Task<T>` object, returned by most functions in the sdk, can be thought of as a reference to a job being done either on the main thread or a background thread. The `T` represents the type of result that task is expected to return when done executing it's logic.

<br />

## Handling Results From A Task <a name=results></a>

When you have a Task, you are able to apply different sets of listeners that will receive your result or catch your exceptions. There are 2 ways to approach this result handling

<br />

### First Way (Recommended) <a name=recommended_way></a>

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

### Second Way (Not Recommended) <a name=unrecommended_way></a>

We believe it is better to separate out the two outcomes of a task, but not all share the same philosophy. This method of result and exception handling combines both eventualities into a single callback, `OnCompleteListener<TResult> listener`. This has a single abstract method which will be implemented `onComplete (Task<TResult> task)`. The invoking of this callback does not signify a successful or a failed task result. Extra logic must be added to make that determination. The `OnCompleteListener` gives you a reference to the task. It is from here that you can do status checks `task.isSuccessful()`, `task.isCompleted()`, or `task.isCanceled()`. The result of the task can be retrieved by calling `task.getResult()`. Alternatively, the thrown exception can be retrieved with a simple `task.getException()` call.

**WARNING** Calling `task.getResult()` while the task is still executing or has already failed will result in a `IllegalStateException` and a `RuntimeException` respectively.

<br />
<br />

## <a name=projectIntegration></a>Project Integration and Initialization
Please follow the [Project Integration and Initialization](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-project-integration-and-initialization), [R8/Proguard](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#r8--proguard), and the application class/manifest step in the [Scanning Your First Receipt](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-scanning-your-first-receipt) sections to properly add and initialize recognizer sdk.

To add the sdk to your android project please follow these steps:

1. Add the following maven repository to your build.gradle or settings.gradle, depending on your implementation:

    ```groovy
    repositories {
      maven { url  "https://maven.microblink.com" }
    }
    ```

2. Add the following to your dependency section in your app `build.gradle`.

```groovy
dependencies {
    implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.3"))

    implementation("com.microblink.blinkreceipt:blinkreceipt-digital")
    implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
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

## <a name=imap_client></a> IMAP
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
ImapClient( applicationContext, object : InitializeCallback {

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

**Note: The `ProviderSetupDialogFragment` only supports the following providers Yahoo, AOL, and Gmail. Attempting to set an unsupported IMAP provider will result in an IllegalStateException.**

```kotlin
     ProviderSetupDialogFragment.newInstance(
      ProviderSetupOptions.newBuilder(
         PasswordCredentials.newBuilder(
            Provider.GMAIL,
            "email@blink.com",
            "account password"
         ).build()
      ).build()
   ).callback {

   }.show(supportFragmentManager, TAG)
```
<br />
<br />

### IMAP Login To/Verify Account
The `verify()` function is used to determine if the sdk has any cached credentials that can be used without explicit sign in. This can be called without any parameters or with an `Executor` and `PasswordCredentials`. The empty parameter function call will automatically attempt to fetch the cached credentials within the sdk and verify the credentials against the ImapService. This function call returns a `Task<Boolean>`. When the result emitted is a `true` value, then the credentials that were either passed in or cached in the sdk grant access to a valid account. In the event an exception is thrown, that means that the credentials, either passed in or cached, are not valid credentials to access a specific account.

```kotlin
client.verify(PasswordCredentials.newBuilder(
   Provider.GMAIL,
   "test@gmail.com",
   "app password"
).build()).addOnSuccessListener { isVerified ->

}.addOnFailureListener {

}
```
<br />

### IMAP Credentials
The `credentials()` function is used to fetch the cached account's `PasswordCredentials` on the sdk. This is usually called AFTER `verify()`, once a client can verify that there is a valid account on the sdk. This does not verify the account credentials. It only fetches them from our encrypted cache and returns them to the caller.

```kotlin
client.accounts().addOnSuccessListener { accounts ->

}.addOnFailureListener {

}
```

<br />
<br />

### IMAP Messages

After a user has been signed in to their IMAP Account, we can now fetch their emails and find any receipts they may have stored in their email. Before we initiate a search, we want to make sure we have properly configured the `ImapClient`. All sdk email clients have properties that can be configured to optimize searching. Here is a list of the following properties

| Property Name | Type                                    | Default Value | Client Function | Description                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|---------------|-----------------------------------------|---------------|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  dayCutoff    | Int                                     |     14        | dayCutoff(int days) | Maximum number of days look back in a users inbox for receipts                                                                                                                                                                                                                                                                                                                                                                                  |
| filterSensitive | Boolean                                 | false    | filterSensitive(boolean filterSensitive)| When set to true the sdk will not return product results for products deemed to be sensitive i.e. adult products                                                                                                                                                                                                                                                                                                                                |
| subProducts   | Boolean                                 | false      | subProducts(boolean subProducts)| Enable sdk to return subproducts found on receipts under parent products i.e. "Burrito + Guacamole <- Guac is subproduct"                                                                                                                                                                                                                                                                                                                       |
| countryCode   | String                                  |  "US"      | countryCode(String countryCode) | Helps classify products and apply internal product intelligence                                                                                                                                                                                                                                                                                                                                                                                 |
| sendersToSearch | Map\<String, Merchant\> | null | sendersToSearch(Map\<String, Merchant\> sendersToSearch) | This allows clients to search for merchants that may have sent receipts under a different email. For example, Target may have sent an email from "receipts@uniquetarget.com". It is still a Target receipt, but under a different email. Therefore, the client can provide a Merchant like `mapOf( "receipts@uniquetarget.com" to  Merchant( "Target.com", "receipts@uniquetarget.com"))`. |

Once the client is configured then we are ready to start parsing emails. On the `ImapClient` call `messages(@NonNull MessagesCallback callback)` to begin the message reading. The calling of this function completes a series of tasks internally, before potentially returning a list of `List<ScanResults>` via the `MessagesCallback` parameter. Upon a successful execution, the callback will emit a result of `List<ScanResults>` from the overriden onComplete() function. The number of `onComplete()` emissions depends on the number IMAP accounts you have credentials for. The `messages(...)` function will attempt to read messages based on the specified configuration set on the client for each account logged in. In addition to a List<ScanResults>, each emission of onComplete will give you the `PasswordCredential` of the corresponding account from which the scan results were derived from. Within the callback, there is an onException interface method. This will be triggered in the event of an error fetching messages from an account. The account e-mail retrieval process is segregated from each other. Therefore, failure to retrive messages from one account doesn't mean a failure to retrieve messages from all accounts. It is entirely possible, to receive an onException callback AND an onComplete callback within a single `messages(...)` call. If results were found then each item in the list will represent a successfully scanned receipt. Please use the ScanResults data to display information to users or use for internal use.


<br />
**EXAMPLE IMAP READ MESSAGES**

```kotlin
    fun messages() {
      client.messages(
          object: MessagesCallback {
              override fun onComplete(credential: PasswordCredentials,result: List<ScanResults>) {
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
<br />
<br />

### IMAP Remote Messages

The `messages()` function is responsible for fetching emails and parsing those emails on the device. This is the normal behavior of the sdk. However, we now have `remoteMessages(@NonNull JobResultsCallback callback)`. This function is similar to messages, but instead of parsing the emails on the device, it will parse the emails on the server. The JobResultsCallback.onComplete(...) function will trigger upon a completed operation. Within the callback users will receive `credential: PasswordCredentials` and `result: JobResults`. The password credentials is covered in other parts of the documentation. The `JobResults` parameter will give you a reference to the server job. In addition to the server job id, it will also let you know if the job was successful, or if there were any errors with your request.

To run this same remote scrape on a background schedule — without the user reopening your app — see [Automated Scrape](#auto_scrape).


### IMAP Logout
When you wish to sign out from a user's current account use the `logout()` function on the `ImapClient`. This takes in an optional `Boolean clearCache` parameter. The logout function will sign a user out of their account and clear the credentials cached in the sdk. By opting to set the clearCache flag, the logout function will also clear all Cookies stored. The return type is a `Task<Boolean>`. When a successful `true` result is given then it can be assumed that the client has successfully cleared all stored credentials and data for a user. If an exception is thrown then, there could have been an issue with one or more of the tasks executed to complete the logout functionality.

<br />

```kotlin
 client.logout(PasswordCredentials.newBuilder(
   Provider.GMAIL,
   "test@gmail.com",
   "app password"
).build()).addOnSuccessListener {

}.addOnFailureListener {

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

<br />
<br />

## <a name=auto_scrape></a> Automated Scrape

Auto-scrape keeps a linked inbox producing receipts without the user ever reopening your app. Once armed, the SDK schedules a periodic background job; each run walks the user's linked in-scope accounts and creates the same server-side scrape job that a manual `ImapClient.remoteMessages()` call creates. The backend reads the inbox, parses the receipts, and POSTs them to your configured client endpoint.

**What comes back to your app is a job handle, not receipts.** Each account's run delivers a `JobResults` — job id, success flag, `JobStatusCode`, and message — to the callback you register. Receipt data itself lands on your endpoint server side, exactly as it does for a manual remote scrape. If you need `ScanResults` on the device, keep using `ImapClient.messages()`; auto-scrape is not a replacement for it.

Nothing is scheduled until you opt in by calling `begin()`, and nothing is scheduled while the user has no in-scope account linked.

### At A Glance

| | |
|---|---|
| Entry point | `AutoScrapeClient` (`com.microblink.digital`) |
| Provider scope | Gmail IMAP accounts with a non-empty username — see `AutoScrapeClient.isAutoScrapeAccount(credentials)` |
| Default cadence | 24 hours (`AutoScrapeClient.DEFAULT_INTERVAL_HOURS`) |
| Minimum cadence | 12 hours (`AutoScrapeClient.MIN_INTERVAL_HOURS`); there is no upper limit |
| Delivered result | One `JobResults` per account, per run, on a background thread |
| Scheduling | `WorkManager` periodic work; requires a network connection |
| Opt in | Per process launch, via `begin()` — see [Arming and Re-Arming](#auto_scrape_arming) |
| Account linking | Unchanged — link accounts with your existing IMAP flow (`ImapClient.link(...)` / `ImapSetupDialogFragment`) |

No new manifest entries, permissions, or R8/ProGuard rules are required. The SDK ships the consumer rules that keep `AutoScrapeClient` and its worker, and it bundles WorkManager itself. If your app provides its own WorkManager configuration, follow the [WorkManager Integration](https://microblink.github.io/blinkreceipt-android/workmanager_integration/) guide.

<br />

### <a name=auto_scrape_quick_start></a> Quick Start

Create the client and arm it in your `Application` class, once the digital SDK has initialized.

Kotlin
```kotlin
class BlinkApplication : Application() {

    // Hold the client for the life of the process; see "Arming and Re-Arming" below.
    private lateinit var autoScrapeClient: AutoScrapeClient

    override fun onCreate() {
        super.onCreate()

        BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {

            override fun onComplete() {
                autoScrapeClient = AutoScrapeClient(this@BlinkApplication).apply {
                    intervalHours = 24
                }

                autoScrapeClient.begin(
                    success = { credentials, result ->
                        // Background thread. One call per scraped account, per run.
                        Log.d(TAG, "auto-scrape ${credentials.username} job ${result.id} success ${result.success}")
                    },
                    failure = { throwable ->
                        // Background thread. One call per failed account.
                        Log.e(TAG, "auto-scrape failed", throwable)
                    },
                )
            }

            override fun onException(e: Throwable) {
                // Auto-scrape cannot be armed if the SDK failed to initialize.
            }
        })
    }
}
```

Java

`begin()` takes two Kotlin function types, which Java sees as `Function2` / `Function1`. Lambdas work as normal — just return `Unit.INSTANCE`.

```java
import kotlin.Unit;

public final class BlinkApplication extends Application {

    private AutoScrapeClient autoScrapeClient;

    @Override
    public void onCreate() {
        super.onCreate();

        BlinkReceiptDigitalSdk.initialize(this, new InitializeCallback() {

            @Override
            public void onComplete() {
                autoScrapeClient = new AutoScrapeClient(BlinkApplication.this);

                autoScrapeClient.intervalHours(24);

                autoScrapeClient.begin(
                        (credentials, result) -> {
                            // Background thread. One call per scraped account, per run.
                            Log.d(TAG, "auto-scrape " + credentials.username()
                                    + " job " + result.getId()
                                    + " success " + result.getSuccess());

                            return Unit.INSTANCE;
                        },
                        throwable -> {
                            // Background thread. One call per failed account.
                            Log.e(TAG, "auto-scrape failed", throwable);

                            return Unit.INSTANCE;
                        }
                );
            }

            @Override
            public void onException(@NonNull Throwable throwable) {
                // Auto-scrape cannot be armed if the SDK failed to initialize.
            }
        });
    }
}
```

That is the whole integration. From here the SDK takes over: when the user links a Gmail account the periodic work starts, and when the last one is removed it is cancelled.

<br />

### <a name=auto_scrape_arming></a> Arming and Re-Arming

`begin()` is what we call *arming*. Two rules follow from it, and they are the only part of auto-scrape that needs care:

1. **The armed state is in-memory only.** It is deliberately not persisted — auto-scrape is user consent, and consent should not survive a process death silently. Every scheduled run checks whether a client is currently armed; if none is, the run is skipped and the cadence is left intact for next time.
2. **Therefore you must re-arm on every process launch.** Call `begin()` from `Application.onCreate()` (or from your own startup path that always runs), not only from the screen where the user turned the feature on.

If you persist the user's preference yourself, gate the launch-time `begin()` on it:

Kotlin
```kotlin
if (settings.autoScrapeEnabled) {
    autoScrapeClient.begin(success = ::onAutoScrapeResult, failure = ::onAutoScrapeError)
}
```

Java

If you prefer method references over lambdas, the methods must return `Unit` so Java can convert them to the Kotlin function types:

```java
if (settings.isAutoScrapeEnabled()) {
    autoScrapeClient.begin(this::onAutoScrapeResult, this::onAutoScrapeError);
}

private Unit onAutoScrapeResult(Credentials.Password credentials, JobResults result) {
    // ...

    return Unit.INSTANCE;
}

private Unit onAutoScrapeError(Throwable throwable) {
    // ...

    return Unit.INSTANCE;
}
```

Re-arming is cheap and safe: `begin()` replaces the previous subscription rather than stacking a second one, and re-arming with an unchanged interval preserves the running cadence, so a launch-time `begin()` does not restart the countdown to the next run.

> **Scope the client to your `Application`, not to an `Activity`.** `close()` cancels the scheduled work, so a client that is closed in `Activity.onDestroy()` takes the schedule down with it every time the user leaves the screen.

<br />

### <a name=auto_scrape_configuration></a> Configuration

All four properties are plain read/write properties on `AutoScrapeClient`. Set them before or after `begin()`; changing one while armed re-applies it to the schedule immediately, and changes made while unarmed are picked up by the next `begin()`.

| Property | Type | Default | Description |
|---|---|---|---|
| `intervalHours` | `Int` | `24` | Cadence of the automated scrape, in hours. Values below `MIN_INTERVAL_HOURS` (12) — including `0` and negatives — are floored at 12. There is no upper limit. Changing it reschedules the work so the new cadence applies right away. |
| `overrideEndpoint` | `String?` | `null` | Overrides the endpoint the backend POSTs automated-scrape results to. `null` keeps the endpoint configured server side. The counterpart of `ImapClient.overrideEndpoint(...)`. |
| `overrideDateTime` | `Long?` | `null` | Date cutoff for the scrape, in epoch milliseconds. `null` keeps the backend's own cutoff. The counterpart of `ImapClient.overrideDateTime(...)`. |
| `countryCode` | `String?` | `null` | ISO 2-character country code hint used when parsing results. `null` keeps the SDK default (`US`). |

Kotlin
```kotlin
autoScrapeClient.apply {
    intervalHours = 36                                  // 36h cadence
    countryCode = "GB"
    overrideEndpoint = "https://receipts.example.com"    // usually left null in production
    overrideDateTime = null                              // keep the backend cutoff
}
```

Java
```java
autoScrapeClient.intervalHours(36);
autoScrapeClient.countryCode("GB");
autoScrapeClient.overrideEndpoint("https://receipts.example.com");
autoScrapeClient.overrideDateTime(null);
```

Two static helpers are available if you want to validate before applying:

Kotlin
```kotlin
val effective = AutoScrapeClient.clampInterval(userChoice)          // e.g. 6 -> 12
val inScope = AutoScrapeClient.isAutoScrapeAccount(credentials)     // will this account be scraped?
```

Java
```java
int effective = AutoScrapeClient.clampInterval(userChoice);
boolean inScope = AutoScrapeClient.isAutoScrapeAccount(credentials);
```

<br />

### <a name=auto_scrape_callbacks></a> Callbacks and Threading

The `success` and `failure` lambdas carry the same parameters as the manual remote scrape's `JobResultsCallback`, and are invoked **on a background thread**.

* `success(credentials, result)` — fires once per account that was submitted successfully. `credentials` identifies the account (`credentials.username`); `result` is the `JobResults` handle: `id`, `success`, `code` (`JobStatusCode`), `message`.
* `failure(throwable)` — fires once per account that failed. Accounts are independent, so a single run can deliver both successes and failures.
* Post to the main thread yourself before touching UI.
* Exceptions thrown by your callbacks are caught and logged; they cannot stop delivery for the remaining accounts. Still, keep the callbacks short and non-blocking — persist the job id, update a counter, enqueue your own work.

Kotlin
```kotlin
autoScrapeClient.begin(
    success = { credentials, result ->
        if (result.success == true) {
            repository.recordAutoScrapeJob(credentials.username, result.id)
        } else {
            // A completed job that reported a problem — inspect result.code / result.message.
            analytics.autoScrapeRejected(result.code, result.message)
        }
    },
    failure = { throwable -> analytics.autoScrapeFailed(throwable) },
)
```

Java
```java
autoScrapeClient.begin(
        (credentials, result) -> {
            if (Boolean.TRUE.equals(result.getSuccess())) {
                repository.recordAutoScrapeJob(credentials.username(), result.getId());
            } else {
                analytics.autoScrapeRejected(result.getCode(), result.getMessage());
            }

            return Unit.INSTANCE;
        },
        throwable -> {
            analytics.autoScrapeFailed(throwable);

            return Unit.INSTANCE;
        }
);
```

A `JobStatusCode` such as `INVALID_CREDENTIALS` or `EXPIRED_TOKEN` is your cue to prompt the user to re-link that account — the schedule will keep running and keep failing until they do.

<br />

### <a name=auto_scrape_schedule></a> How The Schedule Behaves

You do not schedule or cancel anything yourself; the SDK converges the background work onto one rule — **it exists only while a client is armed and at least one in-scope account is linked.** In practice:

* **Arming with no account linked does nothing visible.** The client stays dormant and the schedule starts the moment the user links a Gmail account.
* **Signing out the last in-scope account cancels the work**, whether or not a client is armed at that moment. A signed-out user's timer never keeps ticking.
* **There is only ever one worker.** All scheduling funnels through a single unique work name, so arming several clients, re-arming at launch, or changing the interval repeatedly cannot produce overlapping runs.
* **Runs need connectivity.** The work carries a "network connected" constraint; a transient failure is retried with exponential backoff starting at 30 minutes.
* **The cadence is a floor, not a guarantee.** This is standard `WorkManager` periodic work, so Doze, App Standby buckets, and battery optimizations can defer a run. Treat `intervalHours` as "no more often than this", and never build UI that promises a scrape at an exact time.
* **The user's app-password/credential state is the source of truth.** Auto-scrape reads accounts from the same encrypted credential storage the manual clients use; you never pass credentials to `AutoScrapeClient`.

<br />

### <a name=auto_scrape_stopping></a> Stopping Auto-Scrape

| Call | Effect | Use it when |
|---|---|---|
| `cancel()` | Unsubscribes your callbacks and cancels the scheduled work. The same instance can be re-armed later with `begin()`. | The user turns the feature off in your settings screen. |
| `close()` | Releases the client for good: cancels its internal scope and the scheduled work. The instance must not be reused. | Your `Application`-scoped client is being torn down (or an `Activity`-scoped one in `onDestroy()`). |

Kotlin
```kotlin
// User toggled the feature off
autoScrapeClient.cancel()

// Later, user toggled it back on
autoScrapeClient.begin(success = ::onAutoScrapeResult, failure = ::onAutoScrapeError)
```

Java
```java
autoScrapeClient.cancel();

autoScrapeClient.begin(this::onAutoScrapeResult, this::onAutoScrapeError);
```

Remember to persist the toggle so your launch-time `begin()` reflects it — see [Arming and Re-Arming](#auto_scrape_arming).

<br />

### <a name=auto_scrape_best_practices></a> Best Practices

**Do**

1. **Create exactly one `AutoScrapeClient` per process** and hold it in your `Application` (or an app-scoped DI component). Every armed client receives every result, so two armed instances mean duplicate callbacks.
2. **Arm inside `InitializeCallback.onComplete()`**, after `BlinkReceiptDigitalSdk.initialize(...)` succeeds.
3. **Re-arm on every launch**, gated on your own persisted user preference.
4. **Ask for consent before arming.** Background inbox scraping is a user-visible privacy decision; give the user a toggle and honor it with `cancel()`.
5. **Leave `intervalHours` at the 24h default** unless you have a specific reason. Shorter cadences cost battery and rarely surface more receipts; the SDK will floor anything under 12h anyway.
6. **Keep callbacks short, and marshal to the main thread** before touching UI.
7. **Record `JobResults.id`** if you reconcile receipts against your own backend — it is the handle for the server-side job that will POST the receipts.
8. **React to credential errors.** Surface a re-link prompt when a run reports `INVALID_CREDENTIALS`, `EXPIRED_TOKEN`, or `NO_CREDENTIALS`.
9. **Leave `overrideEndpoint` / `overrideDateTime` `null` in production.** They exist for testing and for hosts with a bespoke ingestion endpoint.

**Don't**

1. **Don't scope the client to an `Activity` or `Fragment`.** `close()` cancels the schedule.
2. **Don't expect `ScanResults`.** Auto-scrape reports jobs; receipts arrive at your endpoint. Use `ImapClient.messages()` for on-device results.
3. **Don't schedule your own periodic work that calls `remoteMessages()` as well.** You would double-submit jobs for the same inbox.
4. **Don't assume runs are punctual**, and don't block a user flow on a scheduled scrape. Give the user a manual "check now" action backed by `ImapClient.remoteMessages()` if immediacy matters.
5. **Don't call `begin()` per screen.** Repeated arming is safe but pointless — one launch-time call is the whole contract.
6. **Don't reuse a closed client.** Create a new instance instead.

<br />

### <a name=auto_scrape_testing></a> Testing and QA

Waiting 24 hours is not a test plan. Two practical options:

**1. Shorten the cadence.** The floor is 12 hours, which is still slow, but it verifies real scheduling end to end.

**2. Enqueue the worker directly (debug builds only).** `AutoScrapeWorker` is public, so QA can force a run:

Kotlin
```kotlin
WorkManager.getInstance(applicationContext).enqueueUniqueWork(
    "auto-scrape-qa",
    ExistingWorkPolicy.REPLACE,
    OneTimeWorkRequestBuilder<AutoScrapeWorker>().build(),
)
```

Java
```java
WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(
        "auto-scrape-qa",
        ExistingWorkPolicy.REPLACE,
        new OneTimeWorkRequest.Builder(AutoScrapeWorker.class).build()
);
```

Two caveats for this path: the worker still requires an armed client (call `begin()` first, or the run is skipped), and a hand-built request carries none of your `AutoScrapeClient` override configuration, so the run uses server-side defaults for endpoint, cutoff, and country.

You can also inspect the real schedule with `adb shell dumpsys jobscheduler` or WorkManager's own diagnostics; the work is registered under the unique name `com.microblink.digital.auto-scrape`.

<br />

### <a name=auto_scrape_troubleshooting></a> Troubleshooting

| Symptom | Likely cause |
|---|---|
| Nothing is ever scheduled | `begin()` was never called, or no in-scope account is linked. Auto-scrape covers **Gmail** IMAP accounts with a non-empty username; check with `AutoScrapeClient.isAutoScrapeAccount(credentials)`. |
| Scheduled, but callbacks never fire | The process was restarted and `begin()` was not called again this launch, so each run skips. Move the `begin()` call into `Application.onCreate()`. |
| Callbacks stopped firing after leaving a screen | The client was `close()`d with the `Activity`. Scope it to the `Application`. |
| Duplicate callbacks for the same job | More than one `AutoScrapeClient` is armed in the process. Keep a single instance. |
| Runs are much later than `intervalHours` | Expected. Doze / App Standby / battery optimization defer background work; the interval is a minimum. |
| `JobResults.success` is `false` with a credential `code` | The stored app password is no longer valid. Prompt the user to re-link that account. |
| Interval change appears to be ignored | Values under 12 are floored to `MIN_INTERVAL_HOURS`. Confirm with `AutoScrapeClient.clampInterval(hours)`. |
| Crash or missing class after enabling R8 | The SDK's consumer rules keep the auto-scrape API and worker; make sure you have not stripped consumer rules from your build. |

<br />
<br />

## <a name=Outlook></a> Outlook
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
    client.login(this).addOnSuccessListener {

   }.addOnFailureListener {

   }
```
### Outlook Logout
```kotlin
client.logout().addOnSuccessListener {

}.addOnFailureListener {

}
```

### Outlook Messages
Messages returns a Task, which allows you to get a list of scan results for messages found in the Outlook mailbox.
```kotlin
 client.messages().addOnSuccessListener {

}.addOnFailureListener {

}
```
### Outlook Destroy Client
```kotlin
override fun onDestroy() {
    super.onDestroy()

    client.close()
}
```

<br />
<br />
<br />
<br />

## **Gmail**

Blink Receipt Digital sdk allows for full Gmail Integration.

## <a name=gmail></a> **Gmail Client**

The `GmailClient` is the corner stone of the gmail sdk integration. It is the access point for reading and parsing emails from clients. It leverages Google's task framework to allow for seamless and clear multi-threading functionality.

To instantiate the `GmailClient` you must provide the constructor 3 non-null and non-zero arguments.

1. Context: `Context`. When using the client within an Android `Activity` you can pass in `this` for the argument value. If using the client within an Android `Fragment` you can pass in `requireActivity()`.

2. Thread Count: `int` which determines the number of threads to use for processing `e-receipt` emails. The internal default value we use is `4`.

3. Client Id: `String` which comes from  Google's api services. Please refer [here](https://developers.google.com/identity/protocols/oauth2/native-app) for more information on how to create an Android client id.

<br />

#### Code sample for Gmail Client Instantiation: KOTLIN
```kotlin
    // Activity Example
    class GmailActivity: Activity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            val gmailClient = GmailClient(this, 4)
        }

    }

    // Fragment Example
    class GmailFragment: Fragment() {

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val gmailClient = GmailClient(requireActivity(), 4)
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

```kotlin
    fun loginUser() {
        val task: Task<GoogleSignInAccount> = gmailClient.login()

        task.addOnSuccessListener {

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

```kotlin
class GmailInboxFragment : Fragment() {

    private lateinit var gmailClient: GmailClient

    //...Instantiate GmailClient in one of the lifecycle methods

    // Attempt to login
    fun login() {
        gmailClient.login()
                .addOnSuccessListener {

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

        gmailClient.onAccountAuthorizationActivityResult(requestCode, resultCode, data)
           .addOnSuccessListener { signInAccount ->

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

```kotlin
     private fun verifyUser() {
        gmailClient.verify()
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }
```

<br />
<br />

### **Logging Out of Gmail**

Users may log out of gmail via the client's `logout()` function. The logout function will return a `Task<Boolen>`. This function completes 2 objectives. First, it signs the currently signed in user out from Gmail/Google SDK. This means that the user can no longer be "silently" signed in to Google. The next time the `verify()` is called a `false` result should be returned via the `Task` return object. This also means that the `credentials()` call will not return a `GoogleSignInAccount` result, but instead throw an exception (not unlike the initial user flow for sign in we covered before). Secondly, the `logout()` call will clear any cached "date" threshold we use for email searching. You should never receive a `Boolean` result of `false` for the signout task. It will always either be true or throw an exception in the unlikely event of an error.

<br />

**EXAMPLE GMAIL LOGOUT IMPLEMENTATION**

```kotlin
    private fun logoutUser() {
        gmailClient.logout().addOnSuccessListener {

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

```kotlin

    fun messages() {
      client.messages(requireActivity())
       .addOnSuccessListener { results ->

       }.addOnFailureListener {

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

    client.close()
}
```

##  Requirements
- AndroidX
- Min SDK 23+
- Compile SDK: 36+
- Java 17+

### Product Intelligence
If you wish to include product intelligence functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```xml
 <meta-data android:name="com.microblink.ProductIntelligence" android:value="PRODUCT INTELLIGENCE KEY" />
```

# Package com.microblink.digital
