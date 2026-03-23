# OffersNavigation Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor the Engage SDK's offers flow into an SDK-owned `OffersNavigation` composable with slotted callbacks, removing `EngageEventBus` and making all navigation/DI internal.

**Architecture:** Two repos are modified: `engage-kmp` (the SDK) and `windfall-android-sdk` (the host app). SDK changes create the new public API and internalize navigation. Host app changes delete `OffersNavDisplay` and simplify `OffersActivity` to use the new composable.

**Tech Stack:** Kotlin Multiplatform, Jetpack Compose, Koin DI, Navigation3, Coroutines

---

## Chunk 1: New Public Types and EngageClient Properties

### Task 1: Create Reward sealed class

**Files:**
- Create: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/Reward.kt`

- [ ] **Step 1: Create Reward.kt**

```kotlin
package com.actualplatform.engage

/**
 * Represents a reward earned by the user during the offers flow.
 */
sealed class Reward(open val amount: Float) {
    /** Reward from a qualified promotion on a scanned receipt. */
    data class Promotion(override val amount: Float) : Reward(amount)
    /** Reward from claiming a boost offer (Google SDK rewarded ad or CPA). */
    data class Boost(override val amount: Float) : Reward(amount)
}
```

- [ ] **Step 2: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add engage/src/commonMain/kotlin/com/actualplatform/engage/Reward.kt
git commit -m "feat: add Reward sealed class to public API"
```

---

### Task 2: Add flattened reward currency properties to EngageClient

**Files:**
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/EngageClient.kt` (add properties after line 104)

- [ ] **Step 1: Add reward currency properties to EngageClient**

After the existing `testOptions` property (line 104), add:

```kotlin
/** Display name for the reward currency (e.g. "Points", "Stars"). */
@Volatile
var rewardCurrencyName: String = RewardCurrency.DEFAULT_NAME

/** Payout percentage applied to base reward amounts. Default 100.0. */
@Volatile
var rewardPayoutPercentage: Double = RewardCurrency.DEFAULT_PAYOUT_PERCENTAGE

/** Optional icon for the reward currency, as raw bytes (e.g. PNG). */
@Volatile
var rewardIcon: ByteArray? = null
```

Add the import for `RewardCurrency` if not already present. Check `RewardCurrency.kt` for the exact constant names — the defaults are `DEFAULT_PAYOUT_PERCENTAGE = 100.0`. If `DEFAULT_NAME` doesn't exist, add it to `RewardCurrency` companion object.

- [ ] **Step 2: Add DEFAULT_NAME constant to RewardCurrency if needed**

In `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/RewardCurrency.kt`, add to the companion object:

```kotlin
internal const val DEFAULT_NAME = "Points"
```

- [ ] **Step 3: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add engage/src/commonMain/kotlin/com/actualplatform/engage/EngageClient.kt
git add engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/RewardCurrency.kt
git commit -m "feat: add flattened reward currency properties to EngageClient"
```

---

### Task 3: Move public types to root package

Move `OfferStyle`, `RewardCurrency`, `OfferScreen`, `AdsLoadingScreen`, `ReceiptSummaryScreen` to root package `com.actualplatform.engage`. Update package declarations and imports across the codebase.

**Files:**
- Move: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/offers/OfferStyle.kt` → `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/OfferStyle.kt`
- Move: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/RewardCurrency.kt` → `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/RewardCurrency.kt`
- Move: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/loading/AdsLoadingScreen.kt` → `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/AdsLoadingScreen.kt`
- Move: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/rss/ReceiptSummaryScreen.kt` → `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/ReceiptSummaryScreen.kt`
- Update: all files that import the moved types

Note: `OfferScreen.kt` is already at `com.actualplatform.engage` — no move needed.

- [ ] **Step 1: Move OfferStyle.kt**

Move the file, update package declaration from `com.actualplatform.engage.legacy.offers` to `com.actualplatform.engage`. Update `LocalOfferStyle` if it exists in the same file.

- [ ] **Step 2: Move RewardCurrency.kt**

