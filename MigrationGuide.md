# Migration Guide: Upgrading to BlinkReceipt v2.1.0

This guide helps you migrate your application from the existing BlinkReceipt SDK to the new BlinkReceipt v2.1.0 SDK. The updated SDK improves performance, reliability, and device compatibility by leveraging Google's native CameraX framework, while maintaining existing SDK functionality.

## Key differences

- **blinkreceipt-barcode is removed**: The SDK is removed and we recommend swtiching to using Google ML Kit for the same functionality. 
- **blinkreceipt-camera**: The SDK now uses CameraX to provide frames and capture images.
- **blinkreceipt-recognizer**: Many legacy Camera related classes are now unused and deleted, the most important changes are outlined in #Migration-guide. 


## Migration guide

### Update dependencies 

Update dependencies to use the latest BlinkReceipt SDK

```groovy
dependencies {
     implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:2.1.0"))

     implementation("com.microblink.blinkreceipt:blinkreceipt-core")
     implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
}
```

### BlinkReceipt Camera

It is no longer possible to set desired resolution, instead 1080p resolution is used for all frames and captured images.

### BlinkReceipt UI Changes

If you're using BlinkReceipt built-in UI (`blinkreceipt-camera-ui`) there are no updates required.

### Custom UI Changes

If you're providing your own custom UI on top of the `RecognizerView` there are a couple changes required.

#### Remove forwarding lifecycle callbacks, use `lifecycle()` instead

Manual lifecycle methods `create()`, `start()`, `resume()`, `pause()`, `stop()`, `destroy()` are removed. Use `lifecycle()` instead.

```java
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    recognizerView.lifecycle(this);
}
```

#### Update `takePicture()` call

`RecognizerView.takePicture()` no longer takes `CameraCaptureListener` as argument. Instead, the listener should be set prior to calling `takePicture()` via the following method: `RecognizerView.cameraCaptureListener(CameraCaptureListener)`

```java
public class MyCameraActivity extends AppCompatActivity implements CameraCaptureListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this
        recognizerView.cameraCaptureListener(this);

        (...)

        button.setOnClickListener(v -> recognizerView.takePicture());
    }

    @Override
    public void onCaptured(@NonNull BitmapResult bitmapResult) {
        // Add CameraCaptureListener.onCaptured implementation        
    }

    @Override
    public void onException(@NonNull Throwable throwable) {
        // Add CameraCaptureListener.onException implementation        
    }
}
```

#### Update Frame Results usage

The `CameraFrameResult` no longer has `Bitmap frame1080p()`. Instead, since only 1080p resolution is used, the `Bitmap` is returned via existing method `Bitmap bitmap();` 

For the same reason, it is no longer possible to call `TakePictureResult.high()`, and you should just call `TakePictureResult.bitmap()` instead.

Also, it is no longer possible to set `Bitmap` in `CameraFrameResult`, as the object will be automatically populated with the `Bitmap` from the SDK.

#### Update any other deleted methods or classes

The following methods are no longer available. They are mostly related to the old Camera framework and are now not required with the CameraX. 

- `BlinkDeviceInfo`
- `CameraOptions`
- `Camera1Frame`, `Camera2Frame`
- `CameraRecognizerFactory`
- `CameraUtil`
- `SizeCompat`
- `FrameHandler` and its implementations
- `SimpleCameraEvents`
- `SimpleCameraRecognizerCallback`
- `CameraRecognizerCallback.onPreviewStopped()`
- `TakePictureInteceptor`
- `RecognizerView`
    - `setVideoResolutionPreset()`
    - `setMeteringAreas()`
    - `setAspectMode()`
    - `setInitialOrientation()`
    - `changeConfiguration()`
    - `getCameraViewState()`
    - `onEdgeDetectionResults()`
    - `cameraEventsListener()`
- `ImageResolution`
- `ImageResolutionProcessor`
- `FrameProcessor`


