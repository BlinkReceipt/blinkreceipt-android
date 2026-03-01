# CameraX Migration Guide

This guide helps you migrate your application to **BlinkReceipt SDK 2.x.x and newer**. 

The updated SDK improves performance, reliability, and device compatibility by leveraging Google’s CameraX framework, while maintaining existing SDK functionality.

## Key Differences

- **:blinkreceipt-camera**: The SDK now uses CameraX to provide frames and capture images
- **:blinkreceipt-recognizer**: Many legacy Camera-related classes are no longer used and have been removed. The most important changes are outlined below under [Breaking API Changes](##breaking-api-changes)

## Prerequisites

Before you begin, please ensure you have the following:

1. Update **Android Studio** to the latest stable version.
2. Update the **Android Gradle Plugin** to the latest version.
3. Update your SDK dependencies in `build.gradle` or `build.gradle.kts`:
    ```groovy
    // build.gradle
    dependencies {
        implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:2.0.0"))
        
        implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
        implementation("com.microblink.blinkreceipt:blinkreceipt-camera")
        implementation("com.microblink.blinkreceipt:blinkreceipt-camera-ui")
        // ...
    }
    ```
4. Ensure your `compileSdk` is set to a recent version (e.g., **API 35 or higher**) to support the latest AndroidX libraries.

## Breaking API Changes

With the transition to CameraX, there are **minimal but important API changes**.

> ⚠️ **Please review the following carefully and update your integration as needed.**

### Camera Resolution Behavior
- When using APIs within `:blinkreceipt-camera`, it is no longer possible to set a custom camera resolution.
- All frames and captured images now use a **fixed 1080p resolution**.

---

### Out-of-the-Box Camera Experience
- If you are using the default Camera Experience (`blinkreceipt-camera-ui`), **no changes are required**.

---

### Custom UI Integrations
- If you provide your own custom UI on top of `com.microblink.RecognizerView`, the following changes apply:

#### Lifecycle Handling
- Forwarding lifecycle callbacks has been removed.
- You should now rely on standard Android lifecycle handling via `LifecycleOwner`.
- Manual lifecycle method forwarding (`create()`, `start()`, `resume()`, `pause()`, `stop()`, `destroy()`) is no longer required.
    - Example:
    ```java
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            recognizerView.lifecycle(this);
        }
    ```

---
#### Camera Capture Listener Changes
- `RecognizerView.takePicture()` no longer accepts a `CameraCaptureListener` as a parameter.
- Instead, the listener must be set **before** calling `takePicture()`.
    - Example:
      ```java
          recognizerView.cameraCaptureListener(new CameraCaptureListener() {
              @Override
              public void onCaptured(@NonNull BitmapResult bitmapResult) {
                  // Add CameraCaptureListener.onCaptured implementation        
              }
    
              @Override
              public void onException(@NonNull Throwable throwable) {
                  // Add CameraCaptureListener.onException implementation        
              }
          });
    
          // e.g., Trigger capture
          recognizerView.takePicture();
      ```

---
#### Frame Result Changes
- `com.microblink.CameraFrameResult` no longer returns a `Bitmap` via `frameResult.bitmap()`.
- Since CameraX uses a fixed 1080p resolution, the bitmap is now returned through `takePictureResult.bitmap()`.
- For the same reason, `takePictureResult.bitmap(width, height)` is no longer supported.
- You should always use `takePictureResult.bitmap()`.
- It is also no longer possible to manually set a bitmap on `CameraFrameResult`; the SDK automatically populates it.

---
### Removed APIs
The following methods and classes are no longer available. If your code references them, they should be removed.

#### Legacy Camera APIs (removed)
- `com.microblink.internal.BlinkDeviceInfo`
- `com.microblink.CameraOptions`
- `com.microblink.Camera1Frame`, `com.microblink.Camera2Frame`
- `com.microblink.CameraRecognizerFactory`
- `com.microblink.CameraUtil`
- `com.microblink.SizeCompat`
- `com.microblink.FrameHandler` and its implementations
- `com.microblink.SimpleCameraEvents`
- `com.microblink.SimpleCameraRecognizerCallback`
- `com.microblink.CameraRecognizerCallback.onPreviewStopped()`
- `com.microblink.TakePictureInterceptor`

#### `RecognizerView` methods removed
- `setVideoResolutionPreset()`
- `setMeteringAreas()`
- `setAspectMode()`
- `setInitialOrientation()`
- `changeConfiguration()`
- `getCameraViewState()`
- `onEdgeDetectionResults()`
- `cameraEventsListener()`
- `com.microblink.ImageResolution`
- `com.microblink.ImageResolutionProcessor`
- `com.microblink.FrameProcessor`

These APIs were tied to the legacy camera framework and are no longer required with CameraX.

---

## Behavioral Changes

The switch to CameraX improves overall stability and performance across a wider range of devices. 

However, you may notice the following differences:

- **Camera Preview:** The preview rendering is now handled by CameraX's `PreviewView`, which may handle aspect-ratio scaling differently than the legacy implementation.
- **Permissions:** Ensure all camera permissions required by CameraX are handled correctly. The standard `CAMERA` permission is still required.

---

## Testing Checklist

After upgrading to the new SDK version, we strongly recommend testing the following scenarios:

- ✅ Test all flows that use BlinkReceipt camera or Scan features. Confirm the camera preview starts correctly and scanning works as expected.
- ✅ Verify correct camera lifecycle behavior when the app moves between background and foreground.
- ✅ Test across a variety of devices and Android versions to confirm CameraX compatibility and behavior.

---

## Get Help

If you encounter issues during the migration or have questions about these changes, please contact our support team.
