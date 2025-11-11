The recommended way of using the Account Linking Android SDK in the background is using [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager), which is the recommended solution for persistent work.
To start, you have to define a class which will extend from a WorkManager worker.
We recommend extending from `CoroutineWorker` if you're working with Kotlin, and `ListenableWorker` if you're working with Java,
as we have to wait until the Account Linking SDK finishes before finishing work.

=== "Kotlin"
    ```kotlin
    public class AccountLinkingWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    }
    ```

=== "Java"
    ```java
    public class AccountLinkingWorker extends ListenableWorker {

        public AccountLinkingWorker(@NonNull Context appContext, @NonNull WorkerParameters params) {
            super(appContext, params);
        }
    }
    ```

Then you have to override the `doWork`/`startWork` function, in which we need to set the licence keys (if you haven't set them in the manifest file as described [here](getting_started_alp.md#setting-your-license-keys)), initialize the Account Linking SDK, instantiate the `AccountLinkingClient`, configure it, and call `orders`.

=== "Kotlin"
    ```kotlin
    override suspend fun doWork(): Result {
        //set license keys if you haven't done so in your AndroidManifest.xml
        BlinkReceiptLinkingSdk.licenseKey = "<your_account_linking_key>"
        BlinkReceiptLinkingSdk.productIntelligenceKey = "<your_product_intelligence_key>"

        //initialize the sdk
        BlinkReceiptLinkingSdk.initialize(applicationContext)

        val client = AccountLinkingClient(applicationContext)
        //configure the client
        client.dayCutoff = 14

        val suspendResult = suspendCoroutine { continuation ->
            client.orders(AMAZON, failure = { retailerId, exception ->
                //log exception
                Timber.e(exception)
                if(exception.code == VERIFICATION_NEEDED) {
                    // if verification is needed, you should attempt to retrieve orders while the app is in the foreground,
                    // and show the exception.view to the end user so he can complete the required verification
                }

                continuation.resume(Result.failure())
            }, success = { retailerId, results, remaining, uuid ->
                if(results != null){
                    //store results in database, API etc.
                }

                // no remaining orders, so we can complete the session
                if(remaining == 0){
                    continuation.resume(Result.success())
                }
            })
        }

        return suspendResult
    }
    ```
=== "Java"
    ```java
    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        //set license keys if you haven't done so in your AndroidManifest.xml
        BlinkReceiptLinkingSdk.licenseKey("<your_account_linking_licence_key>");
        BlinkReceiptLinkingSdk.productIntelligenceKey("<your_product_intelligence_key>");

        //initialize the sdk
        BlinkReceiptLinkingSdk.initialize(getApplicationContext());

        SettableFuture<Result> future = SettableFuture.create();
        AccountLinkingClient client = new AccountLinkingClient(this.getApplicationContext());
        //configure the client
        client.dayCutoff(14);
        client.orders(AMAZON, (Integer retailerId, ScanResults results, Integer remaining, String uuid) -> {
            if (results != null) {
                //store results in database, API etc.
            }

            // no remaining orders, so we can complete the session
            if (remaining == 0) {
                future.set(Result.success());
            }
            return Unit.INSTANCE;
        }, (Integer retailerId, AccountLinkingException exception) -> {
            //log exception
            Timber.e(exception);
            if (exception.code() == AccountLinkingCodes.VERIFICATION_NEEDED) {
                // if verification is needed, you should attempt to retrieve orders while the app is in the foreground,
                // and show the exception.view to the end user so he can complete the required verification
            }

            future.setException(exception);

            return Unit.INSTANCE;
        });

        return future;
    }
    ```

After we're done with the Worker implementation, we need to enqueue its execution. WorkManager enables you to enqueue either [one-time](https://developer.android.com/guide/background/persistent/getting-started/define-work#schedule_one-time_work) or [periodic](https://developer.android.com/guide/background/persistent/getting-started/define-work#schedule_periodic_work) work.
Depending on your specific needs, you can enqueue the execution at a specific point in your users journey.
Preferably, you should enqueue background work after you've successfully linked an account or retrieved orders at least once in the foreground, as additional verification may be required from the end user.
Here's an example on how to enqueue periodic work:

=== "Kotlin"
    ```kotlin
    val manager = WorkManager.getInstance(requireContext())
    val request = PeriodicWorkRequestBuilder<AccountLinkingWorker>(7, TimeUnit.DAYS)
    //account linking requires a working network connection
    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
    .build()

    manager.enqueueUniquePeriodicWork("AccountLinkingBackgroundWork", ExistingPeriodicWorkPolicy.UPDATE, request)
    ```
=== "Java"
    ```java
    WorkManager manager = WorkManager.getInstance(this);
    PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(AccountLinkingFragment.AccountLinkingWorker.class, 7, TimeUnit.DAYS)
    //account linking requires a working network connection
    .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
    .build();
    manager.enqueueUniquePeriodicWork("AccountLinkingBackgroundWork", ExistingPeriodicWorkPolicy.UPDATE, request);
    ```