Move the file, update package declaration from `com.actualplatform.engage.legacy` to `com.actualplatform.engage`. Update the `calculate()` extension function in the same file.

- [ ] **Step 3: Move AdsLoadingScreen.kt**

Move the file, update package declaration from `com.actualplatform.engage.legacy.loading` to `com.actualplatform.engage`. Update internal imports to reference moved types.

- [ ] **Step 4: Move ReceiptSummaryScreen.kt**

Move the file, update package declaration from `com.actualplatform.engage.legacy.rss` to `com.actualplatform.engage`. Update internal imports to reference moved types.

- [ ] **Step 5: Fix all broken imports across the engage-kmp codebase**

Search for all imports referencing the old package paths and update them:
- `com.actualplatform.engage.legacy.offers.OfferStyle` → `com.actualplatform.engage.OfferStyle`
- `com.actualplatform.engage.legacy.RewardCurrency` → `com.actualplatform.engage.RewardCurrency`
- `com.actualplatform.engage.legacy.loading.AdsLoadingScreen` → `com.actualplatform.engage.AdsLoadingScreen`
- `com.actualplatform.engage.legacy.rss.ReceiptSummaryScreen` → `com.actualplatform.engage.ReceiptSummaryScreen`
- `com.actualplatform.engage.legacy.offers.LocalOfferStyle` → `com.actualplatform.engage.LocalOfferStyle`

- [ ] **Step 6: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add -A
git commit -m "refactor: move public types to root com.actualplatform.engage package"
```

---

## Chunk 2: Remove EngageEventBus and Refactor ViewModels

### Task 4: Refactor OfferViewModel to remove EngageEventBus dependency

**Files:**
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/offers/internal/ui/OfferViewModel.kt`
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/offers/internal/di/OfferModule.kt`

- [ ] **Step 1: Replace event bus emission with observable state**

In `OfferViewModel.kt`:
1. Remove the `EngageEventBus` constructor parameter
2. Remove the `import com.actualplatform.engage.legacy.EngageEvent`
3. Remove the `import com.actualplatform.engage.legacy.EngageEventBus`
4. Add a `SharedFlow` for scan requests:

```kotlin
private val _scanRequested = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
val scanRequested: SharedFlow<Unit> = _scanRequested
```

5. In `prepareScanSession()` (around line 167), replace `eventBus.emit(EngageEvent.CameraScan)` with `_scanRequested.tryEmit(Unit)`

- [ ] **Step 2: Update OfferModule to remove EngageEventBus injection**

In `OfferModule.kt`, update the OfferViewModel factory to not inject EngageEventBus. The `viewModelOf(::OfferViewModel)` call should still work if the constructor parameter is removed — verify the Koin module matches the new constructor signature.

- [ ] **Step 3: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/offers/internal/ui/OfferViewModel.kt
git add engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/offers/internal/di/OfferModule.kt
git commit -m "refactor: replace EngageEventBus in OfferViewModel with SharedFlow"
```

---

### Task 5: Refactor ReceiptSummaryViewModel to remove EngageEventBus dependency

**Files:**
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/rss/internal/ui/ReceiptSummaryViewModel.kt`
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/rss/internal/di/ReceiptSummaryModule.kt`

- [ ] **Step 1: Replace event bus emissions with callback-based reward reporting**

In `ReceiptSummaryViewModel.kt`:
1. Remove the `EngageEventBus` constructor parameter
2. Remove `EngageEvent` and `EngageEventBus` imports
3. Add a `SharedFlow` for rewards:

```kotlin
private val _rewardEarned = MutableSharedFlow<Reward>(extraBufferCapacity = 1)
val rewardEarned: SharedFlow<Reward> = _rewardEarned
```

4. Import `com.actualplatform.engage.Reward`
5. At line 103, replace `eventBus.emit(EngageEvent.RewardEarned.Boost(...))` with `_rewardEarned.tryEmit(Reward.Boost(pointsToAdd.toFloat()))`
6. At line 125, replace `eventBus.emit(EngageEvent.RewardEarned.Promo(...))` with `_rewardEarned.tryEmit(Reward.Promotion(pointsToAdd.toFloat()))`

