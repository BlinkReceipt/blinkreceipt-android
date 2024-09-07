Please follow the [Project Integration and Initialization](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-project-integration-and-initialization), [R8/Proguard](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#r8--proguard), and the application class/manifest step in the [Scanning Your First Receipt](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-scanning-your-first-receipt) sections to properly add and initialize recognizer sdk.

To add the sdk to your android project please follow these steps:

1. Add the following maven repository to your build.gradle or settings.gradle, depending on your implementation:

=== "Groovy"
    ```groovy
    repositories {
      maven { url  "https://maven.microblink.com" }
    }
    ```

2. Add the following to your dependency section in your app `build.gradle`.

=== "Groovy"
      ```groovy
      dependencies {
          implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.3"))
      
          implementation("com.microblink.blinkreceipt:blinkreceipt-digital")
          implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
      }
      ```

Initialize the `BlinkReceiptDigitalSdk` in your application class.

=== "Kotlin"
      ```kotlin
      class BlinkApplication : Application() {
      
          override fun onCreate() {
              super.onCreate()
      
              BlinkReceiptDigitalSdk.initialize(this, object : InitializeCallback {
      
                  override fun onComplete() {
      
                  }
      
                  override fun onException(e: Throwable) {
      
                  }
      
              })
          }
      }
      ```