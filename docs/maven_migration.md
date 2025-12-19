### Effective From: Sep 1, 2025 

Microblink is moving its Android SDK distribution from a custom Maven repository ([Microblink Maven](https://maven.microblink.com/)) to **Maven Central** ([Maven Central: Search](https://central.sonatype.com/search?q=com.microblink.blinkreceipt)).

### What’s Changing?

#### Old Setup (Before Migration):
```groovy
repositories {
    maven { url "https://maven.microblink.com" }
}
dependencies {
    implementation 'com.microblink.blinkreceipt:blinkreceipt-bom:[version]'
}
```
    The second way was copying the `.aar` file(s) into your project, usually into a `libs` folder in the root of your project, and then referencing it
    in your `build.gradle` files like this:
     ```groovy title="build.gradle"
     dependencies {
         implementation files("../libs/LibBlinkReceiptCore.aar")
     }
     ```

But this would only add our binary `.aar` file to your project, which meant you also had to include all our transitive dependencies manually - and
keep track of them as we updated them.

### How to migrate to our Maven integration

We've migrated over to using Maven as our main and recommended way of distributing our libraries and keeping track of our transitive dependencies. So,
here are the steps you need to take to migrate over to using our maven integration:

1. #### Remove the old integration
    1. ##### Integration as a separate module
        If your current integration was [As a separate module](#as-a-separate-module), you'll have to remove/delete those modules for all of our
        libraries. I.e., if you were using our `LibBlinkReceiptRecognizer` module to scan physical receipts, you also had to
        use `LibBlinkReceiptCore`, `LibBlinkReceiptCamera`, and possibly `LibBlinkReceiptCameraUi`, which means you would have to delete each of these
        modules.
        Then, remove the `include` entries from the `settings.gradle` for those modules, i.e.:

        ```diff title="settings.gradle"
         include ':app'
        -include ':LibBlinkReceiptCore'
        -include ':LibBlinkReceiptRecognizer'
        -include ':LibBlinkReceiptCamera'
        -include ':LibBlinkReceiptCameraUi'
        ```
        Lastly, remove the dependency declarations for these modules, i.e.:

        ```diff title="build.gradle"
         dependencies {
         ...//your other dependencies
        -implementation(project(':LibBlinkReceiptCore'))
        -implementation(project(':LibBlinkReceiptRecognizer'))
        -implementation(project(':LibBlinkReceiptCamera'))
        -implementation(project(':LibBlinkReceiptCameraUi'))
        }
        ```

    2. ##### Integration by copying the files to your project

        If your current integration was by [Copying the files into your project](#copying-the-files-into-your-project), you'll have to delete
        the `.aar` files from your project. I.e., if you were using our `LibBlinkReceiptRecognizer` module to scan physical receipts, you also had to
        use `LibBlinkReceiptCore`, `LibBlinkReceiptCamera`, and possibly `LibBlinkReceiptCameraUi`, you would have to
        delete `LibBlinkReceiptRecognizer.aar`,`LibBlinkReceiptCore.aar`, `LibBlinkReceiptCamera.aar`and `LibBlinkReceiptCameraUi.aar`.
        Then, remove the dependency declarations to these files from your code, i.e.:

        ```diff title="build.gradle"
        dependencies {
        ...//your other dependencies
        -implementation(files("../libs/LibBlinkReceiptCore.aar"))
        -implementation(files("../libs/LibBlinkReceiptRecognizer.aar"))
        -implementation(files("../libs/LibBlinkReceiptCamera.aar"))
        -implementation(files("../libs/LibBlinkReceiptCameraUi.aar"))
        }
        ```

2. #### Add our Maven repository to your project configuration
   Our libraries are hosted on  mavenCentral(). To be able to reference our libraries, you have to add
   our Maven server to your project configuration. There are currently two ways of doing this, either in your root `build.gradle` file or in
   your `settings.gradle` file. Older projects usually have the maven repositories declared in the root `build.gradle`, while newer ones have it in
   `settings.gradle`. To add our Maven server to your project configuration, do the following, depending on if you have the config in
   your `build.gradle` or `settings.gradle` file:
       1. ##### build.gradle

        === "Kotlin"
            ```diff title="build.gradle.kts"
            allProjects {
              repositories {
                ... // your other repositories
            +    mavenCentral()
              }
            }
            ```
        === "Groovy"
            ```diff title="build.gradle"
            allProjects {
              repositories {
                ... // your other repositories
            +    mavenCentral()
              }
            }
            ```

       2. ##### settings.gradle

        === "Kotlin"
            ```diff title="settings.gradle.kts"
               dependencyResolutionManagement {
                 repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                 repositories {
                   ... // your other repositories
            +       mavenCentral()
                 }
               }
            ```
        === "Groovy"
            ```diff title="settings.gradle"
               dependencyResolutionManagement {
                 repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                 repositories {
                   ... // your other repositories
            +      mavenCentral()
                 }
               }
            ```

3. #### Remove transitive dependencies and reference our dependencies in your code
   All that's left is to remove transitive dependencies and add our maven dependency declarations in your `build.gradle` dependencies blocks. Since we're using
   a [BOM](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms) implementation
   for our maven integration, you only have to declare the root version of our SDK using the platform declaration. You can then declare only the
   necessary modules you want to use, without specifying a version.
   Here are the changes you are supposed to make in your build.gradle, for each of our library modules:

    === "BlinkReceiptRecognizer"
         ```diff title="build.gradle"
         dependencies {
         -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
         -  implementation("androidx.appcompat:appcompat:1.7.0")
         -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
         -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
         -  implementation("com.squareup.retrofit2:retrofit:2.11.0")
         -  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
         -  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
         -  implementation("com.squareup.okio:okio:3.10.2")
         -  implementation("com.google.android.gms:play-services-tasks:18.2.0")
         -  implementation("com.google.android.gms:play-services-auth:21.2.0")
         -  implementation("androidx.webkit:webkit:1.12.1")
         -  implementation("androidx.work:work-runtime:2.10.0")
         -  implementation("androidx.work:work-runtime-ktx:2.10.0")
         -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
         -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
         -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.1")
         -  implementation("androidx.core:core-ktx:1.13.1")

         +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
         +  implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
            //BlinkReceiptRecognizer doesn't depend on BlinkReceiptCameraUi, so if you want to use our default scanning UI you also have to include the following dependency
         +  implementation("com.microblink.blinkreceipt:blinkreceipt-camera-ui")
         }
         ```

    === "BlinkReceiptAccountLinking"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
        -  implementation("androidx.core:core-ktx:1.13.1")
        -  implementation("androidx.work:work-runtime:2.10.0")
        -  implementation("androidx.work:work-runtime-ktx:2.10.0")
        -  implementation("androidx.appcompat:appcompat:1.2.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
        -  implementation("com.squareup.retrofit2:retrofit:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
        -  implementation("com.squareup.okio:okio:3.10.2")
        -  implementation("com.google.android.gms:play-services-tasks:18.2.0")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.2")
        -  implementation("androidx.webkit:webkit:1.12.1")
        -  implementation("androidx.datastore:datastore-preferences:1.1.1")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}")
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-account-linking")
        }
        ```

    === "BlinkReceiptDigital"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.work:work-runtime:2.10.0")
        -  implementation("androidx.work:work-runtime-ktx:2.10.0")
        -  implementation( "com.microsoft.identity.client:msal:5.5.0" ) {
        -    exclude group: "com.microsoft.device.display"
        -}
        -  implementation("com.sun.mail:android-mail:1.6.7")
        -  implementation("com.sun.mail:android-activation:1.6.7")
        -  implementation("androidx.appcompat:appcompat:1.7.0")
        -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        -  implementation("androidx.fragment:fragment-ktx:{{ blinkreceipt.release }}")
        -  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
        -  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")
        -  implementation("androidx.lifecycle:lifecycle-common-java8:2.8.3")
        -  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
        -  implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.3")
        -  implementation("androidx.webkit:webkit:1.12.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.1")
        -  implementation("com.google.android.material:material:1.12.0")
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
        -  implementation("androidx.core:core-ktx:1.13.1")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
        -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        -  implementation("androidx.core:core:1.3.1")
        -  implementation("com.squareup.okio:okio:3.10.2")
        -  implementation("com.google.android.gms:play-services-tasks:18.2.0")
        -  implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0" exclude module: "httpclient")
        -  implementation("com.google.api-client:google-api-client-android:2.7.0" exclude module: "httpclient")
        -  implementation("com.google.http-client:google-http-client-gson:1.45.0" exclude module: "httpclient")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-digital")
        }
        ```
    === "BlinkReceiptEarnings"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.appcompat:appcompat:1.7.0")
        -  implementation("com.google.android.gms:play-services-tasks:18.2.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
        -  implementation("com.squareup.okio:okio:3.10.2")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-earnings")
         }
        ```
    === "BlinkReceiptSurveys"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
        -  implementation("androidx.appcompat:appcompat:1.7.0")
        -  implementation("androidx.core:core-ktx:1.13.1")
        -  implementation("androidx.work:work-runtime:2.10.0")
        -  implementation("androidx.work:work-runtime-ktx:2.10.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.10.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
        -  implementation("com.squareup.okio:okio:3.10.2")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.1")
        -  implementation("com.google.android.gms:play-services-tasks:18.2.0")
        -  implementation("com.google.android.material:material:1.12.0")
        -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        -  implementation("androidx.fragment:fragment-ktx:{{ blinkreceipt.release }}")
        -  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
        -  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-surveys")
         }
        ```

    === "BlinkReceiptBarcode"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.appcompat:appcompat:1.7.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:2.9.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:2.9.0")
        -  implementation("com.squareup.okio:okio:3.10.2")
        -  implementation("androidx.lifecycle:lifecycle-runtime:2.6.1")
        -  implementation("com.google.zxing:core:3.5.1")
        -  implementation("com.google.android.gms:play-services-tasks:18.2.0")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-barcode")
        }
        ```

    === "BlinkReceiptCore"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.appcompat:appcompat:1.7.0")
        -  implementation("androidx.core:core-ktx:1.13.1")
        -  implementation("androidx.work:work-runtime:2.10.0")
        -  implementation("androidx.work:work-runtime-ktx:2.10.0")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
        -  implementation("com.google.android.gms:play-services-tasks:18.2.0")
        -  implementation("com.squareup.okio:okio:3.10.2")
        -  implementation("androidx.preference:preference-ktx:1.2.1")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-core")
        }
        ```
    === "BlinkReceiptCameraUi"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
        -  implementation("androidx.core:core-ktx:1.13.1")
        -  implementation("androidx.appcompat:appcompat:1.7.0")
        -  implementation("com.google.android.material:material:1.12.1")
        -  implementation("androidx.fragment:fragment:1.5.7")
        -  implementation("androidx.fragment:fragment-ktx:{{ blinkreceipt.release }}")
        -  implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:{{ blinkreceipt.release }}"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-camera-ui")
        }
        ```

#### New Setup (After Migration):
```groovy
repositories {
    mavenCentral()
}
dependencies {
    implementation 'com.microblink.blinkreceipt:blinkreceipt-bom:[version]'
}
```
>
> 🔁 Only the repository source is changing. The SDK package name and usage remain the same.
>

### Step-by-Step Migration Instructions

#### 1. Remove the Custom Microblink Maven Repository
In your project’s **build.gradle** (Project-level or Module-level), remove:
```groovy
maven { url "https://maven.microblink.com" }
```

#### 2. Ensure Maven Central is Declared
If not already present, add this to your repositories block:
```groovy
repositories {
    mavenCentral()
}
```
>
> ℹ️ If you’re using google() make sure mavenCentral() is included as well.
>

#### 3. Keep Your Existing Dependency Declaration
No changes are required to the dependency itself. You can continue using:
```groovy
implementation 'com.microblink.blinkreceipt:blinkreceipt:[version]'
```
Replace [version] with the version you need. You can browse available versions here: [Maven Central: Search](https://central.sonatype.com/search?q=com.microblink.blinkreceipt)

### Post-Migration Checklist
- Removed the custom [Maven Microblink](https://maven.microblink.com/) repository
- Added or verified mavenCentral() is present
- Synced Gradle and verified no build errors
- Checked that the correct SDK version is downloaded from Maven Central

### FAQ

#### Q: Do I need to change the SDK version number?
- **A:** No, unless you’re upgrading to a newer version. The same artifact and versioning are used.

#### Q: What if I’m using a private or enterprise build system (e.g., Artifactory)?
- **A:** Ensure your internal repository proxies Maven Central. Contact your DevOps team if needed.

#### Q: Will older versions be available on Maven Central?
- **A:** Only selected versions are hosted. It’s recommended to use the latest version available on Maven Central.
