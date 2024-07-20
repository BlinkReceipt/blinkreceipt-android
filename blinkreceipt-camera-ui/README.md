# Blink Camera UI SDK

## Table Of Contents

* [Intro](#intro)
* [Setup](#setup)
* [First Scan](#start_camera_scan)
* [Configuring Your Scan](#configure)
* [Understand Results](#results)
* [Advance Options](#advance-options)

## <a name=intro></a> Intro

The Blink Camera UI SDK is a wrapper implementation of the existing `RecognizerView`. This sdk provides a plug and play solution for those who wish to leverage a prebaked implementation of the Recgonizer Experience. Though the UI is predefined, there are many options available that enable you to make this experience uniquely yours!

## <a name=setup></a> The Setup
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

     implementation("com.microblink.blinkreceipt:blinkreceipt-camera-ui")
}
```

Please follow the [Project Integration and Initialization](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-project-integration-and-initialization), [R8/Proguard](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#r8--proguard), and the application class/manifest step in the [Scanning Your First Receipt](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-scanning-your-first-receipt) sections to properly add and initialize recognizer sdk.

## <a name=start_camera_scan></a> How to start a Camera Scan

There are 2 ways to start a camera scan.

### Activity

The Activity approach utilizes the common `startActivityForResults` approach in Android. However, this sdk utilizes the new activity for results api, known as [Activity Contracts](https://developer.android.com/training/basics/intents/result#custom). You have to register your `launcher` at the class level. **NOT WITHIN A FUNCTION IN YOUR ACTIVITY/FRAGMENT**

```java
    private final ActivityResultLauncher<CameraRecognizerOptions> launcher = registerForActivityResult(new CameraRecognizerContract(), result -> {
       //Parsing Results
       ...
    });
```

```java

    private void startCameraScan() {
        launcher.launch(auncher.launch(new CameraRecognizerOptions.Builder().build());
    }

```

The launcher provides a method `launch` where you pass in the argument `CameraRecognizerOptions`. There are default values set as part of the builder pattern, so for a generic experience you can pass in the example code above.

Upon finishing a scan the `results` block previously defined when defining the `launcher` will be invoked with the results passed back from the scan session. We will further explore the results and how to parse them later.


### Fragment

The fragment approach is also a typical implementation of the Android Fragment. The Fragment must be instantiated via a builder. If it is not then, it will not be able to configure itself and will crash the app. Similar to the activity implementation, this too takes in the arguments of `ScanOptions` and `CameraCharacteristics`.

```java

    private void observeFragmentResultScanState() {
        supportFragmentManager
            .setFragmentResultListener(
                CameraRecognizerFragment.SCAN_SESSION_RESULTS_KEY,
                this
            ) { _, bundle ->
                CameraRecognizerResults scanResult = bundle.getParcelable<CameraRecognizerResults>(CameraRecognizerFragment.SCAN_RESULTS_KEY);

                // Parse results

            }
    }

    private void commitRecognizerUiFragment() {
        supportFragmentManager.beginTransaction()
                .add(
                    R.id.nav_host_fragment,
                    new CameraRecognizerFragment.Builder()
                        .options(new ScanOptions.Builder().build())
                        .cameraCharacteristics(new CameraCharacteristics.Builder().build())
                        .build(),
                    FRAGMENT_TAG
                )
                .commitNowAllowingStateLoss()
    }

```
Your host activity or host fragment, is able to listen to the results of the scan session via a FragmentResultListener. This is an android implementation, that allows users to commmunicate between fragments through a layer of abstraction. The method takes in a `KEY` which indicates the results you are listening for. In our case, you want to listen for the `CameraRecognizerFragment.SCAN_SESSION_RESULTS_KEY` key. The callback function passes a `Bundle`, and to unwrap the results in the bundle you must call `bundle.getParcelable<CameraRecognizerResults>(CameraRecognizerFragment.SCAN_RESULTS_KEY)`. This will provide you the results of the scan session.

##  <a name=configure></a> Configuring Your Scan

### CameraCharacteristics
As previously mentioned, the sdk allows you to define many things within the UI implementation, to make the experience uniquely yours. This is done by providing the `CameraCharacteristics` as part of your argument set, whether it is in the `CameraRecognizerOptions` of the activity contract or the `CameraRecognizerFragment.Builder` argument.

`CameraCharacteristics` is new to the sdk. This provides an easy to use controller for the ux/ui of the scanning experience. The following is a list of configurable options and how to use them.

```java
   previousFrameOverlayPercentage(decimalOverlap)
```

Part of the user experience when capturing a frame is the ability to "save" that frame in the context of the session. Upon confirming a photo, the image will animate towards the top of the screen. The intended experience is to have a small section of the captured frame remain on the screen at the top. This "sliver" is intended to help the user line up their next frame, if it is a long receipt. This configuration takes in a float and that float must be between 0-1.0f. A value of 0 means that the image will 100% animate off the screen and a value of 1 means it will go nowhere when confirming. Neither are ideal. A default value of 0.10f is provided as a default.

```java
    cameraPermission(true)
```

Since this experience does use the camera of hardware devices; in certain versions of android we must request permission at runtime. If you wish for the sdk to handle the permission asking, then simply turn this value to `true`. If you wish to handle asking permissions yourself, then you can set it to `false`.

```java
    style(R.style.BlinkRecognizerStyle)
```

The UI implementation is an Android Fragment, and it is made up of custom views. Since you do not have direct access to these components, this configuration is how you define your own theme. The base theme extends Material theme. So to take full advantage, you can create your own theme as defined in your styles.xml and have it extend BlinkRecognizerStyle. There you can override a variety of configurations that suit your need. Read on for more details.

```java
    .scanCharacteristics(
        new ScanCharacteristics.Builder()
            .date(true)
            .merchant(true)
            .total(true)
            .build())
```

Part of the new scan experience is the ability to determine get realtime feedback on receipt data, while it's being parsed. You simply define which receipt properties you wish to provide feedback to the user about and simply set the value as `true`. If you define no characteristics, then there will be no feedback, but the scan session will still grab those properties if found.

```java
    .tooltipCharacteristics(
        new TooltipCharacteristics.Builder()
            .displayTooltips(true)
            // After taking a photo, this tooltip indicates where the user should orientate their camera to capture next frame
            .maxFramesShowAlignHint(tooltipNumber)
            // This is the initial hint when a user first sees the ui.
            .maxFramesMultiplePhotosHint(tooltipNumber)
            // This is the hint displayed after a user snaps a photo, and the captured image is being presented for confirmation.
            .maxFramesLongReceiptAddPhotosHint(tooltipNumber)
            .build())
```

To help users as much as possible, we implemented a series of tooltips. These are temporary ui indicators that help guide a user through their scan experience. We offer the ability to determine how often each of the several tooltips should occur. A brief description is provided above for each tooltip.



### ScanOptions

The scan options has been a staple of the Blink Receipt SDK since it's inception. It defines the configuration for the ocr component.To learn more about Scan Options please refer to our existing documentation located [here](https://github.com/BlinkReceipt/blinkreceipt-android/tree/master/blinkreceipt-recognizer#-scanning-your-first-receipt).


## <a name=results></a> Understanding Your Results

Once a scan is completed a user can select the `finish` button, indicated by the check mark button on the lower right, or the cancel button, indicated by the `X` button on the top left. In either scenario, a result object is created. This result object is of type `CameraRecognizerResults`. You can think of it as an abstract class with 3 distinct implementations.

1. Success
The Success class indicates a successful scan with `ScanResults` and `Media`. The former contains information about your scan, and the latter, images captured as part of the scan.

2. Exception
The Exception class indicates there was some sort of unrecoverable exception that occurred while the user was in the scan experience. There is a `Throwable` property, which will give you more clues as to why this has occurred.

3. Cancelled
The Cancelled class indicates a user decided to cancel their session, by clicking on the `X` button in the top left or the android back gesture common on phones.

When receiving the results it's imporant to check which implementation the `CameraRecognizerResults` instance is.

```java
        if (result instanceof CameraRecognizerResults.Success) {
            CameraRecognizerResults.Success scanResults = ((CameraRecognizerResults.Success) result);

           // navigate user to a screen where results can be displayed
        } else if (result instanceof CameraRecognizerResults.Exception) {
            // Display some error messaging, and help user recover
            Log.e(((CameraRecognizerResults.Exception)result).exception());
        } else {
            // Cancelled case, no action
        }

```

## <a name=advance-options></a> Advance Options

In the previous section we mentioned how users could define their own theme and override many of the ui characteristics within the scan experience. If you wish to define your own theme, without inheriting from the default `BlinkRecognizerStyle` you may do so. However, it is important you define all the following themes. If you do not, then the application will crash as it attempts to search for the following items. These options control such things like button colors, tap states, iconography, etc... Feel free to change them as you see fit.

```xml
    <!-- Style that defines the entire scan experience -->
    <style name="BlinkRecognizerStyle" parent="Theme.MaterialComponents.Light.NoActionBar">
        <item name="colorPrimary">@color/blink_blue</item>
        <item name="colorPrimaryVariant">@color/blink_blue_tapped</item>
        <item name="colorSecondary">@color/light_grey</item>
        <item name="colorSecondaryVariant">@color/dark_grey</item>
        <item name="colorOnPrimary">@android:color/white</item>
        <!-- Background of buttons cancel and torch-->
        <item name="secondary_button_background">@drawable/default_secondary_button_background</item>
        <!-- Background of buttons retake and finish-->
        <item name="secondary_action_button_background">@drawable/tooltip_secondary_action_button_selector</item>
        <!-- Define the background of the primary buttons on the center of the screen-->
        <item name="capture_button_background">@drawable/primary_secondary_color_selector</item>
        <item name="confirm_action_button_background">@drawable/primary_secondary_color_selector</item>
        <item name="viewport_bounds_color">@color/see_through_black</item>
        <item name="viewport_bounds_color_captured">@color/black</item>
        <item name="capture_photo_icon_color">@android:color/white</item>
        <item name="captured_photo_icon_color">@android:color/white</item>
        <item name="finish_scan_icon_color">@android:color/white</item>
        <item name="retake_photo_icon_color">@android:color/white</item>
        <item name="torch_icon_color">@android:color/white</item>
        <item name="exit_icon_color">@android:color/white</item>
        <item name="tooltip_style">@style/BlinkRecognizerStyle.Tooltip</item>
        <item name="tooltip_text_color">@android:color/white</item>
        <item name="guideline_hint_view_background">?attr/colorPrimary</item>
        <item name="progress_bar_color">?attr/colorPrimary</item>
    </style>

    <!-- Define the style of the tooltips that appear when defined as part of CameraCharacteristics-->
    <style name="BlinkRecognizerStyle.Tooltip">
        <item name="tooltip_point_height">8dp</item>
        <item name="tooltip_box_radius">6dp</item>
        <item name="tooltip_color">?attr/colorPrimary</item>
        <item name="tooltip_text_color">?attr/colorOnPrimary</item>
        <item name="tooltip_direction">none</item>
        <item name="tooltip_placement">start</item>
    </style>
```
