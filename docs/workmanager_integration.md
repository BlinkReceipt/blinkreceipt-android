This is a guide on how we suggest apps with custom WorkManager configuration and initialization to properly integrate with our SDK.

If your app provides custom WorkManager configuration and initialization, follow these steps:

#### First, ensure that `androidx.startup` framework is added in your gradle dependencies:
```groovy

depdendencies {
    implementation("androidx.startup:startup-runtime:X.Y.Z")
}

```

#### Add the following entries under `AndroidManifest.xml`:

```xml
<!-- AndroidManifest.xml -->
<application>
    <!-- ... -->
    <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}.androidx-startup"
            android:exported="false"
            tools:node="merge">
            <!-- Remove BlinkReceipt SDK's default initializer -->
            <meta-data
                android:name="com.microblink.internal.ReceiptSdkInitializer" 
                android:value="androidx.startup"
                tools:node="remove" />
            <!-- Remove WorkManager Initializer -->
            <meta-data
                android:name="androidx.work.WorkManagerInitializer"
                android:value="androidx.startup"
                tools:node="remove" />
            <!-- Remove BlinkReceipt's Barcode Detector initializer -->
            <meta-data
                android:name="com.microblink.internal.BarcodeDetectorInitializer"
                android:value="androidx.startup"
                tools:node="remove"/>
            <!-- Introduce your custom Initializer -->
            <meta-data
                android:name="com.custom.app.AppInitializer"
                android:value="androidx.startup"/>
    </provider>
</application>
```

#### Then, define the custom Initializer. This is where the custom WorkManager configuration and initialization takes place.

```kotlin
public class AppInitializer: Initializer<Unit> {

    override fun create(context: Context) {
        val configuration = Configuration.Builder()
            .setDefaultProcessName("com.custom.app:custom-app-process")
            // Other Client-specific configuration(s)
            .build()
        WorkManager.initialize(context, configuration)
        return
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}
```

#### Finally, manually initialize the SDK from the Application class:

```java
@Override
public void onCreate() {
    super.onCreate();

    BlinkReceiptSdk.initialize( context );
}
```
- This will ensure that the your app's WorkManager configuration and initialization will supersede and will be used across both client app and SDK's internal logic.
