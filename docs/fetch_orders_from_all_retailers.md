Starting from version [1.7.6](https://github.com/windfall-labs/windfall-android-sdk/releases/tag/1.7.6), `AccountLinkingClient.orders(success:failure:)` function, where the SDK allows you to perform fetching ALL orders from ALL of your linked retailer(s), was already removed. Hence, a breaking API change. The main reason is that this method caused synchronization and race condition issues which led to unexpected results.

In order to still achieve the same functionality to perform Fetch ALL order from ALL of your linked retailer(s), we highly recommend to follow a similar approach below:

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    // ...
    // Link retailers
    // ...

    // Execute the following code block under a suspend function
    // OR execute under a coroutine scope
    coroutineScope.launch {
        val retailerAccounts = withContext(Dispatchers.IO) {
            client.accounts().await() ?: listOf()
        }

        // Collect Scan Results from each retailer. Each retailer can have multiple Scan Results.
        val allOrdersScanResults = MutableStateFlow(listOf<ScanResults>())

        // Create a sequence of callback flows that represents fetch orders from each retailer
        val fetchOrderOperationFlowSequence = retailerAccounts.map { retailer ->
            callbackFlow<Result<Unit>> {
                client.orders(
                    retailerId = retailer.retailerId,
                    success = { /*retailerId*/_: Int, results: ScanResults?, remaining: Int, /*uuid*/_: String ->
                        if(results != null) {
                            // Append results
                            allOrdersScanResults.update { current ->
                                ArrayList(current).apply { add(results) }
                            }

                            // Emit event
                            trySend(
                                Result.success(Unit)
                            )
                        }

                        if(remaining <= 0) {
                            close()    // Close this callbackFlow since there are NO more remaining products to scan from this retailer
                        }
                    },
                    failure = { _: Int, throwable: AccountLinkingException ->
                        trySend(Result.failure(throwable))
                        close()
                        // TODO:: Handle Error
                    },
                )

                awaitClose()
            }
        }
            .asSequence()

        // Iterate collect results in a SYNCHRONOUS and SEQUENTIAL manner.
        flow {
            for(orderOperation in fetchOrderOperationFlowSequence) {
                emitAll(orderOperation)
            }
        }
            .catch {
                // TODO:: Handle Error
            }
            .collect()

        if(allOrdersScanResults.value.isNotEmpty()) {
            // You may SORT Scanned Results by Retailer
            val sortedScannedResults = allOrdersScanResults.value
                .sortedBy { it.retailerId().id() }
            // TODO:: Show SCANNED RESULTS
        } else {
            // TODO:: Show EMPTY results prompt
        }

    }

    ```

You may implement your own implementation of Fetch ALL Orders operation for as long as the execution is done in SYNCHRONOUS manner, execute Fetch Order operation from 1 retailer at a time.
