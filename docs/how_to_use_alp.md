### Linking Accounts

Before you can start retrieving orders, you have to link an account.

To link an account to the SDK, you have to instantiate an `Account` object and call the `link`
method on an instance of the `AccountLinkingClient` class. The `link` method takes in three arguments.

The first argument is an `Account` object, representing the account to be linked.

The second one is a `success` callback, which will be called if the linking process is successful. The callback returns a UUID for tracking purposes.
If you find any issues while linking or retrieving orders, you can send us the UUID of the session, and we can check the logs to see what happened.

The third argument is a `failure` callback, which is called whenever the linking process runs into an issue. It returns
an [AccountLinkingException](https://htmlpreview.github.io?https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/docs/blinkreceipt-account-linking/com/microblink/linking/AccountLinkingException.html)
which contains an error code, a UUID of the session, a reference to an underlying exception, and, depending on the error type, a reference to a
WebView.
It's possible that while linking your account, additional actions are needed from the end user, like entering a 2FA code, solving a captcha, or
similar. This is recognized by the error code in the returned exception `VERIFICATION_NEEDED`.
In this case, you have to display the returned WebView. For more information, see the example below.

Account Linking offers two UI/UX experiences when linking a retailer's connection. Depending on your app needs, you can choose:

#### 1. Host App Authentication
Using this flow, the client’s host app provides a native prompt for users to populate credentials. All other interactions with the merchant are done with the WebView hidden in the background with exception when 2FA, Captcha or other required user input.

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    
    // AMAZON is used just as an example
    val account = Account(
        retailerId = AMAZON,
        credentials = Credentials.Password(
            "amazon_username",
            "amazon_password"
        )
    )
    
    client.link(
        account = account,
        success = { uuid ->
            // grab orders, etc... 
        },
        failure = { exception ->
            if (exception.code == VERIFICATION_NEEDED) {
                // in this case, the exception.view will be != null, so you can show it in your app
                // and the user can resolve the needed verification, i.e.:
                if (exception.view != null) {
                    binding.webViewContainer.addView(exception.view)
                }
            }
        }
    )
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);

    Account account = new Account(
        AMAZON,
        new Credentials.Password(
            "amazon_username",
            "amazon_password"
        )
    );

    client.link(
        account,
        (String uuid) -> {
            // grab orders, etc...
            return Unit.INSTANCE;
        },
        (AccountLinkingException exception) -> {
            if (exception.code() == VERIFICATION_NEEDED) {
                // in this case, the exception.view will be != null, so you can show it in your app
                // and the user can resolve the needed verification, i.e.:
                if (exception.view() != null) {
                    binding.webViewContainer.addView(exception.view());
                }
            }
            return Unit.INSTANCE;
        }
    );
    ```

- Collect the user’s credentials for a given retailer using your own UI

#### 2. Retailer Web View Authentication
Using this flow the host app doesn’t need to provide any native functionality to collect credentials but instead present a WebView with retailer’s webpage pre-populated.
All steps needed for authentication, a user may handle in the same WebView.

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    
    // AMAZON is used just as an example.
    val account = Account(
        retailerId = AMAZON,
        credentials = Credentials.None // No need to explicitly provide credentials
    )

    client.link(
        account = account,
        success = { uuid ->
            // grab orders, etc... 
        },
        failure = { exception ->
            // handle exception
        }
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);

    Account account = new Account(
        AMAZON,
        Credentials.None.INSTANCE
    );

    client.link(
        account,
        (String uuid) -> {
            // grab orders, etc...
            return Unit.INSTANCE;
        },
        (AccountLinkingException exception) -> {
            // handle exception
            return Unit.INSTANCE;
        }
    );
    ```

By linking the account, the details of that account (e.g., `retailerId` and `credentials` field values) are cached locally and encrypted for future use and can be retrieved using the `accounts` method.

