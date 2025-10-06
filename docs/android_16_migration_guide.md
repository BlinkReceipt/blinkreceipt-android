# Android 16 (API 36) Migration Guide for BlinkReceipt Android SDK

This guide provides essential steps for updating your app to target Android 16 (API 36) while ensuring continued compatibility with the BlinkReceipt Android SDK.

With the release of Android 16, Google has introduced several changes to enhance privacy, security, and user experience. To comply with Google Play's requirements and leverage the latest platform features, you must update your app's `targetSdkVersion` to 36.

Our latest SDK version, `{{ blinkreceipt.release }}`, is fully compatible with Android 16. Please update to this version before proceeding.

## Notes
**Min SDK 23:** 
Apps running on devices below API 23 can still use your existing app, but they won’t be able to upgrade once the new version ships.

**Target API 36:** 
You only need to compile against it; there’s no risk unless our SDK starts using API 36 features (which it doesn’t today).

**Java 17:** 
Our upcoming SDK (`{{ blinkreceipt.release }}`) targets JVM 17, but this won’t break client apps as long as you’re using reasonably up-to-date build tools.

**React Native:** 
Android 16 support requires the latest RN version, but that’s separate from our SDK upgrade.

## Prerequisites

Before you begin, please ensure you have the following:

1. Update Android Studio to the latest stable version (e.g., Koala 🐨 or newer).
2. Update the **Android Gradle Plugin** to the version recommended for targeting API 36.
    * See the [Android Gradle plugin](https://developer.android.com/build/releases/gradle-plugin#updating-gradle) official documentation to see what required plugin versions you'll use.

3. Update our SDK in your `build.gradle` or `build.gradle.kts` file:
```groovy
// build.gradle
dependencies {
    implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
    
    implementation("com.microblink.blinkreceipt:blinkreceipt-digital")
    implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
    // ...
}
```

4. Change your app's `targetSdkVersion` and `compileSdkVersion` to 36:
```groovy
// build.gradle
android {
    compileSdk 36

    defaultConfig {
        targetSdk 36
        // ...
    }
    // ...
}
```

## Behavioral Changes

These changes affect how your app and our SDK behave when running on an Android 16 device, regardless of targetSdkVersion. However, they are most relevant when you update your target.

### Edge-to-Edge Display Enabled by Default

**What's changing?**

To create more immersive user experiences, Android 16 enables edge-to-edge rendering by default for all apps targeting API 36. This means your app's UI will draw behind the transparent system status and navigation bars.

**Impact on your app:**

If you display any of our SDK's full-screen or large UI components (like [com.microblink.RecognizerCameraView]), they may have interactive elements (like buttons or input fields) that are partially obscured by the system bars, making them difficult for users to tap.

**Action required:**

You must handle window insets to prevent UI elements from overlapping with the system bars. Ensure you've opted into edge-to-edge.

   * See official documentation on [Behavior changes: Apps targeting Android 16 or higher](https://developer.android.com/about/versions/16/behavior-changes-16#edge-to-edge)

This ensures that BlinkReceipt SDK's UI respects the device's safe areas while still providing an immersive, edge-to-edge experience.

## Testing Checklist

After implementing the changes above, we strongly recommend you test the following scenarios on a device or emulator running Android 16:

- ✅ Test all flows that involve selecting BlinkReceipt SDK Camera or Scan feature. Ensure that it appears and functions correctly.

- ✅ Perform regression testing on all core features of your app that integrate with BlinkReceipt Android SDK `{{ blinkreceipt.release }}`.

## Get Help
If you encounter any issues during migration or have questions about these changes, please do not hesitate to reach out to our support team.
