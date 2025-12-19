# Camera UI + Jetpack Compose Integration Guide

This guide provides comprehensive instructions for integrating the BlinkReceipt Camera UI into a Jetpack Compose-based Android application. It covers two primary integration strategies: utilizing the `RecognizerView` for a custom scanning UI and implementing the out-of-the-box `Blink Camera Ui` for a complete, pre-built camera experience.

## Custom Scan using `RecognizerView`
To integrate the `RecognizerView` with Jetpack Compose, use the `AndroidView` composable. This allows you to embed a traditional Android View within your Compose UI. It's important to manage the lifecycle of the `RecognizerView` by creating it in the `factory` lambda of `AndroidView` and handling its lifecycle events.

Here is an example of how to set up the `RecognizerView` in a Composable function:
```kotlin
@Composable
fun RecognizerViewComposable(
    modifier: Modifier,
    // ...
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  // Use remember to prevent the RecognizerView from being re-created on every recomposition.
  val recognizerView = remember {
      RecognizerView(context).apply {
        setMeteringAreas(arrayOf(RectF(0f, 0f, 1f, 1f)), true)
        initialOrientation = Orientation.ORIENTATION_PORTRAIT
        aspectMode = CameraAspectMode.ASPECT_FILL

        // Observe Recognizer Callback
        recognizerCallback(object: CameraRecognizerCallback {
          // ...
        })

        // Initialize with ScanOptions
        initialize(
          ScanOptions
            .newBuilder()
            // ...
            .build()
        )
        
        // Attach Lifecycle Owner
        lifecycle(lifecycleOwner)
      }
  }

  AndroidView(
      factory = { recognizerView },
      modifier = modifier,
      onRelease = { view ->
          view.terminate()
      }
  )
}
```

## Out-of-the-box Camera Experience using Blink Camera UI
There are two primary approaches to integrate the pre-built camera experience: embedding the `CameraRecognizerFragment` directly into your composable for a tightly integrated UI, or launching it as a separate activity using a `CameraRecognizerContract` for a more decoupled flow.

### 1. Embed `CameraRecognizerFragment` using `AndroidFragment`
This method involves embedding the `CameraRecognizerFragment` within a composable using the `AndroidFragment` composable from the `androidx.fragment:fragment-compose` library. This is an excellent choice if your app already utilizes a Fragment-based architecture or if you want to place the camera view as a component within a larger composable screen. This approach allows you to manage the camera's lifecycle and handle results directly within the composable's context.

```groovy
// build.gradle
implementation "androidx.fragment:fragment-compose:1.8.9"
```
```kotlin
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OobCameraContent(
                modifier = Modifier,
                onScanResults = { results: CameraRecognizerResults ->
                    // Process CameraRecognizerResults here...
                }
            )
        }
    }
}
```
```kotlin
@Composable
internal fun CameraRecognizerContent(
    modifier: Modifier,
    onScanResults: ((CameraRecognizerResults) -> Unit),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidFragment<CameraRecognizerFragment>(
        modifier = modifier.fillMaxSize(),
        arguments = bundleOf(
            CameraRecognizerFragment.OPTIONS to ScanOptions
                .newBuilder()
                // Define your own ScanOptions configuration here...
                .build(),
            CameraRecognizerFragment.CAMERA_CHARACTERISTICS to CameraCharacteristics.Builder()
                .scanCharacteristics(
                    ScanCharacteristics.Builder()
                        // Define your own ScanCharacteristics configuration here...
                        .build()
                )
                .tooltipCharacteristics(
                    TooltipCharacteristics.Builder()
                        // Define your own TooltipCharacteristics configuration here...
                        .build()
                )
                // Define other CameraCharacteristics configuration here...
                .build(),
        ),
    ) { fragment ->
        fragment.parentFragmentManager
            .setFragmentResultListener(
                CameraRecognizerFragment.SCAN_SESSION_RESULTS_KEY,
                lifecycleOwner,
            ) { _, bundle ->
                bundle.parcelable<CameraRecognizerResults>(CameraRecognizerFragment.SCAN_RESULTS_KEY)
                    ?.let { results ->
                        // Retrieve CameraRecognizerResults here...
                        onScanResults(results)
                    }
            }
    }
}
```


### 2. Launch as an Activity using `CameraRecognizerContract`
This approach leverages the modern Android Activity Result APIs. By using `rememberLauncherForActivityResult` with the `CameraRecognizerContract`, you can launch the camera recognizer as a separate activity and receive the results back in a callback. This method is ideal for a more decoupled architecture where the camera scanning process is a distinct step in a user flow. It simplifies state management as the camera UI is entirely separate from your calling composable.

```kotlin
@Composable
internal fun CameraRecognizerContent(
    modifier: Modifier,
    onScanResults: ((CameraRecognizerResults) -> Unit),
) {
    val launcher = rememberLauncherForActivityResult(
        CameraRecognizerContract()
    ) { results: CameraRecognizerResults ->
        onScanResults(results)
    }
    
    // Launch the camera recognizer when this composable enters the composition
    LaunchedEffect(Unit) {
        launcher.launch(
            CameraRecognizerOptions.Builder()
                .options(
                    ScanOptions
                        .newBuilder()
                        // Define your own ScanOptions configuration here...
                        .build()
                )
                .characteristics(
                    CameraCharacteristics.Builder()
                        // Define other CameraCharacteristics configuration here...
                        .scanCharacteristics(
                            ScanCharacteristics.Builder()
                                // Define your own ScanCharacteristics configuration here...
                                .build()
                        )
                        .tooltipCharacteristics(
                            TooltipCharacteristics.Builder()
                                // Define your own TooltipCharacteristics configuration here...
                                .build()
                        )
                        .build()
                )
                .build()
        )
    }
}
```