- [ ] **Step 2: Replace EngageConfiguration with EngageClient for currency calculations**

In `ReceiptSummaryViewModel.kt`:
1. Replace the `configuration: EngageConfiguration` constructor parameter with `client: EngageClient`
2. Replace `configuration.currency.calculate(...)` calls with a helper that reads from client:

```kotlin
private fun calculateReward(baseAmount: Double): Int {
    val percentage = client.rewardPayoutPercentage
    return (baseAmount * percentage / 100.0).toInt()
}
```

3. Update the two reward calculation sites (lines ~97 and ~119) to use `calculateReward()` instead of `configuration.currency.calculate()`

- [ ] **Step 3: Update ReceiptSummaryModule**

In `ReceiptSummaryModule.kt`, update the ViewModel factory to match the new constructor — replace EngageEventBus and EngageConfiguration with EngageClient (already available via Koin as `get<EngageClient>()`).

- [ ] **Step 4: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/rss/
git commit -m "refactor: replace EngageEventBus and EngageConfiguration in ReceiptSummaryViewModel"
```

---

### Task 6: Refactor AdsLoadingViewModel to use EngageClient instead of EngageConfiguration

**Files:**
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/loading/internal/ui/AdsLoadingViewModel.kt`
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/loading/internal/di/LoadingModule.kt`

- [ ] **Step 1: Replace EngageConfiguration parameter**

In `AdsLoadingViewModel.kt`:
1. Replace `configuration: EngageConfiguration` constructor parameter with reading from `EngageClient` (already injected or inject via Koin)
2. At line 149, where `configuration` is passed to `qualifiedPromotionsUseCase.promotions()`, create a `RewardCurrency` from client properties:

```kotlin
val currency = RewardCurrency(
    name = client.rewardCurrencyName,
    userPayoutPercentage = client.rewardPayoutPercentage,
    icon = client.rewardIcon,
)
```

Or better: update `QualifiedPromotionsUseCase.promotions()` to accept `RewardCurrency` directly instead of `EngageConfiguration`.

- [ ] **Step 2: Update QualifiedPromotionsUseCase if needed**

In `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/loading/internal/domain/QualifiedPromotionsUseCase.kt`:
If `promotions()` accepts `EngageConfiguration`, change it to accept `RewardCurrency` directly (since `EngageConfiguration` is just a wrapper around `RewardCurrency`).

- [ ] **Step 3: Update LoadingModule**

In `LoadingModule.kt`, update the AdsLoadingViewModel factory to match the new constructor.

- [ ] **Step 4: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/loading/
git commit -m "refactor: replace EngageConfiguration with EngageClient in AdsLoadingViewModel"
```

---

### Task 7: Update public screen composables to read config from EngageClient

**Files:**
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/AdsLoadingScreen.kt` (moved location)
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/ReceiptSummaryScreen.kt` (moved location)
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/OfferScreen.kt`

- [ ] **Step 1: Update AdsLoadingScreen**

Remove the `configuration: EngageConfiguration` parameter. Remove the `CompositionLocalProvider(LocalEngageConfiguration provides configuration)` wrapper. The internal composables now read config from EngageClient via Koin.

- [ ] **Step 2: Update ReceiptSummaryScreen**

Remove the `configuration: EngageConfiguration` parameter. Remove the `CompositionLocalProvider(LocalEngageConfiguration provides configuration)` wrapper.

- [ ] **Step 3: Update OfferScreen**

Remove the `CompositionLocalProvider(LocalEngageConfiguration provides EngageConfiguration.default())` wrapper if present.

- [ ] **Step 4: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add engage/src/commonMain/kotlin/com/actualplatform/engage/AdsLoadingScreen.kt
git add engage/src/commonMain/kotlin/com/actualplatform/engage/ReceiptSummaryScreen.kt
git add engage/src/commonMain/kotlin/com/actualplatform/engage/OfferScreen.kt
git commit -m "refactor: remove EngageConfiguration parameter from public screen composables"
```

---

