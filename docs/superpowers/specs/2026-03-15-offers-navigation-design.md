# OffersNavigation Public API Design

## Summary

Refactor the Engage SDK's offers flow from a host-app-orchestrated architecture to an SDK-owned composable (`OffersNavigation`) with slotted callbacks. The host app embeds the composable, provides camera scanning via a suspend callback, and receives reward/lifecycle events through typed callbacks. All navigation, Koin DI, and internal state management become SDK-internal concerns.

## Motivation

The current architecture requires the host app to:
- Own `OffersNavDisplay` (navigation orchestration)
- Subscribe to `EngageEventBus` (leaked internal implementation)
- Configure `KoinApplication` with `legacyEngageModule`
- Hardcode `CameraRecognizerScreen` as a navigation destination
- Pass `EngageConfiguration` through composable parameters

This leaks SDK internals, creates tight coupling, and makes integration fragile. The host app should only need to embed a composable and provide what it uniquely owns: the camera scanning implementation and the receipts SDK to engage model mapping.

## Public API

### OffersNavigation Composable

The primary integration point. Lives at `com.actualplatform.engage.OffersNavigation`.

```kotlin
@Composable
fun OffersNavigation(
    modifier: Modifier = Modifier,
    offerStyle: OfferStyle = OfferStyle.default(),
    onScanReceipt: suspend () -> ScanResults?,
    onRewardEarned: (Reward) -> Unit = {},
    onContinue: () -> Unit = {},
    onError: (Int, String?) -> Unit = {},
    onDismiss: () -> Unit,
)
```

**Parameters:**
- `modifier` — Standard Compose modifier for sizing/layout.
- `offerStyle` — Customization for the offer wall UI (title, FAB text). Passed internally to `OfferScreen`.
- `onScanReceipt` — Suspend callback invoked when the user taps "scan receipt" on the offer wall. The host app launches its camera, scans a receipt, maps the results from the receipts SDK's `ScanResults` to the engage model's `ScanResults`, and returns them. Returns `null` if the scan was cancelled or failed.
- `onRewardEarned` — Called when the user earns a reward (promo qualification or boost claim).
- `onContinue` — Called when the user taps "continue" on the receipt summary screen.
- `onError` — Called when an error occurs during the flow (validation failure, network error, etc.).
- `onDismiss` — Called when the user dismisses the offers flow.

### Reward Sealed Class

Lives at `com.actualplatform.engage.Reward`.

```kotlin
sealed class Reward(open val amount: Float) {
    data class Promotion(override val amount: Float) : Reward(amount)
    data class Boost(override val amount: Float) : Reward(amount)
}
```

### EngageClient — Flattened Configuration Properties

`EngageConfiguration` is removed as a wrapper. Its properties are flattened directly onto `EngageClient`:

```kotlin
// New properties on EngageClient
var rewardCurrencyName: String
var rewardPayoutPercentage: Double
var rewardIcon: ByteArray?
```

These are injected internally via Koin where needed (ViewModels, use cases) — the host app sets them on the client, the SDK reads them internally.

### Individual Screens (Stay Public)

For advanced clients who want to build their own navigation flow, individual screens remain public at the root package `com.actualplatform.engage`:

- `OfferScreen` — Offer wall composable
- `AdsLoadingScreen` — Ads loading and receipt validation composable
- `ReceiptSummaryScreen` — Reward summary composable

Using these directly requires the client to manage their own navigation, Koin setup, and `ReceiptSessionStateHolder`.

## Package Structure

All public types live at the root package `com.actualplatform.engage/`:

```
com.actualplatform.engage/
    EngageClient.kt
    OffersNavigation.kt          ← NEW
    Reward.kt                    ← NEW
    OfferScreen.kt               ← moved from legacy/offers/
    AdsLoadingScreen.kt          ← moved from legacy/loading/
    ReceiptSummaryScreen.kt      ← moved from legacy/rss/
    OfferStyle.kt                ← moved from legacy/offers/
    RewardCurrency.kt            ← moved from legacy/
    NetworkAnalyzer.kt
    Results.kt
    AppSessionState.kt

com.actualplatform.engage.internal/
    ... all internal implementation (ViewModels, use cases, DI modules, etc.)
```

## Navigation Flow

Managed entirely within `OffersNavigation`:

```
OfferScreen → [user taps scan] → onScanReceipt() → AdsLoadingScreen → ReceiptSummaryScreen
                                  (host app camera)    (validate/load)    (show rewards)
```