!!! info
    Only one account can be linked per retailer at once. For example, you can only link one Walmart account.
    If you try to link another Walmart account, you'll get an error `ALREADY_LINKED`.

    To link a new account for the same retailer, you need to unlink the old account first. For more details, check out the [Unlinking Account](#unlinking-account) section.

!!! warning  "Do not tie `link` calls to any lifecycle methods, like `onCreate` or `onStart`, as this can lead to creating multiple sessions at once and memory leaks."

To see the full list of supported retailers and their IDs, check out
the [documentation](https://htmlpreview.github.io/?https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/docs/blinkreceipt-account-linking/com/microblink/linking/RetailerIds.html).

### Account Re-authentication

After the account is successfully linked, it may get logged out at some point,
meaning it wouldn’t have an active retailer session. Trying to call `orders` for that account will
result in an error `REAUTHENTICATION_REQUIRED`.

To avoid this error, it is recommended to call the `requiresReAuthentication` method on the `AccountLinkingClient` instance. 
The method takes `Account` as an argument and checks whether it still has an active retailer session, i.e.,
if the account is authenticated.

If the account requires re-authentication, call `reAuthenticate` first and then proceed with grabbing orders.

An example of how to use re-authentication is shown below:

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    
    // use already linked account 
    
    client.requiresReAuthentication(account)
        .addOnSuccessListener { requiresReAuthentication ->
            if (requiresReAuthentication) {
                client.reAuthenticate(
                    account,
                    success = {
                        // for example perform grab orders
                    },
                    failure = { exc ->
                        // handle error
                    }
                )
            } else {
                // account doesn't require a re-authentication
                // proceed with, for example, grab orders
            }
        }
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);

    // use already linked account
    
    client.requiresReAuthentication(account)
        .addOnSuccessListener( requiresReAuthentication -> {
            if (requiresReAuthentication) {
                client.reAuthenticate(
                    account,
                    success = {
                        // for example perform grab orders
                    },
                    failure = { exc ->
                        // handle error
                    }
                )
            } else {
                // account doesn't require a re-authentication
                // proceed with, for example, grab orders
            }
        }
    ```

### Grabbing Orders

To grab orders, call the `orders` function on the `AccountLinkingClient` instance.

The method takes in a `retailerId`, a `success` and a `failure` callback.

The `success` callback will be called multiple times during one order retrieving session. 
The callback will be invoked once for every order that has been found, from the most recent to the oldest order.
The callback provides the retailer ID, the retrieved order, the session UUID, as well as the number 
of remaining orders which are expected to be returned.

The `failure` callback returns an `AccountLinkingException` as well as the retailer ID for which the failure occurred.
If some additional action is required from the user, the `failure` callback can return an exception with the code `VERIFICATION_NEEDED`,
which means you'll have to handle it in a way described in the [Linking Account](#1-host-app-authentication) section.

!!! warning "Always make sure to call `orders` for the retailer with the valid account"
    If you haven't previously linked an account for the given retailer, the `orders` method will call the `failure` callback with the error code `MISSING_CREDENTIALS`.

    If the linked account for the provided retailer requires re-authentication, the `orders` method will call the `failure` callback with the error code `REAUTHENTICATION_REQUIRED`.

#### Grabbing Orders Example

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    
    // link an account or get already linked account, for example for AMAZON

    // check whether the account requires re-authentication

    val allOrders = mutableListOf<ScanResults>()

    client.orders(
        AMAZON,
        success = { retailerId: Int, results: ScanResults?, remaining: Int, uuid: String ->
            if (results != null) {
                allOrders.add(results)
            }

            if (remaining == 0) {
                // process the collected orders
            }
        },
        failure = { retailerId: Int, exception: AccountLinkingException ->
            if (exception.code == VERIFICATION_NEEDED) {
                // in this case, the exception.view will be != null, so you can show it in your app
                // and the user can resolve the needed verification, i.e.:
                if (exception.view != null) {
                    binding.webViewContainer.addView(exception.view)
                }
            }
        })
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);

    // link an account or get already linked account, for example for AMAZON

    // check whether the account requires re-authentication

    List<ScanResults> allResults = new ArrayList<>();

    client.orders(AMAZON, (Integer retailerId, ScanResults results, Integer remaining, String uuid) ->{
        allResults.add(results);
        if (remaining == 0){
            // process the collected orders
        }
        return Unit.INSTANCE;
    }, (Integer retailerId, AccountLinkingException exception) -> {
        if (exception.code() == VERIFICATION_NEEDED){
            // in this case, the exception.view() will be != null, so you can show it in your app
            // and the user can resolve the needed verification, i.e.:
            if (exception.view() != null){
                binding.webViewContainer.addView(exception.view())
            }
        }
        return Unit.INSTANCE;
    });
    ```
!!! warning  "Do not tie `orders` calls to any lifecycle methods, like `onCreate` or `onStart`, as this can lead to creating multiple sessions at once and memory leaks."


### Unlinking Account

When you want to link a new account for the retailer that already has an account linked, you need to call `unlink`.
By calling `unlink`, account data is removed from the device cache.

Here's an example of a flow you might have in your implementation:

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)

    client.link(
        Account(
            AMAZON,
            Credentials.Password(
                "amazon_username",
                "amazon_password"
            )
        ),
        { uuid ->
            // ... 
        },
        failure = { exception ->
            // ...
        }
    )
    
    // do some work, grab orders, etc...

    // retrieve and unlink account
    client.accounts().addOnSuccessListener {
        val amazonAccount = it?.firstOrNull { it.retailerId == AMAZON }
        if (amazonAccount != null) {
            client.unlink(amazonAccount).addOnSuccessListener {
                // i.e. link another Amazon account
            }
        }
    }
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);

    client.link(
        new Account(
            AMAZON,
            new Credentials.Password(
                "amazon_username",
                "amazon_password"
            )
        ),
        (String uuid) -> {
            // ...
        },
        (AccountLinkingException exception) -> {
            // ...
        }
    );
    
    // do some work, grab orders, etc...

    // retrieve and unlink account
    client.accounts().addOnSuccessListener(accounts ->{
        Optional<Account> account = accounts.stream().filter(acc -> acc.retailerId() == AMAZON).findFirst();
        if(account.isPresent()){
            client.unlink(account.get()).addOnSuccessListener(success -> {
                // i.e. link another amazon account
            });
        }
    });
    ```

### Closing the client

After you're finished with the client, i.e. you've linked the accounts you wanted to or retrieved orders, you must call the `close` method of
the `AccountLinkingClient` to reclaim resources and prevent memory leaks.

### Resetting retailer history

If you want to clear the history of a specific retailer, you can call the `resetHistory` and pass in the specific retailer ID.
You should do this when you want to retrieve older orders that you had already retrieved before.

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)

    client.resetHistory(AMAZON).addOnSuccessListener {
        // ...
    }
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);

    client.resetHistory(AMAZON).addOnSuccessListener(success -> {
        // ...
    });
    ```
