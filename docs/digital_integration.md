Please follow the [Project Integration and Initialization](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/receipt-recognizer/README.md#-project-integration-and-initialization), [R8/Proguard](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/receipt-recognizer/README.md#r8--proguard), and the application class/manifest step in the [Scanning Your First Receipt](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/receipt-recognizer/README.md#-scanning-your-first-receipt) sections to properly add and initialize recognizer sdk.

To add the sdk to your android project please follow these steps:

1. Add the following maven repository to your build.gradle or settings.gradle, depending on your implementation:

=== "Groovy"
    ```groovy
    repositories {
      mavenCentral()
    }
    ```

2. Add the following to your dependency section in your app `build.gradle`.

=== "Groovy"
      ```groovy
      dependencies {
          implementation(platform("com.actualplatform:receipt-bom:{{ blinkreceipt.release }}"))

          implementation("com.actualplatform:receipt-digital")
          implementation("com.actualplatform:receipt-recognizer")
      }
      ```

Initialize the `ReceiptDigitalSdk` in your application class.

=== "Kotlin"
      ```kotlin
      class ReceiptApplication : Application() {

          override fun onCreate() {
              super.onCreate()

              ReceiptDigitalSdk.initialize(this, object : InitializeCallback {

                  override fun onComplete() {

                  }

                  override fun onException(e: Throwable) {

                  }

              })
          }
      }
      ```