### Task 8: Delete EngageEventBus, EngageEvent, and EngageConfiguration

**Files:**
- Delete: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/EngageEventBus.kt`
- Delete: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/EngageEvent.kt`
- Delete: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/EngageConfiguration.kt`
- Modify: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/di/LegacyEngageModule.kt` (remove EngageEventBus singleton)

- [ ] **Step 1: Remove EngageEventBus from LegacyEngageModule**

In `LegacyEngageModule.kt` line 15, remove `single { EngageEventBus.get() }`.

- [ ] **Step 2: Delete the three files**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
rm engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/EngageEventBus.kt
rm engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/EngageEvent.kt
rm engage/src/commonMain/kotlin/com/actualplatform/engage/legacy/EngageConfiguration.kt
```

- [ ] **Step 3: Fix any remaining references**

Search the codebase for any remaining imports of the deleted types and remove/replace them. Key files to check:
- Any internal composables that reference `LocalEngageConfiguration`
- Any ViewModels that still import `EngageEvent` or `EngageEventBus`

- [ ] **Step 4: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add -A
git commit -m "refactor: delete EngageEventBus, EngageEvent, and EngageConfiguration"
```

---

## Chunk 3: Create OffersNavigation Composable

### Task 9: Create OffersNavigation composable in the SDK

This is the core task. `OffersNavigation` absorbs the navigation logic from `OffersNavDisplay` (currently in the host app) and replaces event bus subscriptions with direct callback invocations.

**Files:**
- Create: `engage-kmp/engage/src/commonMain/kotlin/com/actualplatform/engage/OffersNavigation.kt`

Reference: `windfall-android-sdk/blinkreceipt-app/src/main/java/com/blinkreceipt/development/ui/engage/OffersNavDisplay.kt` (lines 1-219)

- [ ] **Step 1: Create OffersNavigation.kt**

```kotlin
package com.actualplatform.engage

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.NavDisplay
import com.actualplatform.engage.legacy.internal.ReceiptSessionStateHolder
import com.actualplatform.engage.legacy.internal.designsystem.theme.EngageTheme
import com.actualplatform.engage.models.ScanResults
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration

/**
 * Primary composable for the Engage offers flow.
 *
 * Manages the complete offer wall → scan → loading → receipt summary navigation internally.
 * The host app provides camera scanning via the [onScanReceipt] suspend callback and receives
 * lifecycle/reward events through typed callbacks.
 *
 * @param modifier Standard Compose modifier.
 * @param offerStyle Customization for the offer wall UI.
 * @param onScanReceipt Suspend callback invoked when the user taps scan. Host app launches camera,
 *   maps results to [ScanResults], and returns them. Return null if cancelled.
 * @param onRewardEarned Called when the user earns a reward.
 * @param onContinue Called when the user taps continue on the receipt summary.
 * @param onError Called on error with error code and optional message.
 * @param onDismiss Called when the user dismisses the flow.
 */
@Composable
fun OffersNavigation(
    modifier: Modifier = Modifier,
    offerStyle: OfferStyle = OfferStyle.default(),
    onScanReceipt: suspend () -> ScanResults?,
    onRewardEarned: (Reward) -> Unit = {},
    onContinue: () -> Unit = {},
    onError: (Int, String?) -> Unit = {},
    onDismiss: () -> Unit,
) {
    // Internal implementation — see Step 2 for full body
}
```

- [ ] **Step 2: Implement the navigation body**

The body of `OffersNavigation` should:

1. Set up `KoinApplication` with `legacyEngageModule` wrapping the content
2. Create `rememberNavBackStack` with `OffersNavEntry.OfferWall` as initial
3. Get `ReceiptSessionStateHolder` via `koinInject()`
4. Set up `NavDisplay` with entry provider for three routes (no CameraScan route):

**OfferWall entry:**
- Render `OfferScreen` with `offerStyle` parameters and `onBack = onDismiss`
- Observe the OfferViewModel's `scanRequested` flow
- When scan requested: launch coroutine that calls `onScanReceipt()`, stores result in saved state, navigates to AdsLoading if non-null

