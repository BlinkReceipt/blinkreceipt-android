Before you can start scanning PDFs, you have to create an instance of the `PdfClient`.
=== "Kotlin"
    ```kotlin
    client = PdfClient(applicationContext).also {
        val options: ScanOptions = ScanOptions.newBuilder().build()

        it.countryCode = options.countryCode()

        it.filterSensitiveData = options.filterSensitiveData()

        it.returnSubProducts = options.returnSubProducts()

        it.returnVoidedProducts = options.returnVoidedProducts()

        lifecycle.addObserver(it)
    }
    ```
!!! warning
    If you don’t add a lifecycle observer, you will be responsible for releasing the `PdfClient` resources.

#### Recognize PDF

You can identify a PDF document by providing it as a Uri, File, or ParcelFileDescriptor input. The process of recognizing text within the PDF is performed asynchronously, and the results are delivered through the Google Task framework.

When recognizing text in a PDF document, you might use ParcelFileDescriptor to efficiently open and read the PDF file, feeding it to the OCR (Optical Character Recognition) engine asynchronously. The Google Task framework is well-suited for handling such asynchronous tasks and delivering results when the OCR process is complete.

=== "Kotlin"
    ```kotlin
    client.recognize(uri).addOnSuccessListener(this) {
        //Handle Results
    }.addOnFailureListener {
        //Handle failure
    }
    ```
