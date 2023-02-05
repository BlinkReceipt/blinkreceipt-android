# Blink Receipt SDK

Blink Receipt SDK for Android is an SDK that enables you to easily add near real time OCR functionality to your app with the purpose of scanning receipts. With provided camera management you can easily create an app that scans receipts. You can also scan images stored as Android Bitmaps that are loaded either from gallery, network or SD card.

Using Blink Receipt in your app requires a valid license key.  After registering, you will be able to generate a license key for your app. License key is bound to package name of your app, so please make sure you enter the correct package name when asked.

See below for more information about how to integrate Blink Receipt SDK into your app.

# Table of Contents

* [Android _BlinkReceipt_ integration instructions](#intro)
* [Quick Start: Scan your first receipt](#quickStart)
  * [Understanding Results](#results)
  * [Customize Camera Scan Activity](#customizeScanActivity)
  * [Customize Scan Configuration](#customizeScanSession)
* [Recognizer View](#recognizerView)
* [Recognizer Client](#recognizerClient)
* [Adding Product Intelligence](#intelligence)
* [Adding Google Places](#google)
* [Adding Yelp](#yelp)
* [Adding Amazon](#amazon)
* [Processor Configuration Considerations](#processorConfigurations)
* [Android OS Support](#androidos)
* [Auto Configuration](#autoConfiguration)
* [Client User Id](#clientId)
* [Requirements](#requirements)

## AAR
The package contains Android Archive (AAR) that contains everything you need to use BlinkReceipt library.

## <a name=intro></a> Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

```groovy
dependencies {
  implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10"

  implementation 'androidx.appcompat:appcompat:1.2.0'

  implementation 'androidx.constraintlayout:constraintlayout:2.0.1'

  implementation 'com.squareup.okhttp3:okhttp:4.9.3'
  implementation 'com.squareup.retrofit2:retrofit:2.9.0'
  implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
  implementation 'com.squareup.retrofit2:converter-scalars:2.9.0'
  implementation 'com.squareup.okio:okio:3.0.0'

  implementation "com.google.android.gms:play-services-tasks:18.0.1"
  implementation "com.google.android.gms:play-services-auth:19.2.0"

  implementation 'com.jakewharton.timber:timber:5.0.1'

  implementation "androidx.webkit:webkit:1.4.0"

  implementation "androidx.work:work-runtime:2.6.0"
  implementation "androidx.work:work-runtime-ktx:2.6.0"

  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0"
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0"
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.0"

  implementation "androidx.core:core-ktx:1.6.0"

  implementation project( ':blinkreceipt-core' )
  implementation project( ':blinkreceipt-camera' )
}
```

## R8 / PROGUARD

Retrofit

```proguard
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
 @retrofit2.http.* <methods>;}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
```

okio

```proguard
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
```

okhttp

```proguard
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
```

## <a name="quickStart"></a> Scanning Your First Receipt
Even though there are different ways to initialize the sdk, the recommended way would be through the `AndroidManifest.xml` file. Within this file add the following configuration.

`AndroidManifest.xml`
```xml
 <meta-data
    android:name="com.microblink.LicenseKey"
    android:value="BLINK RECEIPT LICENSE KEY" />
```

The easiest way to get started scanning your first receipt would be to use the internal Scan Activity within the aar.

```java
    ScanOptions scanOptions = ScanOptions.newBuilder()
     .frameCharacteristics( FrameCharacteristics.newBuilder()
        .storeFrames( true )
        .compressionQuality( 100 )
        .externalStorage( false ) .build() )
    .logoDetection( true )
    .build();

    Bundle bundle = new Bundle();

    bundle.putParcelable( CameraScanActivity.SCAN_OPTIONS_EXTRA, scanOptions );

    Intent intent = new Intent( this, CameraScanActivity.class )
        .putExtra( CameraScanActivity.BUNDLE_EXTRA, bundle );

    startActivityForResult( intent, SCAN_RECEIPT_REQUEST );
```

The results are returned through 2 objects, which can be retrieved by getting the parcelable extras `ScanResults` and `Media`. The `ScanResults` object contain the results from the scan session.

```java
 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

     if ( requestCode == SCAN_RECEIPT_REQUEST && resultCode == Activity.RESULT_OK ) {
        ScanResults brScanResults = data.getParcelableExtra( CameraScanActivity.DATA_EXTRA );

        Media media = data.getParcelableExtra( CameraScanActivity.MEDIA_EXTRA );
    }
}
```

### <a name=customizeScanActivity></a> Customize Camera Scan Activity
The camera scan activity baked into the sdk contains numerous configurations that can be controlled via intent extras.

ScanOptions (Recommended) Extra recommended to properly scan receipt.
```java
CameraScanActivity.SCAN_OPTIONS_EXTRA;
```
Extra to have the activity be a full screen activity. `false` by default.

```java
CameraScanActivity.FULL_SCREEN_EXTRA;
```
Extra to keep screen on while scanning session in progress. `false` by default.
```java
CameraScanActivity.KEEP_SCREEN_ON_EXTRA;
```
Enable enhanced autofocus for camera during scan session. `false` by default.
```java
CameraScanActivity.ENABLE_ENHANCED_AUTO_FOCUS;
```
Alternative way to initialize SDK if not done through default method.
```java
CameraScanActivity.LICENSE_KEY_EXTRA;
```
Defines the region in which we want to scan on the frame. The properties of the RectF are defined as a percentage of the screen.
```java
CameraScanActivity.VIEW_PORT_EXTRA;
```
Video resolution preset extra for camera.
```java
CameraScanActivity.VIDEO_RESOLUTION_EXTRA;
```
Interface passed in as a parcelable extra, that will receive every recognizer's result.
```java
CameraScanActivity.CAMERA_RECOGNIZER_CALLBACK_EXTRA;
```

UI Dimens
```xml
<?xml version="1.0" encoding="utf-8"?>
 <resources>
    <dimen name="camera_scan_bottom_frame_height">80dp</dimen>
    <dimen name="camera_scan_take_picture_size">60dp</dimen>
</resources>
```
### <a name=customizeScanSession></a> Customize Scan Configuration
Want to see your captured frames? save scanned results? This extra functionality is possible through the scanOptions object. The builder pattern allows you to customize your scan session configuration.

`retailer( Retailer retailer )`: If the retailer is known the sdk can take advantage of the pre-defined text styles and other known characteristics of that retailer's receipt.

`searchTargets( List<Product> searchTargets )`: If searching for particular set of Products, those products can be passed into the scan options and consequently the scansession itself.

`storeFrames( boolean storeFrames )`: If set to true, this configuration will save the captured and confirmed frames to disk. The paths to those files will be returned within the `Media` object which is returned in the onActivityResults bundle.

`useExternalStorage( boolean useExternalStorage )`: This configuration goes hand in hand with the previous configuration. By default frames will be stored within the applications internal storage. If set to `true` the path will be set to be the external application storage. Results and access to these files will be returned the same way.

`edgeDetectionConfiguration( EdgeDetectionConfiguration configuration )`: The sdk's functionality includes edge detection. Here we can determine the edges of the receipt and therefore the content percentage of the receipt. This configurations allow for customized parameters to be set around what is acceptable criteria scanning a receipt. In the case of a low content percentage or below the defined threshold we display a helpful message to the user to let them know to move closer.

`promotionSlugs( Utility.newArrayList( new Slug( "[NAME OF SLUG]" ) ) )`: If set, this configuration will validate promotions based on the configured slugs.

`validatePromotions( true || false )`: If set to true, this configuration will validate promotions..

## <a name=results></a>Retrieving Results
The RecognizerCallback interface is the way to retrieve results and statuses on the scanning progress.
```java
public interface RecognizerCallback {
 // Called when scan results are compiled and saved images are processed.
void onRecognizerDone( @NonNull ScanResults results, Media media );

 // Called in the case there is an exception while scanning the captured frame.
void onRecognizerException(@NonNull Throwable e );

// The callback invoked whenever a step within the scanning process is returned.
void onRecognizerResultsChanged( @NonNull RecognizerResult result );
}
```

```java
public interface CameraRecognizerCallback {
    // The callback invoked if while utilizing the RecognizerView the confirm frame is called saving the image. This callback provides the location of the saved frame. void onConfirmPicture( @NonNull File file );
    // As of Android Marshmallow (API 24) Runtime permissions are required to access hardware features like the camera. This callback will be invoked if proper permissions have not been granted for camera use. void onPermissionDenied();
    //Notifying the user of any issue while using camera preview as well as when preview is started and ended. void onPreviewStarted();
    void onPreviewStopped();

    void onException( @NonNull Throwable throwable );
}
```
The RecognizerCallback also provides preliminary results.

```java
 recognizerView.preliminaryResults();
```
```java
@Override
public void onRecognizerResultsChanged(@NonNull RecognizerResult result) {
    if ( result instanceof PreliminaryResult ) {
        PreliminaryResult results = (PreliminaryResult) result;
    }
}
```

The RecognizerCallback also provides raw results.

```java
@Override
public void onRecognizerResultsChanged(@NonNull RecognizerResult result) {
    if ( result instanceof OcrRawResult ) {
        OcrRawResult ocrRawResult = (OcrRawResult) result;
    }
}
```

The RecognizerCallback also provides edge results.

```java
@Override
public void onRecognizerResultsChanged(@NonNull RecognizerResult result) {
    if ( result instanceof EdgeDetectionResult ) {
        EdgeDetectionResult edges = (EdgeDetectionResult) result;
    }
}
```

`RecognizerResult` is an interface that encapsulates any result of any step in our scanning process. When the onRecognizerResultsChanged( RecognizerResult result ) is invoked by callback listener it is important to check the type of result that it may be. We recommend doing that with a simple `instanceOf` check. There are a variety of results that can be passed through this callback.

The most important results for users is the `EdgeDetectionResults` and the `SearchTargetResults`.

The edge detection result contains edge detection information about the latest frame processed. The result object contains a `contentPercent` indicating the percentage of the frame that the receipt contained, as well as the state of the EdgeDetection. The state is a reflection of the `EdgeDetectionConfiguration` passed in through the scan options before the scan session was created. The state contains one of the following values:

`ABOVE_THRESHOLD`: The receipt in the latest frame takes up at least the minimum threshold set via the EdgeDetectionConfiguration object.

`BELOW_THRESHOLD`: The receipt in the latest frame takes up less than the minimum threshold set via the EdgeDetectionConfiguration object.

`CONSECUTIVE_ABOVE_THRESHOLD_LIMIT_REACHED`: N number of frames have consistently been at or above the minimum threshold. This value N is set via the EdgeDetectionConfiguration object.

`CONSECUTIVE_BELOW_THRESHOLD_LIMIT_REACHED`: N number of frames have consistently been below the minimum threshold. This value N is set via the EdgeDetectionConfiguration object.

The search target results contain lists of products prescribed in the ScanOptions. At least one of the products described were found in the last frame scanned, and are therefore confirmed through this result.

## <a name=recognizerView></a> RecognizerView: Provide your own UI on top of Camera View
The sdk does have an easy to use activity that can be used and customized as described above. However, the sdk has decoupled components that give you the ability to build your own ui on top of the camera view. That view is called the `RecognizerView`.  If your project wants to have a custom UI, add the `RecognizerView` to your layout for the camera portion of your UI. The RecognizerView is a view that provides a camera preview for the user as well as other capabilities for the devloper. The `RecognizerView` handles its own lifecycle, but it is required that in each callback of your activity, you forward that state to the RecognizerView.

```java
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recognizerView.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        recognizerView.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        recognizerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        recognizerView.pause();
    }

    @Override
    public void onStop() {
        super.onStop();

        recognizerView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        recognizerView.destroy();
    }
```
### Capabilities and Customizations

#### Pre Scanning Configuration
Before the RecognizerView can be used it must be initialized via the `initialize( ScanOptions scanOptions )`. Please see above for the description of the ScanOptions class.

The RecognizerView provides the ability to attach your own `RecognizerCall.class` with the `setRecognizerCallback()` method for listening to results for each step of the scanning process.

#### Scanning Capabilities
The RecognizerView is also able to capture frames, not unlike a regular camera application. This is achieved with the `takePicture( CameraCaptureListener listener )` method. The method takes in a `CameraCaptureListener.class`. This listener provides the recognizer view a callback to pass back the resulting image. The image is returned within a `BitmapResults` object. In order to access the resulting bitmap, call bitmap(). The RecognizerView will create a copy of this bitmap, so you are free to manipulate, display, clean it up, etc... however you feel fit. Once the user confirms the picture you should call `confirmPicture( @NonNull BitmapResult results )`.

In addition to the take picture functionality the `RecognizerView` provide the ability to write the frame to disk and have the set of captured frames returned to you at the end of the scan session via the `Media` object in the `onRecognizerDone()` call back a part of the `RecognizerCallback.class`.

You can also cancel your current scan if it is taking longer than normal to retrieve any result callback, but it is recommened to set an appropriate Timeout within your scanOptions object so that it may be resolved internally.

Terminating your scan via `terminate()` will end your session, resetting the parser and internal result calculating mechanism.

#### Finishing the Scan
When you wish to finish your scan session call `finishedScanning()`. This will begin the session ending process where results will be compiled and finalized. Results are delivered via the `onRecognizerDone( ScanResults results, Media media )` within the interface RecognizerCallback.

*Note*
All RecognizerCallback methods are executed on the main thread.

## <a name=recognizerClient></a>Recognizer Client
If you wish to enable users to provide their own receipt image, instead of providing them with a camera interface, then you can opt out of using the `RecognizerClient` instead of the `RecognizerView`. This component provides a non-ui interface to the recognition mechanisms under the covers of the Recognizer View. It is simple to use and requires minimal setup.

#### Initialization And Running Scans
The `RecognizerClient` can be instantiated with its primary constructor which takes in  a `context`. Once the client is instantiated, it is now ready for use. To use the `RecognizerClient` call the `recognize(ScanOptions options, RecognizerCallback listener, Bitmap... bitmaps)` function. The scan options you define is the exact same object you define when initializing the `RecognizerView`. The `RecognizerCallback` is an interface which acts as a listener for the client when images are going through processing. The `onRecognizerDone` function is the indicator that the images passed in have been successfully processed and the scan session has completed. You will notice this is the same interface used in the `RecognizerView` implementation.

**NOTE this feature must be enabled through your license in order to use. If your application is not allowed to use this api, then onRecognizerException will immediately trigger informing you of your missing permissions.**

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeClient();

        // fetch bitmaps
        sendBitmapsForScanning()
    }

    private void initializeClient() {
        client = new RecognizerClient(this);
    }

    private void sendBitmapsForScanning() {
        ScanOptions options = ScanOptions.newBuilder()
                .build();

        client.recognize(options, new RecognizerCallback() {
            @Override
            public void onRecognizerDone(@NonNull ScanResults scanResults, @NonNull Media media) {
                Toast.makeText(MainActivity.this, "Results: " + scanResults.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecognizerException(@NonNull Throwable throwable) {
                Toast.makeText(MainActivity.this, "Exception: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecognizerResultsChanged(@NonNull RecognizerResult recognizerResult) {

            }
        }, bitmaps);
    }
}
```
#### Image Orientation [OPTIONAL]
Now image recognition does require image orientation in order to get the most accurate results. Though this field is not required there is an overloaded `recognize` function that allows you to pass in the orientation of the images you are passing. There are 4 orientations to choose from.
 _________
T         |
O         |   CameraOrientation.ORIENTATION_LANDSCAPE_LEFT
P_________|

| TOP |
|     |       CameraOrientation.ORIENTATION_PORTRAIT
|_____|

 _________
|         T
|         O   CameraOrientation.ORIENTATION_LANDSCAPE_RIGHT
|_________P

_______
|     |
|     |       CameraOrientation.ORIENTATION_PORTRAIT_UPSIDE_DOWN
| TOP |

If you give the user the ability to rotate images or define the orientation, then we can ensure the most accurate results. If no orientation is provided the sdk will make a best guess as to what the orientation is based on the Bitmap properties.


## <a name=intelligence></a>Product Intelligence
If you wish to include product intelligence functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```xml
 <meta-data android:name="com.microblink.ProductIntelligence" android:value="PRODUCT INTELLIGENCE KEY" />
```

## <a name=google></a>Google Places
If you wish to include Google Places functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```xml
 <meta-data android:name="com.microblink.GooglePlacesKey" android:value="GOOGLE PLACES KEY"/>
```

## <a name=yelp></a>Yelp
If you wish to include Yelp functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```xml
 <meta-data android:name="com.microblink.YelpKey" android:value="YELP KEY"/>
```

## <a name=clientId></a>Client User Id
If you wish to include your client user id within your project add your client user id key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```xml
 <meta-data android:name="com.microblink.ClientUserId" android:value="CLIENT USER ID"/>
```

## <a name=amazon></a>Amazon
If you wish to include Amazon functionality within your project. Note: Amazon functionality targets KitKat and above.

`Credentials`
```java
AmazonManager.getInstance( this ).credentials( AmazonCredentials( "AMAZON_EMAIL", "AMAZON_PASSWORD" ) )
```

`Orders`
```java
AmazonManager.getInstance( this ).orders( object: AmazonCallback {

        override fun onComplete(orders: List<ScanResults>?) { }

        override fun onException( e: AmazonException) { }
        } )
```

## <a name=androidos></a> Android OS Support

BlinkReceipt is distributed with support for Android minSdk version 21

## <a name=autoConfiguration></a> Auto Configuration

Even though there are different ways to initialize the sdk, the recommended way would be through the `AndroidManifest.xml` file. Within this file add the following configuration to disable auto configuration.

`AndroidManifest.xml`
```xml
 <meta-data android:name="com.microblink.AutoConfiguration" android:value="false" />
```
If you manually initialize the SDK you should disable auto configuration in your manifest and within your projects Application class please add the following code to initialize the sdk.

```java
@Override
public void onCreate() {
        super.onCreate();

        BlinkReceiptSdk.initialize( context );
        }
```

```xml
<meta-data
        android:name="com.microblink.AutoConfiguration"
        android:value="false" />
```

```java
@Override
public void onTerminate() {
        BlinkReceiptSdk.terminate();

        super.onTerminate();
        }
```

## <a name=processorConfigurations></a> Processor Architecture Considerations

BlinkReceipt is distributed with **ARMv7** and **ARM64** native library binaries.

**ARMv7** architecture gives the ability to take advantage of hardware accelerated floating point operations and SIMD processing with [NEON](http://www.arm.com/products/processors/technologies/neon.php). This gives BlinkReceipt a huge performance boost on devices that have ARMv7 processors. Most new devices (all since 2012.) have ARMv7 processor so it makes little sense not to take advantage of performance boosts that those processors can give. Also note that some devices with ARMv7 processors do not support NEON and VFPv4 instruction sets, most popular being those based on [NVIDIA Tegra 2](https://en.wikipedia.org/wiki/Tegra#Tegra_2), [ARM Cortex A9](https://en.wikipedia.org/wiki/ARM_Cortex-A9) and older. Since these devices are old by today's standard, BlinkReceipt does not support them. For the same reason, BlinkReceipt does not support devices with ARMv5 (`armeabi`) architecture.

**ARM64** is the new processor architecture that most new devices use. ARM64 processors are very powerful and also have the possibility to take advantage of new NEON64 SIMD instruction set to quickly process multiple pixels with a single instruction.

There are some issues to be considered:

- ARMv7 build of the native library cannot be run on devices that do not have ARMv7 compatible processor
- ARMv7 processors do not understand x86 instruction set
- ARM64 processors understand ARMv7 instruction set, but ARMv7 processors do not understand ARM64 instructions.
  - <a name="64-bit-notice"></a> **NOTE:** as of the year 2018, some android devices that ship with ARM64 processors do not have full compatibility with ARMv7. This is mostly due to incorrect configuration of Android's 32-bit subsystem by the vendor, however Google decided that as of August 2019 all apps on PlayStore that contain native code need to have native support for 64-bit processors (this includes ARM64 and x86_64) - this is in anticipation of future Android devices that will support 64-bit code **only**, i.e. that will have ARM64 processors that do not understand ARMv7 instruction set.
- if ARM64 processor executes ARMv7 code, it does not take advantage of modern NEON64 SIMD operations and does not take advantage of 64-bit registers it has - it runs in emulation mode

`LibBlinkReceiptRecognizer.aar` archive contains ARMv7 and ARM64 builds of the native library. By default, when you integrate BlinkReceipt into your app, your app will contain native builds for all these processor architectures. Thus, BlinkReceipt will work on ARMv7 and ARM64 devices and will use ARMv7 features on ARMv7 devices and ARM64 features on ARM64 devices. However, the size of your application will be rather large.

## <a name="reduce-size"></a> Reducing the final size of your app

We recommend that you distribute your app using [App Bundle](https://developer.android.com/platform/technology/app-bundle). This will defer apk generation to Google Play, allowing it to generate minimal APK for each specific device that downloads your app, including only required processor architecture support.

### Using APK splits

If you are unable to use App Bundle, you can create multiple flavors of your app - one flavor for each architecture. With gradle and Android studio this is very easy - just add the following code to `build.gradle` file of your app:

```
android {
  ...
  splits {
    abi {
      enable true
      reset()
      include 'armeabi-v7a', 'arm64-v8a'
      universalApk true
    }
  }
}
```

With that build instructions, gradle will build two different APK files for your app. Each APK will contain only native library for one processor architecture and one APK will contain all architectures. In order for Google Play to accept multiple APKs of the same app, you need to ensure that each APK has different version code. This can easily be done by defining a version code prefix that is dependent on architecture and adding real version code number to it in following gradle script:

```
// map for the version code
def abiVersionCodes = ['armeabi-v7a':1, 'arm64-v8a':2]

import com.android.build.OutputFile

android.applicationVariants.all { variant ->
    // assign different version code for each output
    variant.outputs.each { output ->
        def filter = output.getFilter(OutputFile.ABI)
        if(filter != null) {
            output.versionCodeOverride = abiVersionCodes.get(output.getFilter(OutputFile.ABI)) * 1000000 + android.defaultConfig.versionCode
        }
    }
}
```

For more information about creating APK splits with gradle, check [this article from Google](https://developer.android.com/studio/build/configure-apk-splits.html#configure-abi-split).

After generating multiple APK's, you need to upload them to Google Play. For tutorial and rules about uploading multiple APK's to Google Play, please read the [official Google article about multiple APKs](https://developer.android.com/google/play/publishing/multiple-apks.html).

### Removing processor architecture support

If you won't be distributing your app via Google Play or for some other reasons want to have single APK of smaller size, you can completely remove support for certain CPU architecture from your APK. **This is not recommended due to [consequences](#arch-consequences)**.

To keep only some CPU architectures, for example `armeabi-v7a` and `arm64-v8a`, add the following statement to your `android` block inside `build.gradle`:

```
android {
    ...
    ndk {
        // Tells Gradle to package the following ABIs into your application
        abiFilters 'armeabi-v7a', 'arm64-v8a'
    }
}
```

This will remove other architecture builds for **all** native libraries used by the application.

To remove support for a certain CPU architecture only for BlinkReceipt, add the following statement to your `android` block inside `build.gradle`:

```
android {
    ...
    packagingOptions {
        exclude 'lib/<ABI>/libBlinkReceipt.so'
    }
}
```

where `<ABI>` represents the CPU architecture you want to remove:

- to remove ARMv7 support, use `exclude 'lib/armeabi-v7a/libBlinkReceipt.so'`
- to remove ARM64 support, use `exclude 'lib/arm64-v8a/libBlinkReceipt.so'`

You can also remove multiple processor architectures by specifying `exclude` directive multiple times. Just bear in mind that removing processor architecture will have side effects on performance and stability of your app. Please read [this](#arch-consequences) for more information.

### <a name="arch-consequences"></a> Consequences of removing processor architecture

- Google decided that as of August 2019 all apps on Google Play that contain native code need to have native support for 64-bit processors (this includes ARM64 and x86_64). This means that you cannot upload application to Google Play Console that supports only 32-bit ABI and does not support corresponding 64-bit ABI.

- By removing ARMv7 support, BlinkReceipt will not work on devices that have ARMv7 processors.
- By removing ARM64 support, BlinkReceipt will not use ARM64 features on ARM64 device
- also, some future devices may ship with ARM64 processors that will not support ARMv7 instruction set.

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 21+
- Compile SDK: 29+
- Java 8+