- **OfferScreen → scan**: SDK calls `onScanReceipt()`. Host app launches camera, returns `ScanResults?`.
- **Scan results → AdsLoadingScreen**: SDK navigates internally, passes results to validation/loading.
- **AdsLoadingScreen → ReceiptSummaryScreen**: SDK navigates internally after validation completes.
- **ReceiptSummaryScreen back**: Navigates back to OfferScreen (internal).
- **ReceiptSummaryScreen continue**: Calls `onContinue()` callback.
- **ReceiptSummaryScreen receipt icon click**: Handled internally.
- **Reward earned (promo or boost)**: Calls `onRewardEarned(Reward)` callback.
- **Error**: Calls `onError(code, message)` callback.
- **Dismiss**: Calls `onDismiss()` callback.

## Camera Scanning Integration

The `onScanReceipt` callback uses the suspend/Channel bridge pattern to interop with Android's `ActivityResultContract`:

```kotlin
// Host app usage
val scanResultChannel = remember { Channel<ScanResults?>() }
val launcher = rememberLauncherForActivityResult(scanContract) { result ->
    scanResultChannel.trySend(
        when (result) {
            is CameraRecognizerResults.Success -> result.results?.toEngageModel()
            else -> null
        }
    )
}

OffersNavigation(
    onScanReceipt = {
        launcher.launch(scanOptions)
        scanResultChannel.receive()
    },
    onRewardEarned = { reward ->
        when (reward) {
            is Reward.Promotion -> log("Promo: ${reward.amount}")
            is Reward.Boost -> log("Boost: ${reward.amount}")
        }
    },
    onContinue = { finish() },
    onDismiss = { finish() },
)
```

The `toEngageModel()` mapper extension function remains the host app's responsibility, as it bridges the receipts SDK's `ScanResults` to the engage model's `ScanResults`.

## What Gets Removed

| Component | Reason |
|---|---|
| `EngageEventBus` | Completely removed. Replaced by slotted callbacks on `OffersNavigation`. |
| `EngageEvent` sealed interface | Removed. Each event type becomes a callback parameter. |
| `EngageConfiguration` data class | Removed. Properties flattened onto `EngageClient`. |
| `LocalEngageConfiguration` CompositionLocal | Removed. Config values injected via Koin from `EngageClient`. |

## What Gets Moved

| Component | From | To |
|---|---|---|
| `OfferScreen` | `com.actualplatform.engage.legacy.offers` | `com.actualplatform.engage` |
| `AdsLoadingScreen` | `com.actualplatform.engage.legacy.loading` | `com.actualplatform.engage` |
| `ReceiptSummaryScreen` | `com.actualplatform.engage.legacy.rss` | `com.actualplatform.engage` |
| `OfferStyle` | `com.actualplatform.engage.legacy.offers` | `com.actualplatform.engage` |
| `RewardCurrency` | `com.actualplatform.engage.legacy` | `com.actualplatform.engage` |

## Internal Changes

### OffersNavigation Implementation

`OffersNavigation` absorbs the logic currently in `OffersNavDisplay` (windfall-android-sdk):
- Navigation3 backstack management with routes: OfferWall, AdsLoading, ReceiptSummary
- No CameraScan route — camera is handled via the suspend callback, not as a navigation destination
- ReceiptSessionStateHolder management (clear on new scan, etc.)
- Error handling routed to `onError` callback
- Reward events from ViewModels routed to `onRewardEarned` callback

### ViewModel Changes

- `OfferViewModel`: Instead of emitting `EngageEvent.CameraScan` to the event bus, it exposes a state/callback that `OffersNavigation` observes to trigger `onScanReceipt`.
- `ReceiptSummaryViewModel`: Instead of emitting `EngageEvent.RewardEarned` to the event bus, it calls through a callback chain to `onRewardEarned`.
- All ViewModels read reward currency config from `EngageClient` (injected via Koin) instead of receiving `EngageConfiguration` as a parameter.

### Koin Module Changes

- `legacyEngageModule` becomes fully internal — never exposed to host apps.
- Koin context is managed within `OffersNavigation` via `KoinApplication` composable wrapper (internal).
- `EngageConfiguration`-related beans removed; replaced by direct reads from `EngageClient`.

## Host App Changes (windfall-android-sdk)

- `OffersNavDisplay.kt` — deleted (logic moves to SDK's `OffersNavigation`)
- `OffersActivity.kt` — simplified to just `setContent { OffersNavigation(...) }`
- `ScanResultsMapper.kt` — stays in host app (mapping is host responsibility)
- No Koin imports or setup needed
- No EngageEventBus references