**AdsLoading entry:**
- Retrieve scan results from saved state
- Render `AdsLoadingScreen` with scan results
- `onFinished` → navigate to ReceiptSummary
- `onRetryScan` → pop back to OfferWall, trigger new scan
- `onCancelled` → pop back to OfferWall

**ReceiptSummary entry:**
- Render `ReceiptSummaryScreen`
- `onContinue` → call the `onContinue` callback parameter
- `onBackClick` → pop back to OfferWall (clear session)
- `onReceiptIconClick` → handled internally
- Observe ReceiptSummaryViewModel's `rewardEarned` flow → forward to `onRewardEarned` callback

- [ ] **Step 3: Handle navigation entry sealed class**

Create the internal navigation entry type (can be in the same file or a separate internal file):

```kotlin
internal sealed interface OffersNavEntry {
    data object OfferWall : OffersNavEntry
    data class AdsLoading(val scanResults: ScanResults) : OffersNavEntry
    data object ReceiptSummary : OffersNavEntry
}
```

Note: Check the existing `OffersNavEntry` in the host app's `OffersNavDisplay.kt` for the exact pattern used with Navigation3.

- [ ] **Step 4: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:compileKotlinMetadata`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add engage/src/commonMain/kotlin/com/actualplatform/engage/OffersNavigation.kt
git commit -m "feat: add OffersNavigation composable as primary public API"
```

---

## Chunk 4: Host App Migration

### Task 10: Simplify OffersActivity to use OffersNavigation

**Files:**
- Modify: `windfall-android-sdk/blinkreceipt-app/src/main/java/com/blinkreceipt/development/ui/OffersActivity.kt`
- Keep: `windfall-android-sdk/blinkreceipt-app/src/main/java/com/blinkreceipt/development/ui/engage/mapper/ScanResultsMapper.kt`

- [ ] **Step 1: Rewrite OffersActivity**

Replace the current content with the Channel bridge pattern:

```kotlin
package com.blinkreceipt.development.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import com.actualplatform.engage.OffersNavigation
import com.actualplatform.engage.Reward
import com.blinkreceipt.development.ui.engage.mapper.toEngageModel
import com.microblink.camera.ui.CameraRecognizerResults
import com.microblink.core.ScanOptions
import kotlinx.coroutines.channels.Channel

internal class OffersActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scanResultChannel = remember { Channel<com.actualplatform.engage.models.ScanResults?>(1) }
            val launcher = rememberLauncherForActivityResult(
                contract = /* the camera activity result contract */
            ) { result ->
                val mapped = when (result) {
                    is CameraRecognizerResults.Success -> result.results?.toEngageModel()
                    else -> null
                }
                scanResultChannel.trySend(mapped)
            }

            MaterialTheme {
                OffersNavigation(
                    onScanReceipt = {
                        launcher.launch(createScanOptions())
                        scanResultChannel.receive()
                    },
                    onRewardEarned = { reward ->
                        // log rewards
                    },
                    onContinue = { finish() },
                    onDismiss = { finish() },
                )
            }
        }
    }
}
```

Note: The exact `ActivityResultContract` and `createScanOptions()` / `createCameraCharacteristics()` calls need to match the existing host app patterns. Reference `OffersNavDisplay.kt` lines 118-148 for how the camera is currently launched, and `MainActivity.java` lines 804-843 for `createScanOptions()`.

- [ ] **Step 2: Remove ScanOptions and CameraCharacteristics from intent extras**

Since these are now created inside the activity (not passed from MainActivity), remove the companion object constants and intent extras handling.

- [ ] **Step 3: Update MainActivity.java**

In `onOffersClicked()` (lines 671-694), simplify the intent to not pass scan options:

```java
Intent intent = new Intent(MainActivity.this, OffersActivity.class);
startActivity(intent);
```

Move `createScanOptions()` and `createCameraCharacteristics()` to OffersActivity if they aren't already there, or keep them in MainActivity if OffersActivity can access them.

