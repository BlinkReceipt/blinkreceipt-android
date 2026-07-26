Auto-scrape keeps a linked inbox producing receipts without the user ever reopening your app. Once armed, the SDK schedules a periodic background job; each run walks the user's linked in-scope accounts and creates the same server-side scrape job that a manual `ImapClient.remoteMessages()` call creates. The backend reads the inbox, parses the receipts, and POSTs them to your configured client endpoint.

**What comes back to your app is a job handle, not receipts.** Each account's run delivers a `JobResults` — job id, success flag, `JobStatusCode`, and message — to the callback you register. Receipt data itself lands on your endpoint server side, exactly as it does for a manual remote scrape. If you need `ScanResults` on the device, keep using `ImapClient.messages()`; auto-scrape is not a replacement for it.

Nothing is scheduled until you opt in by calling `begin()`, and nothing is scheduled while the user has no in-scope account linked.

## At a glance

| | |
|---|---|
| Entry point | `AutoScrapeClient` (`com.microblink.digital`) |
| Provider scope | Gmail IMAP accounts with a non-empty username — see `AutoScrapeClient.isAutoScrapeAccount(credentials)` |
| Default cadence | 24 hours (`AutoScrapeClient.DEFAULT_INTERVAL_HOURS`) |
| Minimum cadence | 12 hours (`AutoScrapeClient.MIN_INTERVAL_HOURS`); there is no upper limit |
| Delivered result | One `JobResults` per account, per run, on a background thread |
| Scheduling | `WorkManager` periodic work; requires a network connection |
| Opt in | Per process launch, via `begin()` — see [Arming and re-arming](#arming-and-re-arming) |
| Account linking | Unchanged — link accounts with your existing IMAP flow (`ImapClient.link(...)` / `ImapSetupDialogFragment`) |

No new manifest entries, permissions, or R8/ProGuard rules are required. The SDK ships the consumer rules that keep `AutoScrapeClient` and its worker, and it bundles WorkManager itself. If your app provides its own WorkManager configuration, follow the [WorkManager Integration](workmanager_integration.md) guide.

## Quick start

Create the client and arm it in your `Application` class, once the digital SDK has initialized.

=== "Kotlin"
    ```kotlin
    class BlinkApplication : Application() {

        // Hold the client for the life of the process; see "Arming and re-arming" below.
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

=== "Java"
    `begin()` takes two Kotlin function types, which Java sees as `Function2` / `Function1`. Lambdas work as
    normal — just return `Unit.INSTANCE`.

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

## Arming and re-arming

`begin()` is what we call *arming*. Two rules follow from it, and they are the only part of auto-scrape that needs care:

1. **The armed state is in-memory only.** It is deliberately not persisted — auto-scrape is user consent, and consent should not survive a process death silently. Every scheduled run checks whether a client is currently armed; if none is, the run is skipped and the cadence is left intact for next time.
2. **Therefore you must re-arm on every process launch.** Call `begin()` from `Application.onCreate()` (or from your own startup path that always runs), not only from the screen where the user turned the feature on.

If you persist the user's preference yourself, gate the launch-time `begin()` on it:

=== "Kotlin"
    ```kotlin
    if (settings.autoScrapeEnabled) {
        autoScrapeClient.begin(success = ::onAutoScrapeResult, failure = ::onAutoScrapeError)
    }
    ```

=== "Java"
    If you prefer method references over lambdas, the methods must return `Unit` so Java can convert
    them to the Kotlin function types:

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

!!! warning "Scope the client to your `Application`, not to an `Activity`"
    `close()` cancels the scheduled work, so a client that is closed in `Activity.onDestroy()` takes the
    schedule down with it every time the user leaves the screen.

## Configuration

All four properties are plain read/write properties on `AutoScrapeClient`. Set them before or after `begin()`; changing one while armed re-applies it to the schedule immediately, and changes made while unarmed are picked up by the next `begin()`.

| Property | Type | Default | Description |
|---|---|---|---|
| `intervalHours` | `Int` | `24` | Cadence of the automated scrape, in hours. Values below `MIN_INTERVAL_HOURS` (12) — including `0` and negatives — are floored at 12. There is no upper limit. Changing it reschedules the work so the new cadence applies right away. |
| `overrideEndpoint` | `String?` | `null` | Overrides the endpoint the backend POSTs automated-scrape results to. `null` keeps the endpoint configured server side. The counterpart of `ImapClient.overrideEndpoint(...)`. |
| `overrideDateTime` | `Long?` | `null` | Date cutoff for the scrape, in epoch milliseconds. `null` keeps the backend's own cutoff. The counterpart of `ImapClient.overrideDateTime(...)`. |
| `countryCode` | `String?` | `null` | ISO 2-character country code hint used when parsing results. `null` keeps the SDK default (`US`). |

=== "Kotlin"
    ```kotlin
    autoScrapeClient.apply {
        intervalHours = 36                                   // 36h cadence
        countryCode = "GB"
        overrideEndpoint = "https://receipts.example.com"     // usually left null in production
        overrideDateTime = null                               // keep the backend cutoff
    }
    ```

=== "Java"
    ```java
    autoScrapeClient.intervalHours(36);
    autoScrapeClient.countryCode("GB");
    autoScrapeClient.overrideEndpoint("https://receipts.example.com");
    autoScrapeClient.overrideDateTime(null);
    ```

Two static helpers are available if you want to validate before applying:

=== "Kotlin"
    ```kotlin
    val effective = AutoScrapeClient.clampInterval(userChoice)          // e.g. 6 -> 12
    val inScope = AutoScrapeClient.isAutoScrapeAccount(credentials)     // will this account be scraped?
    ```

=== "Java"
    ```java
    int effective = AutoScrapeClient.clampInterval(userChoice);
    boolean inScope = AutoScrapeClient.isAutoScrapeAccount(credentials);
    ```

## Callbacks and threading

The `success` and `failure` lambdas carry the same parameters as the manual remote scrape's `JobResultsCallback`, and are invoked **on a background thread**.

* `success(credentials, result)` — fires once per account that was submitted successfully. `credentials` identifies the account (`credentials.username`); `result` is the `JobResults` handle: `id`, `success`, `code` (`JobStatusCode`), `message`.
* `failure(throwable)` — fires once per account that failed. Accounts are independent, so a single run can deliver both successes and failures.
* Post to the main thread yourself before touching UI.
* Exceptions thrown by your callbacks are caught and logged; they cannot stop delivery for the remaining accounts. Still, keep the callbacks short and non-blocking — persist the job id, update a counter, enqueue your own work.

=== "Kotlin"
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

=== "Java"
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

## How the schedule behaves

You do not schedule or cancel anything yourself; the SDK converges the background work onto one rule — **it exists only while a client is armed and at least one in-scope account is linked.** In practice:

* **Arming with no account linked does nothing visible.** The client stays dormant and the schedule starts the moment the user links a Gmail account.
* **Signing out the last in-scope account cancels the work**, whether or not a client is armed at that moment. A signed-out user's timer never keeps ticking.
* **There is only ever one worker.** All scheduling funnels through a single unique work name, so arming several clients, re-arming at launch, or changing the interval repeatedly cannot produce overlapping runs.
* **Runs need connectivity.** The work carries a "network connected" constraint; a transient failure is retried with exponential backoff starting at 30 minutes.
* **The cadence is a floor, not a guarantee.** This is standard `WorkManager` periodic work, so Doze, App Standby buckets, and battery optimizations can defer a run. Treat `intervalHours` as "no more often than this", and never build UI that promises a scrape at an exact time.
* **The user's credential state is the source of truth.** Auto-scrape reads accounts from the same encrypted credential storage the manual clients use; you never pass credentials to `AutoScrapeClient`.

## Stopping auto-scrape

| Call | Effect | Use it when |
|---|---|---|
| `cancel()` | Unsubscribes your callbacks and cancels the scheduled work. The same instance can be re-armed later with `begin()`. | The user turns the feature off in your settings screen. |
| `close()` | Releases the client for good: cancels its internal scope and the scheduled work. The instance must not be reused. | Your `Application`-scoped client is being torn down (or an `Activity`-scoped one in `onDestroy()`). |

=== "Kotlin"
    ```kotlin
    // User toggled the feature off
    autoScrapeClient.cancel()

    // Later, user toggled it back on
    autoScrapeClient.begin(success = ::onAutoScrapeResult, failure = ::onAutoScrapeError)
    ```

=== "Java"
    ```java
    autoScrapeClient.cancel();

    autoScrapeClient.begin(this::onAutoScrapeResult, this::onAutoScrapeError);
    ```

Remember to persist the toggle so your launch-time `begin()` reflects it — see [Arming and re-arming](#arming-and-re-arming).

## Best practices

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

## Testing and QA

Waiting 24 hours is not a test plan. Two practical options:

**1. Shorten the cadence.** The floor is 12 hours, which is still slow, but it verifies real scheduling end to end.

**2. Enqueue the worker directly (debug builds only).** `AutoScrapeWorker` is public, so QA can force a run:

=== "Kotlin"
    ```kotlin
    WorkManager.getInstance(applicationContext).enqueueUniqueWork(
        "auto-scrape-qa",
        ExistingWorkPolicy.REPLACE,
        OneTimeWorkRequestBuilder<AutoScrapeWorker>().build(),
    )
    ```

=== "Java"
    ```java
    WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(
            "auto-scrape-qa",
            ExistingWorkPolicy.REPLACE,
            new OneTimeWorkRequest.Builder(AutoScrapeWorker.class).build()
    );
    ```

Two caveats for this path: the worker still requires an armed client (call `begin()` first, or the run is skipped), and a hand-built request carries none of your `AutoScrapeClient` override configuration, so the run uses server-side defaults for endpoint, cutoff, and country.

You can also inspect the real schedule with `adb shell dumpsys jobscheduler` or WorkManager's own diagnostics; the work is registered under the unique name `com.microblink.digital.auto-scrape`.

## Troubleshooting

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
