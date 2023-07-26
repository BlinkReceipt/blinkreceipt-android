### Linking Accounts

Before you can start retrieving orders, you have to link and verify an account. To link an account to the SDK,
you have to instantiate an `Account` object and call the `link` method on an instance of the `AccountLinkingClient` class:

=== "Kotlin"
    ```kotlin 
    // AMAZON_BETA is used just as an example
    val account = Account(
        AMAZON_BETA,
        PasswordCredentials(
            "amazon_username",
            "amazon_password"
        )
    )
    
    client.link(account).addOnSuccessListener {
        //move on to the next step, verifying the account or retrieving orders
    }.addOnFailureListener {
        //linking shouldn't fail, as the API forbids you from using an invalid retailer ID. 
    }
    ```
=== "Java"
    ```java
    Account account = new Account(
        AMAZON_BETA,
        new PasswordCredentials(
            "amazon_username",
            "amazon_password"
            )
        );

    client.link(account).addOnSuccessListener(success -> {
        //move on to the next step, verifying the account or retrieving orders
    })
    .addOnFailureListener (exception ->{
        //linking shouldn't fail, as the API forbids you from using an invalid retailer ID. 
    });
    ```

!!! info "The `link` method takes in a `vararg` of `Account`, so you can link multiple accounts (for different retailers) at once if needed"

By linking the account, it is cached locally and encrypted for future use and can be retrieved using the `accounts` method.

!!! warning
    Only one account can be linked per retailer at once. E.g. you can only link one Walmart account.
    If you try to link another Walmart account, the old account will be removed and the new account will be linked.

Similarly, accounts can be unlinked, by which they are removed from the device cache. Here's an example of a flow you might have in your
implementation:

=== "Kotlin"
    ```kotlin
    val client = AcountLinkingClient(context)
    client.link(Account(AMAZON_BETA, PasswordCredentials("amazon_username", "amazon_password")))
    //do some work, verify, grab orders, etc...
    
    //retrieve and unlink account
    client.accounts().addOnSuccessListener {
        val amazonAccount = it?.firstOrNull { it.retailerId == AMAZON_BETA }
        if (amazonAccount != null) {
            client.unlink(amazonAccount).addOnSuccessListener {
                //i.e. link another Amazon account
            }
        }
    }
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);
    client.link(new Account(AMAZON_BETA, new PasswordCredentials("amazon_username", "amazon_password")));
    //do some work, verify, grab orders, etc...
    
    //retrieve and unlink account
    client.accounts().addOnSuccessListener(accounts ->{
        Optional<Account> account = accounts.stream().filter(acc -> acc.retailerId() == AMAZON_BETA).findFirst();
        if(account.isPresent()){
            client.unlink(account.get()).addOnSuccessListener(success -> {
                //i.e. link another amazon account
            });
        }
    });
    ```

