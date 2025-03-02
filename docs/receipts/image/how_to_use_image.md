The main and only entry point to perform Scan Receipt Image using Direct API is the `ImageClient` class. You can instantiate one by passing an android context to its constructor:

=== "Kotlin"
```kotlin
val client = ImageClient(context)
```
=== "Java"
```java
ImageClient client = new ImageClient(context);
```

#### Recognize Receipt Image using Direct API

You can scan and process a single or multiple Bitmap image(s). Additionally, you can also customize scan option configuration with `ScanOptions.Builder`.

=== "Kotlin"
```kotlin
val receiptImagesToProcess = arrayOf<Bitmap>(
    // Bitmap image(s)
)
val scanOptions = ScanOptions.newBuilder().build()

client.recognize(
    scanOptions,
    object : RecognizerCallback {
        override fun onRecognizerResultsChanged(result: RecognizerResult) {
            // Process Direct API Success Results.
        }

        override fun onRecognizerException(e: Throwable) {
            // Handle Direct API error.
        }

        override fun onRecognizerDone(results: ScanResults, media: Media) {
            // Observe Direct API Results Changes while Direct API is processing the results.
        }
    },
    *receiptImagesToProcess
)

```

=== "Java"
```java
Bitmap[] receiptImagesToProcess = new Bitmap[]{
    // Bitmap image(s)
};
ScanOptions scanOptions = ScanOptions.newBuilder().build()

client.recognize(
    scanOptions, 
    new RecognizerCallback() {
        @Override
        public void onRecognizerDone(@NonNull ScanResults scanResults, @NonNull Media media) {
            // Process Direct API Success Results.
        }

        @Override
        public void onRecognizerException(@NonNull Throwable throwable) {
            // Handle Direct API error.
        }

        @Override
        public void onRecognizerResultsChanged(@NonNull RecognizerResult recognizerResult) {
            // Observe Direct API Results Changes while Direct API is processing the results.
        }
    }, 
    receiptImagesToProcess
);
```
