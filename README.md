# Blink Receipt SDK

Blink Receipt SDK for Android is an SDK that enables you to easily add near real time OCR functionality to your app with the purpose of scanning receipts. With provided camera management you can easily create an app that scans receipts. You can also scan images stored as Android Bitmaps that are loaded either from gallery, network or SD card.

Using Blink Receipt in your app requires a valid license key.  After registering, you will be able to generate a license key for your app. License key is bound to package name of your app, so please make sure you enter the correct package name when asked.

See below for more information about how to integrate Blink Receipt SDK into your app.

# Table of contents

* [Android _BlinkReceipt_ integration instructions](#intro)
* [Quick Start: Scan your first receipt](#quickStart)
   * [Understanding Results](#results)
   * [Customize Camera Scan Activity](#customizeScanActivity)
   * [Customize Scan Configuration](#customizeScanSession)
* [Recognizer View](#recognizerView)
* [Direct API](#directAPI)
* [Adding Product Intelligence](#intelligence)
* [Adding Google Places](#google)
* [Adding Yelp](#yelp)
* [Adding Amazon](#amazon)
* [Adding Gmail](#gmail)
* [Processor Configuration Considerations](#processorConfigurations)
* [Android OS Support](#androidos)
* [Auto Configuration](#autoConfiguration)
* [Client User Id](#clientId)
* [Requirements](#requirements)

## AAR
The package contains Android Archive (AAR) that contains everything you need to use BlinkReceipt library.

## <a name=intro></a> Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

```
dependencies {
    implementation 'androidx.appcompat:appcompat:1.0.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    
    implementation 'com.squareup.okhttp3:okhttp:4.2.0'
    
    implementation 'com.squareup.retrofit2:retrofit:2.6.2'
    implementation 'com.squareup.retrofit2:converter-gson:2.6.2'
    implementation 'com.squareup.retrofit2:converter-scalars:2.6.2'
    
    implementation 'com.squareup.okio:okio:2.4.0'
    
    implementation "com.google.android.gms:play-services-tasks:17.0.0"
    
    implementation project('REPLACE_WITH_IMPORTED_RECEIPT_SDK_MODULE')
}
```

Even though there are different ways to initialize the sdk, the recommended way would be through the `AndroidManifest.xml` file. Within this file add the following configuration.

`AndroidManifest.xml`
```
 <meta-data
            android:name="com.microblink.LicenseKey"
            android:value="BLINK RECEIPT LICENSE KEY" />
```

Within your projects Application class or Content Provider, please add the following code to initialize the sdk.

```

@Override
public void onCreate() {
    super.onCreate();
    
    ReceiptSdk.sdkInitialize( context );
}

@Override
public void onTerminate() {
    ReceiptSdk.terminate();

    super.onTerminate();
}

```

## <a name="quickStart"></a> Scanning Your First Receipt
The easiest way to get started scanning your first receipt would be to use the internal Scan Activity within the aar.

```
ScanOptions scanOptions = ScanOptions.newBuilder()
                                          .retailer( Retailer.UNKNOWN )
                                          .frameCharacteristics( FrameCharacteristics.newBuilder()
                                                  .storeFrames( true )
                                                  .compressionQuality( 100 )
                                                  .externalStorage( false )
                                                  .build() )
                                          .logoDetection( true )
                                          .build();

Bundle bundle = new Bundle();

bundle.putParcelable( CameraScanActivity.SCAN_OPTIONS_EXTRA, scanOptions );

Intent intent = new Intent( this, CameraScanActivity.class )
                .putExtra( CameraScanActivity.BUNDLE_EXTRA, bundle );

startActivityForResult( intent, SCAN_RECEIPT_REQUEST );
```

The results are returned through 2 objects, which can be retrieved by getting the parcelable extras `ScanResults` and `Media`. The `ScanResults` object contain the results from the scan session.

```
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

`CameraScanActivity.SCAN_OPTIONS_EXTRA` : ScanOptions : (Recommended) Extra recommended to properly scan receipt.

`CameraScanActivity.FULL_SCREEN_EXTRA` : boolean : Extra to have the activity be a full screen activity. `false` by default.

`CameraScanActivity.KEEP_SCREEN_ON_EXTRA`: boolean : Extra to keep screen on while scanning session in progress. `false` by default.

`CameraScanActivity.ENABLE_ENHANCED_AUTO_FOCUS`: boolean : Enable enhanced autofocus for camera during scan session. `false` by default.

`CameraScanActivity.LICENSE_KEY_EXTRA`: String : Alternative way to initialize SDK if not done through default method.

`CameraScanActivity.VIEW_PORT_EXTRA`: RectF : Defines the region in which we want to scan on the frame. The properties of the RectF are defined as a percentage of the screen.

`CameraScanActivity.VIDEO_RESOLUTION_EXTRA` : VideoResolutionPreset : Video resolution preset extra for camera.

`CameraScanActivity.CAMERA_RECOGNIZER_CALLBACK_EXTRA` : CameraRecognizerCallback : Interface passed in as a parcelable extra, that will receive every recognizer's result.

UI Dimens

`<?xml version="1.0" encoding="utf-8"?>
 <resources>
     <dimen name="camera_scan_bottom_frame_height">80dp</dimen>
     <dimen name="camera_scan_take_picture_size">60dp</dimen>
 </resources>`

### <a name=customizeScanSession></a> Customize Scan Configuration
Want to see your captured frames? save scanned results? include barcode recognition? This extra functionality is possible through the scanOptions object. The builder pattern allows you to customize your scan session configuration.

`retailer( Retailer retailer )`: If the retailer is known the sdk can take advantage of the pre-defined text styles and other known characteristics of that retailer's receipt.

`searchTargets( List<Product> searchTargets )`: If searching for particular set of Products, those products can be passed into the scan options and consequently the scansession itself.

`storeFrames( boolean storeFrames )`: If set to true, this configuration will save the captured and confirmed frames to disk. The paths to those files will be returned within the `Media` object which is returned in the onActivityResults bundle.

`useExternalStorage( boolean useExternalStorage )`: This configuration goes hand in hand with the previous configuration. By default frames will be stored within the applications internal storage. If set to `true` the path will be set to be the external application storage. Results and access to these files will be returned the same way.

`edgeDetectionConfiguration( EdgeDetectionConfiguration configuration )`: The sdk's functionality includes edge detection. Here we can determine the edges of the receipt and therefore the content percentage of the receipt. This configurations allow for customized parameters to be set around what is acceptable criteria scanning a receipt. In the case of a low content percentage or below the defined threshold we display a helpful message to the user to let them know to move closer.

`promotionSlugs( Utility.newArrayList( new Slug( "[NAME OF SLUG]" ) ) )`: If set, this configuration will validate promotions based on the configured slugs.

`validatePromotions( true || false )`: If set to true, this configuration will validate promotions..

## <a name=results></a>Retrieving Results
The RecognizerCallback interface is the way to retrieve results and statuses on the scanning progress.

```
public interface RecognizerCallback {

    // Called when scan results are compiled and saved images are processed.
    void onRecognizerDone( @NonNull ScanResults results, Media media );

    // Called in the case there is an exception while scanning the captured frame.
    void onRecognizerException(@NonNull Throwable throwable );

    // The callback invoked whenever a step within the scanning process is returned.
    void onRecognizerResultsChanged( @NonNull RecognizerResult result );

}
```

```
public interface CameraRecognizerCallback {

    // The callback invoked if while utilizing the RecognizerView the confirm frame is called saving the image. This callback provides the location of the saved frame.
    void onConfirmPicture( @NonNull File file );

    // As of Android Marshmallow (API 24) Runtime permissions are required to access hardware features like the camera. This callback will be invoked if proper permissions have not been granted for camera use.
    void onPermissionDenied();

    //Notifying the user of any issue while using camera preview as well as when preview is started and ended.
    void onPreviewStarted();

    void onPreviewStopped();

    void onException( @NonNull Throwable throwable );
}
```
The RecognizerCallback also provides preliminary results.

```
  recognizerView.preliminaryResults();
  
```

```
   @Override
    public void onRecognizerResultsChanged(@NonNull RecognizerResult result) {

        if ( result instanceof PreliminaryResult ) {
            PreliminaryResult results = (PreliminaryResult) result;
        }
    }
```

The RecognizerCallback also provides raw results.

```
   @Override
    public void onRecognizerResultsChanged(@NonNull RecognizerResult result) {

        if ( result instanceof OcrRawResult ) {
            OcrRawResult ocrRawResult = (OcrRawResult) result;
        }
    }
```

The RecognizerCallback also provides edge results.

```
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

```
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
The RecognizerView is also able to capture frames, not unlike a regular camera application. This is achieved with the `takePicture( CameraCaptureListener listener )` method. The method takes in a `CameraCaptureListener.class`. This listener provides the recognizer view a callback to pass back the resulting image. The image is returned within a `BitmapResults` object. In order to access the resulting bitmap, call bitmap(). The RecognizerView will create a copy of this bitmap, so you are free to manipulate, display, clean it up, etc... however you feel fit.

In addition to the take picture functionality the `RecognizerView` provide the ability to write the frame to disk and have the set of captured frames returned to you at the end of the scan session via the `Media` object in the `onScanComplete()` call back a part of the `RecognizerCallback.class`.

You can also cancel your current scan if it is taking longer than normal to retrieve any result callback, but it is recommened to set an appropriate Timeout within your scanOptions object so that it may be resolved internally.

Terminating your scan via `terminate()` will end your session, resetting the parser and internal result calculating mechanism.

#### Finishing the Scan
When you wish to finish your scan session call `finishedScanning()`. This will begin the session ending process where results will be compiled and finalized. Results are delivered via the `onRecognizerDone( ScanResults results, Media media )` within the interface RecognizerCallback.

*Note*
All RecognizerCallback methods are executed on the main thread.

## <a name=directAPI></a> Direct API
The Blink Receipt SDK is capable of scanning images passed in as a bitmap. This utilizes the direct api which can be accessed via the `Recognizer` instance. The Recognizer is a singleton that when calling `recognizeBitmap( @NonNull Bitmap bitmap, @NonNull CameraOrientation orientation, @NonNull final RecognizerCallback callback )` will scan your bitmap image based on the ScanOption configurations and return to you the results via the callback provided.

## <a name=intelligence></a>Product Intelligence
If you wish to include product intelligence functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```
  <meta-data
            android:name="com.microblink.ProductIntelligence"
            android:value="PRODUCT INTELLIGENCE KEY" />
```

## <a name=google></a>Google Places
If you wish to include Google Places functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```
 <meta-data
             android:name="com.microblink.GooglePlacesKey"
             android:value="GOOGLE PLACES KEY"/>
```

## <a name=yelp></a>Yelp
If you wish to include Yelp functionality within your project add your license key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```
        <meta-data
            android:name="com.microblink.YelpKey"
            android:value="YELP KEY"/>
```

## <a name=clientId></a>Client User Id
If you wish to include your client user id within your project add your client user id key to the `AndroidManifest.xml` file, similar to the setup for this sdk.

`AndroidManifest.xml`
```
        <meta-data
            android:name="com.microblink.ClientUserId"
            android:value="CLIENT USER ID"/>
```

## <a name=gmail></a>Gmail
If you wish to include Gmail functionality within your project.

`Dependencies`
```
dependencies {
    implementation "com.google.android.gms:play-services-auth:16.0.1"
    implementation "com.google.apis:google-api-services-gmail:v1-rev105-1.25.0" exclude module: 'httpclient'
    implementation "com.google.android.gms:play-services-tasks:16.0.1"

    implementation "com.google.api-client:google-api-client-android:1.29.2" exclude module: 'httpclient'
    implementation "com.google.http-client:google-http-client-gson:1.30.1" exclude module: 'httpclient'
}

```

`Callback`
```
GmailInboxManager.getInstance( this ).callback( new GmailInboxCallback() {

    @Override
    public void onSignedOut() {

    }

    @Override
    public void onException(@NonNull GmailInboxException e) {

    }

    @Override
    public void onSignedIn() {

    }

    @Override
    public void onComplete(@NonNull List<ScanResults> results) {

    }
    
} );
```

`Client ID (OAuth 2.0 web application client ID)`
```
GmailInboxManager.getInstance( this ).clientId( getString( R.string.client_id ) )
```

`Lifecycle Management`
```
@Override
protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
    super.onActivityResult( requestCode, resultCode, data );

    GmailInboxManager.getInstance().onActivityResult( requestCode, resultCode, data );
}
```

`Sign In`
```
  GmailInboxManager.getInstance( this ).signIn( this )
```

`Sign Out`
```
   GmailInboxManager.getInstance( this ).signOut()
```

`Read Inbox`
```
 GmailInboxManager.getInstance( this ).readInbox( this )
```

## <a name=amazon></a>Amazon
If you wish to include Amazon functionality within your project. Note: Amazon functionality targets KitKat and above.

`Credentials`
```
AmazonManager.getInstance( this ).credentials( AmazonCredentials( "AMAZON_EMAIL", "AMAZON_PASSWORD" ) )

```

`Orders`
```
AmazonManager.getInstance( this ).orders( object: AmazonCallback {

        override fun onComplete(orders: List<ScanResults>?) {
        }

        override fun onException( e: AmazonException) {
        }

    } )
```

## <a name=androidos></a> Android OS Support

BlinkReceipt is distributed with support for Android minSdk version 21

## <a name=autoConfiguration></a> Auto Configuration

Even though there are different ways to initialize the sdk, the recommended way would be through the `AndroidManifest.xml` file. Within this file add the following configuration to disable auto configuration.

`AndroidManifest.xml`
```
 <meta-data
    android:name="com.microblink.AutoConfiguration"
    android:value="false" />
```

## <a name=processorConfigurations></a> Processor Architecture Considerations

BlinkReceipt is distributed with both ARMv7, ARM64, x86 and x86_64 native library binaries.

ARMv7 architecture gives the ability to take advantage of hardware accelerated floating point operations and SIMD processing with NEON. This gives BlinkReceipt a huge performance boost on devices that have ARMv7 processors. Most new devices (all since 2012.) have ARMv7 processor so it makes little sense not to take advantage of performance boosts that those processors can give. Also note that some devices with ARMv7 processors do not support NEON instruction sets. Most popular are those based on NVIDIA Tegra 2 fall into this category. Since these devices are old by today's standard, BlinkReceipt does not support them.

ARM64 is the new processor architecture that most new devices use. ARM64 processors are very powerful and also have the possibility to take advantage of new NEON64 SIMD instruction set to quickly process multiple pixels with single instruction.

x86 architecture gives the ability to obtain native speed on x86 android devices, like Asus Zenfone 4. Without that, BlinkReceipt will not work on such devices, or it will be run on top of ARM emulator that is shipped with device - this will give a huge performance penalty.

x86_64 architecture gives better performance than x86 on devices that use 64-bit Intel Atom processor.

However, there are some issues to be considered:

ARMv7 build of native library cannot be run on devices that do not have ARMv7 compatible processor (list of those old devices can be found here)
ARMv7 processors does not understand x86 instruction set
x86 processors do not understand neither ARM64 nor ARMv7 instruction sets
however, some x86 android devices ship with the builtin ARM emulator - such devices are able to run ARM binaries but with performance penalty. There is also a risk that builtin ARM emulator will not understand some specific ARM instruction and will crash.
ARM64 processors understand ARMv7 instruction set, but ARMv7 processors does not understand ARM64 instructions
if ARM64 processor executes ARMv7 code, it does not take advantage of modern NEON64 SIMD operations and does not take advantage of 64-bit registers it has - it runs in emulation mode
x86_64 processors understand x86 instruction set, but x86 processors do not understand x86_64 instruction set
if x86_64 processor executes x86 code, it does not take advantage of 64-bit registers and use two instructions instead of one for 64-bit operations
LibBlinkReceipt.aar archive contains ARMv7, ARM64, x86 and x86_64 builds of native library. By default, when you integrate BlinkReceipt into your app, your app will contain native builds for all processor architectures. Thus, BlinkReceipt will work on ARMv7, ARM64, x86 and x86_64 devices and will use ARMv7 features on ARMv7 devices and ARM64 features on ARM64 devices. However, the size of your application will be rather large.

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 21+
- Compile SDK: 29+
- Java 8+