To see the full list of supported retailers and their ids, check out
the [documentation](https://htmlpreview.github.io/?https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/docs/blinkreceipt-account-linking/com/microblink/linking/RetailerIds.html)
.

### Grabbing Orders

When grabbing orders, you can decide to either grab orders for all linked accounts or to grab orders for only a specific retailer.
Depending on your use case, you can call the necessary `orders` function on the `AccountLinkingClient` instance.

The method takes in a success and failure callback.

The success callback will be called multiple times during one order retrieving session. The callback will be invoked once for every order that has
been found, from the most recent to the oldest order.
The callback provides the retailer ID, the retrieved order, the session UUID as well as the number of remaining orders which are expected to be
returned.

!!! info "If the linked account hasn't already been verified, the `orders` call will try to verify it before retrieving any orders."

The failure callback returns an `AccountLinkingException` as well as the retailer ID for which the failure occurred.
If user was logged out since the last `verify` or `orders` call or some additional action is required from the user, the
failure callback can return an exception with the code `VERIFICATION_NEEDED`, which means you'll have to handle it like described in the [Verifying Accounts](#verifying-accounts-optional) section.

#### Grabbing Orders Example

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    //config the client, or link an account if you haven't linked it already
    
    val allOrders = mutableListOf<ScanResults>()
    
    client.orders(AMAZON_BETA,
        success = { retailerId: Int, results: ScanResults?, remaining: Int, uuid: String ->
            if (results != null) {
                allOrders.add(results)
            }
    
            if (remaining == 0) {
                //process the collected orders
            }
        },
        failure = { retailerId: Int, exception: AccountLinkingException ->
            if (exception.code == VERIFICATION_NEEDED) {
                //in this case, the exception.view will be != null, so you can show it in your app
                //and the user can resolve the needed verification, i.e.:
                if (exception.view != null) {
                    binding.webViewContainer.addView(exception.view)
                }
            }
        })
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);
    //config the client, or link an account if you haven't linked it already
    Account account = new Account(
            AMAZON_BETA,
            new PasswordCredentials(
                    "amazon_username",
                    "amazon_password"
            )
    );
    
    List<ScanResults> allResults = new ArrayList<>();
    
    client.orders(AMAZON_BETA, (Integer retailerId, ScanResults results, Integer remaining, String uuid) ->{
        allResults.add(results);
        if(remaining == 0){
            //process the collected orders
        }
        return Unit.INSTANCE;
    }, (Integer retailerId, AccountLinkingException exception) -> {
        if(exception.code() == VERIFICATION_NEEDED){
            //in this case, the exception.view() will be != null, so you can show it in your app
            //and the user can resolve the needed verification, i.e.:
            if(exception.view() != null){
                binding.webViewContainer.addView(exception.view())
            }
        }
        return Unit.INSTANCE;
    });
    ```

!!! warning

      If you call the `orders` function which collects orders for all your linked accounts, you will have to keep track of which retailers
      finished or failed manually to be sure that the `orders` session is over.

### Verifying Accounts (optional)

After you've linked an account, you can verify it before retrieving orders. This will ensure that the linked account is valid before you try to retrieve orders.
To verify an account, you have to call the `verify` method of the `AccountLinkingClient`.
The `verify` method takes in the id of the retailer for which you want to verify the linked account, and then also three callbacks.

The first one is a success callback, which will be called if the verification process was successful. The callback returns a boolean value that tells
you if your account was verified, as well as a UUID for tracking purposes.
If you find any issues while verifying or retrieving orders, you can send us the UUID of the session and we can check the logs to see what happened.

The second callback you need to pass in is an error callback, which is called whenever the verification process runs into an issue. It returns
an [AccountLinkingException](https://htmlpreview.github.io?https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/docs/blinkreceipt-account-linking/com/microblink/linking/AccountLinkingException.html)
, which contains an error code, a UUID of the session, a reference to an underlying exception, and, depending on the error type, a reference to a
WebView.
It's possible that while verifying your account, additional actions are needed from the end user, like entering a 2FA code, solving a captcha, or
similar. This is recognized by the error code in the returned exception `VERIFICATION_NEEDED`.
In this case, you have to display the returned WebView. For more information, see the example below.

The third callback is a preview callback, which will return the `WebView` which will run the verification session, but only if
the `BlinkReceiptLinkingSdk.debug` property is set to `true`.
This is meant to be used only for debugging purposes.

#### Verification Example

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    
    val account = Account(
        AMAZON_BETA,
        PasswordCredentials(
            "amazon_username",
            "amazon_password"
        )
    )
    
    client.link(account).addOnSuccessListener {
        client.verify(account.retailerId,
            success = { success: Boolean, uuid: String -> // grab orders, etc...
            },
            failure = { exception ->
                if (exception.code == VERIFICATION_NEEDED) {
                    //in this case, the exception.view will be != null, so you can show it in your app
                    //and the user can resolve the needed verification, i.e.:
                    if (exception.view != null) {
                        binding.webViewContainer.addView(exception.view)
                    }
                }
            }
        )
    }
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);

    Account account = new Account(
            AMAZON_BETA,
            new PasswordCredentials(
                    "amazon_username", 
                    "amazon_password"
            )
    );

    client.link(account).addOnSuccessListener(success ->{
        client.verify(account.retailerId(), (Boolean isSuccess, String uuid) -> {
            //grab orders, etc...
            return Unit.INSTANCE;
        }, (AccountLinkingException exception) -> {
            if (exception.code() == VERIFICATION_NEEDED) {
                //in this case, the exception.view will be != null, so you can show it in your app
                //and the user can resolve the needed verification, i.e.:
                if (exception.view() != null) {
                    binding.webViewContainer.addView(exception.view());
                }
            }
            return Unit.INSTANCE;
        });
    });
    ```

!!! warning  "Do not link verify or order calls to any lifecycle methods, like `onCreate` or `onStart`, as this can lead to creating multiple sessions at once and memory leaks."

### Closing the client

After you're finished with the client, i.e. you've verified the accounts you wanted to verify or retrieved orders, you must call the `close` method of
the `AccountLinkingClient` to reclaim resources and prevent memory leaks.

### Resetting retailer history

If you want to clear the history of a specific retailer, you can call the `resetHistory` and pass in the specific retailer ID.
You should do this when you want to link a different account for the same retailer you already had linked, or if you want to retrieve older orders
which you had already retrieved before.

=== "Kotlin"
    ```kotlin title="Reset retailer history"
    val client = AccountLinkingClient(context)
    
    client.resetHistory(AMAZON_BETA).addOnSuccessListener {
       // e.g. link a different amazon account
    }
    ```
=== "Java"
    ```java title="Reset retailer history"
    AccountLinkingClient client = new AccountLinkingClient(context);

    client.resetHistory(AMAZON_BETA).addOnSuccessListener(success -> {
      // e.g. link a different amazon account
    });    
    ```

Similarly, you can reset the history for all retailers, by calling the `resetHistory` method which takes in no parameters.

=== "Kotlin"
    ```kotlin title="Reset all retailers history"
    val client = AccountLinkingClient(context)
    
    client.resetHistory().addOnSuccessListener {
    }
    ```
=== "Java"
    ```java title="Reset all retailers history"
    AccountLinkingClient client = new AccountLinkingClient(context);
    
    client.resetHistory().addOnSuccessListener(sucess -> {
    });
    ```