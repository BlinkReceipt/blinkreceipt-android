With the release [v1.6.8](https://github.com/BlinkReceipt/blinkreceipt-android/releases/tag/1.6.8) we are moving away from distributing static `.aar`
files on our GitHub Repository
and starting to distribute our libraries via Maven. This means there will also be changes in the way you integrate our libraries.

This is a short guide on how we suggest you migrate from using static `.aar` files and start using our libraries via Maven.

### How `.aar` libraries were added until now

There are two ways you could include an `.aar` library into an Android project until now:

1.  #### As a separate module

    This was possible through Android Studio until a few versions ago - you could go to `File -> New -> New Module -> Import .JAR-.AAR Package`,
    and select the `.aar` file you wanted to include in your project. This would create a new module in your project, which would contain only a
    small `build.gradle` file and your `.aar` file. It would also add an entry to your `settings.gradle` file for the new module.

    Here is an example of what this looks like for our `LibBlinkReceiptCore.aar` library in our GitHub sample
    app - [link](https://github.com/BlinkReceipt/blinkreceipt-android/tree/485d4f3f66c6938c976e8f1ff7510f851e5742d8/blinkreceipt-demo/LibBlinkReceiptCore).

    After that, you could reference the new module in your other modules, e.g. your app module, by adding it as a dependency in your `build.gradle`
    dependency block:
    ```groovy title="build.gradle"
    dependencies {
        implementation project(":LibBlinkReceiptCore")
    }
    ```

2. #### Copying the files into your project

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
   Our libraries are hosted on our own Maven server, at https://maven.microblink.com. To be able to reference our libraries, you have to add
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
            +    maven { url "https://maven.microblink.com" }
              }
            }
            ```
        === "Groovy"
            ```diff title="build.gradle"
            allProjects {
              repositories {
                ... // your other repositories
            +    maven { url = uri("https://maven.microblink.com") }
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
            +      maven { url = uri("https://maven.microblink.com") }
                 }
               }
            ```
        === "Groovy"
            ```diff title="settings.gradle"
               dependencyResolutionManagement {
                 repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                 repositories {
                   ... // your other repositories
            +      maven { url "https://maven.microblink.com" }
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
         -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
         -  implementation("androidx.appcompat:appcompat:1.6.1")
         -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
         -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
         -  implementation("com.squareup.retrofit2:retrofit:2.10.0")
         -  implementation("com.squareup.retrofit2:converter-gson:2.10.0")
         -  implementation("com.squareup.retrofit2:converter-scalars:2.10.0")
         -  implementation("com.squareup.okio:okio:3.9.0")
         -  implementation("com.google.android.gms:play-services-tasks:18.1.0")
         -  implementation("com.google.android.gms:play-services-auth:21.0.0")
         -  implementation("com.jakewharton.timber:timber:5.0.1")
         -  implementation("androidx.webkit:webkit:1.10.0")
         -  implementation("androidx.work:work-runtime:2.9.0")
         -  implementation("androidx.work:work-runtime-ktx:2.9.0")
         -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
         -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
         -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
         -  implementation("androidx.core:core-ktx:1.12.0")

         +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
         +  implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
            //BlinkReceiptRecognizer doesn't depend on BlinkReceiptCameraUi, so if you want to use our default scanning UI you also have to include the following dependency
         +  implementation("com.microblink.blinkreceipt:blinkreceipt-camera-ui")
         }
         ```

    === "BlinkReceiptAccountLinking"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
        -  implementation("androidx.core:core-ktx:1.12.0")
        -  implementation("androidx.work:work-runtime:2.9.0")
        -  implementation("androidx.work:work-runtime-ktx:2.9.0")
        -  implementation("androidx.appcompat:appcompat:1.2.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
        -  implementation("com.squareup.retrofit2:retrofit:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.10.0")
        -  implementation("com.squareup.okio:okio:3.9.0")
        -  implementation("com.jakewharton.timber:timber:5.0.1")
        -  implementation("com.google.android.gms:play-services-tasks:18.1.0")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.2")
        -  implementation("androidx.webkit:webkit:1.10.0")
        -  implementation("androidx.datastore:datastore-preferences:1.0.0")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0")
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-account-linking")
        }
        ```

    === "BlinkReceiptDigital"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.work:work-runtime:2.9.0")
        -  implementation("androidx.work:work-runtime-ktx:2.9.0")
        -  implementation( "com.microsoft.identity.client:msal:4.2.0" ) {
        -    exclude group: "com.microsoft.device.display"
        -}
        -  implementation("com.sun.mail:android-mail:1.6.7")
        -  implementation("com.sun.mail:android-activation:1.6.7")
        -  implementation("androidx.appcompat:appcompat:1.6.1")
        -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        -  implementation("androidx.fragment:fragment-ktx:1.6.2")
        -  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
        -  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
        -  implementation("androidx.lifecycle:lifecycle-common-java8:2.6.2")
        -  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
        -  implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.2")
        -  implementation("androidx.webkit:webkit:1.10.0")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
        -  implementation("com.google.android.material:material:1.11.0")
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
        -  implementation("androidx.core:core-ktx:1.12.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.10.0")
        -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        -  implementation("androidx.core:core:1.3.1")
        -  implementation("com.squareup.okio:okio:3.9.0")
        -  implementation("com.jakewharton.timber:timber:5.0.1")
        -  implementation("com.google.android.gms:play-services-tasks:18.1.0")
        -  implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0" exclude module: "httpclient")
        -  implementation("com.google.api-client:google-api-client-android:1.32.1" exclude module: "httpclient")
        -  implementation("com.google.http-client:google-http-client-gson:1.40.0" exclude module: "httpclient")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-digital")
        }
        ```
    === "BlinkReceiptEarnings"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.appcompat:appcompat:1.6.1")
        -  implementation("com.google.android.gms:play-services-tasks:18.1.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.10.0")
        -  implementation("com.squareup.okio:okio:3.9.0")
        -  implementation("com.jakewharton.timber:timber:5.0.1")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-earnings")
         }
        ```
    === "BlinkReceiptSurveys"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
        -  implementation("androidx.appcompat:appcompat:1.6.1")
        -  implementation("androidx.core:core-ktx:1.12.0")
        -  implementation("androidx.work:work-runtime:2.9.0")
        -  implementation("androidx.work:work-runtime-ktx:2.9.0")
        -  implementation("com.squareup.okhttp3:okhttp:4.10.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.10.0")
        -  implementation("com.squareup.okio:okio:3.9.0")
        -  implementation("com.jakewharton.timber:timber:5.0.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
        -  implementation("com.google.android.gms:play-services-tasks:18.1.0")
        -  implementation("com.google.android.material:material:1.11.0")
        -  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        -  implementation("androidx.fragment:fragment-ktx:1.6.2")
        -  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
        -  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-surveys")
         }
        ```

    === "BlinkReceiptBarcode"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.appcompat:appcompat:1.6.1")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.10.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:2.9.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:2.9.0")
        -  implementation("com.squareup.okio:okio:3.9.0")
        -  implementation("com.jakewharton.timber:timber:5.0.1")
        -  implementation("androidx.lifecycle:lifecycle-runtime:2.6.1")
        -  implementation("com.google.zxing:core:3.5.1")
        -  implementation("com.google.android.gms:play-services-tasks:18.1.0")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-barcode")
        }
        ```

    === "BlinkReceiptCore"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("androidx.appcompat:appcompat:1.6.1")
        -  implementation("androidx.core:core-ktx:1.12.0")
        -  implementation("androidx.work:work-runtime:2.9.0")
        -  implementation("androidx.work:work-runtime-ktx:2.9.0")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        -  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
        -  implementation("com.squareup.okhttp3:okhttp:4.12.0")
        -  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        -  implementation("com.squareup.retrofit2:retrofit:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-gson:2.10.0")
        -  implementation("com.squareup.retrofit2:converter-scalars:2.10.0")
        -  implementation("com.google.android.gms:play-services-tasks:18.1.0")
        -  implementation("com.squareup.okio:okio:3.9.0")
        -  implementation("com.jakewharton.timber:timber:5.0.1")
        -  implementation("androidx.preference:preference-ktx:1.2.1")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-core")
        }
        ```
    === "BlinkReceiptCameraUi"
        ```diff title="build.gradle"
        dependencies {
        -  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
        -  implementation("androidx.core:core-ktx:1.12.0")
        -  implementation("androidx.appcompat:appcompat:1.6.1")
        -  implementation("com.google.android.material:material:1.11.0")
        -  implementation("androidx.fragment:fragment:1.5.7")
        -  implementation("androidx.fragment:fragment-ktx:1.6.2")
        -  implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

        +  implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
        +  implementation("com.microblink.blinkreceipt:blinkreceipt-camera-ui")
        }
        ```

4. #### Combining multiple libraries

    If you want to combine multiple libraries, you only have to declare the BOM dependency once, and then list the other dependencies, e.g., if you wanted
    to use the BlinkReceiptRecognizer, BlinkReceiptAccountLinking and BlinkReceiptDigital libraries in the same project, your dependencies block would
    look something like this:

    ```groovy
    dependencies {
        implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))
        implementation("com.microblink.blinkreceipt:blinkreceipt-recognizer")
        implementation("com.microblink.blinkreceipt:blinkreceipt-account-linking")
        implementation("com.microblink.blinkreceipt:blinkreceipt-digital")
    }
    ```

    Since `BlinkReceiptRecognizer` depends on `BlinkReceiptCore` and `BlinkReceiptCamera`, it will pull them in without you having to declare them as
    dependencies. Same goes for `BlinkReceiptAccountLinking` and `BlinkReceiptDigital` which depend on `BlinkReceiptCore`.
