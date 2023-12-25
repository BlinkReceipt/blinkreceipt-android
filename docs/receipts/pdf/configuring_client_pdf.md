The main and only entry point to scan a PDF is the `PdfClient` class. You can instantiate one by passing an android
context to its constructor:

=== "Kotlin"
    ```kotlin
    val client = PdfClient(context)
    ```
=== "Java"
    ```java
    PdfClient client = new PdfClient(context);
    ```

There are also some more options for configuring the client.

#### Country Code

The `countryCode` property specifies the code of the country in which the pdf scanning client is used.
Setting it correctly improves lookup results for the returned products.
You can specify this with the `countryCode` property on the `PdfClient` instance, i.e.:

=== "Kotlin"
    ```kotlin
    val client = PdfClient(context)

    client.countryCode = "US"
    ```
=== "Java"
    ```java
    PdfClient client = new PdfClient(context);

    client.countryCode("US");
    ```

By default, it is set to `"US"`.

#### Filter Sensitive Data

The `filterSensitiveData` property specifies the code of the country in which the pdf scanning client is used.
Setting it correctly improves lookup results for the returned products.
You can specify this with the `filterSensitiveData` property on the `PdfClient` instance, i.e.:

=== "Kotlin"
```kotlin
val client = PdfClient(context)

client.filterSensitiveData = true
```
=== "Java"
```java
PdfClient client = new PdfClient(context);

client.filterSensitiveData(true);
```

By default, it is set to `false`.

#### Return Sub Products

The `returnSubProducts` property specifies sub products will be returned.
You can specify this with the `returnSubProducts` property on the `PdfClient` instance, i.e.:

=== "Kotlin"
```kotlin
val client = PdfClient(context)

client.returnSubProducts = true
```
=== "Java"
```java
PdfClient client = new PdfClient(context);

client.returnSubProducts(true);
```

By default, it is set to `false`.

#### Return Voided Products

The `returnVoidedProducts` property specifies if voided products will be returned.
You can specify this with the `returnVoidedProducts` property on the `PdfClient` instance, i.e.:

=== "Kotlin"
```kotlin
val client = PdfClient(context)

client.returnVoidedProducts = true
```
=== "Java"
```java
PdfClient client = new PdfClient(context);

client.returnVoidedProducts(true);
```

By default, it is set to `false`.

#### Lifecycle Observer

Android Lifecycle Observer is a part of the Android Architecture Components introduced by Google. It is used to monitor and respond to lifecycle events of Android components, such as activities and fragments. The Android Lifecycle Observer is particularly useful for managing tasks that should be performed at specific points in an Android component's lifecycle. Its recommended to attach a lifecycle so the internal resource will be released.
=== "Kotlin"
```kotlin
client = PdfClient(applicationContext).also {
    lifecycle.addObserver(it)
}

```