- [ ] **Step 4: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/windfall-android-sdk && ./gradlew :blinkreceipt-app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/windfall-android-sdk
git add blinkreceipt-app/src/main/java/com/blinkreceipt/development/ui/OffersActivity.kt
git add blinkreceipt-app/src/main/java/com/blinkreceipt/development/ui/MainActivity.java
git commit -m "refactor: simplify OffersActivity to use OffersNavigation"
```

---

### Task 11: Delete OffersNavDisplay from host app

**Files:**
- Delete: `windfall-android-sdk/blinkreceipt-app/src/main/java/com/blinkreceipt/development/ui/engage/OffersNavDisplay.kt`

- [ ] **Step 1: Delete OffersNavDisplay.kt**

```bash
cd /Users/pequots/AndroidStudioProjects/windfall-android-sdk
rm blinkreceipt-app/src/main/java/com/blinkreceipt/development/ui/engage/OffersNavDisplay.kt
```

- [ ] **Step 2: Remove any remaining imports of OffersNavDisplay**

Search for `OffersNavDisplay` in the windfall-android-sdk codebase and remove references.

- [ ] **Step 3: Verify it compiles**

Run: `cd /Users/pequots/AndroidStudioProjects/windfall-android-sdk && ./gradlew :blinkreceipt-app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/windfall-android-sdk
git add -A
git commit -m "refactor: delete OffersNavDisplay — logic moved to SDK OffersNavigation"
```

---

## Chunk 5: Cleanup and Verification

### Task 12: Clean up internal references to deleted types

**Files:**
- Modify: various files in `engage-kmp` that may still reference `LocalEngageConfiguration`, `EngageConfiguration`, or old package paths

- [ ] **Step 1: Search for stale references**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
grep -r "EngageConfiguration" engage/src/ --include="*.kt" -l
grep -r "LocalEngageConfiguration" engage/src/ --include="*.kt" -l
grep -r "EngageEventBus" engage/src/ --include="*.kt" -l
grep -r "EngageEvent" engage/src/ --include="*.kt" -l
grep -r "com.actualplatform.engage.legacy.offers.OfferStyle" engage/src/ --include="*.kt" -l
grep -r "com.actualplatform.engage.legacy.RewardCurrency" engage/src/ --include="*.kt" -l
```

- [ ] **Step 2: Fix any remaining references**

For each file found, update imports and usages:
- `EngageConfiguration` → read from `EngageClient` properties
- `LocalEngageConfiguration` → remove, inject via Koin
- Internal composables that read `LocalEngageConfiguration.current` → replace with Koin injection of `EngageClient`

- [ ] **Step 3: Full build verification**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew :engage:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
cd /Users/pequots/AndroidStudioProjects/engage-kmp
git add -A
git commit -m "chore: clean up stale references to removed types"
```

---

### Task 13: End-to-end verification

- [ ] **Step 1: Build engage-kmp SDK**

Run: `cd /Users/pequots/AndroidStudioProjects/engage-kmp && ./gradlew build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Build windfall-android-sdk host app**

Run: `cd /Users/pequots/AndroidStudioProjects/windfall-android-sdk && ./gradlew :blinkreceipt-app:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Verify public API surface**

Check that only intended types are public in `com.actualplatform.engage`:
- `EngageClient`
- `OffersNavigation`
- `Reward` (with `Promotion` and `Boost`)
- `OfferScreen`
- `AdsLoadingScreen`
- `ReceiptSummaryScreen`
- `OfferStyle`
- `RewardCurrency`
- `NetworkAnalyzer`
- `Results`
- `AppSessionState`

- [ ] **Step 4: Run on device/emulator**

Install the demo app and verify the offers flow works end-to-end:
1. Launch from MainActivity → OffersActivity
2. Offer wall loads
3. Tap scan → camera launches via callback
4. Scan receipt → results mapped → ads loading screen
5. Ads loading completes → receipt summary screen
6. Back goes to offer wall
7. Continue dismisses the flow

- [ ] **Step 5: Commit any final fixes**

```bash
git add -A
git commit -m "chore: end-to-end verification complete"
```
