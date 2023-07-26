The main and only entry point to using the Account Linking SDK is the `AccountLinkingClient` class. You can instantiate one by passing an android
context to its constructor:

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);
    ```

There are also some more options for configuring the client.

#### Cutoff Day

The cutoff day is the day until which the most recent orders will be retrieved. There are two ways of setting the cutoff day:

1. Setting the `dayCutoff` property on the `AccountLinkingClient` instance, i.e.:

    === "Kotlin"
        ```kotlin
        val client = AccountLinkingClient(context)
        client.dayCutoff = 14
        ```
    === "Java"
        ```java
        AccountLinkingClient client = new AccountLinkingClient(context);
        client.dayCutoff(14);
        ```
   This means that the orders from the last 14 days will be retrieved from the linked account.
   By default, the `dayCutoff` is set to 15 days.

2. Setting the `dateCutoff` property on the `AccountLinkingClient` instance, i.e.:

    === "Kotlin"
        ``` kotlin
        val client = AccountLinkingClient(context)
        client.dateCutoff = Date.from(Instant.now().minus(14, ChronoUnit.DAYS))
        ```
    === "Java"
        ```java
        AccountLinkingClient client = new AccountLinkingClient(context);
        client.dateCutoff(Date.from(Instant.now().minus(14, ChronoUnit.DAYS)));
        ```
   This will also mean that orders from the last 14 days are retrieved, but you could also set a fixed date. The default value is `null`, and
   the `dayCutoff` value is used instead.

#### Latest Orders Only

The `latestOrdersOnly` property specifies at what point the SDK stops searching for orders.

Example: `dayCutoff` is set to 14 days, and you retrieve orders. After that, you set the `dayCutoff` to 28 days. If `latestOrdersOnly` is set to true,
you will only get orders which are newer than the latest order you received the first time.
If it is set to `false`, you will also get orders which are older than those you got when retrieving orders with a 14 days cutoff, and at the same time
inside the 28-day window.

You can specify this with the `latestOrdersOnly` property on the `AccountLinkingClient` instance, i.e.:

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    client.latestOrdersOnly = false
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);
    client.latestOrdersOnly(false);
    ```

The property `latestOrdersOnly` is set to `true` by default, which means you will only get orders newer than the most recent order you've received. If
it's the first time you're retrieving orders, you will get all orders inside the cutoff window.

#### Country Code

The `countryCode` property specifies the code of the country in which the Account Linking SDK is used.
Setting it correctly improves lookup results for the returned products.
You can specify this with the `countryCode` property on the `AccountLinkingClient` instance, i.e.:

=== "Kotlin"
    ```kotlin
    val client = AccountLinkingClient(context)
    client.countryCode = "US"
    ```
=== "Java"
    ```java
    AccountLinkingClient client = new AccountLinkingClient(context);
    client.countryCode("US");
    ```

By default, it is set to `"US"`.