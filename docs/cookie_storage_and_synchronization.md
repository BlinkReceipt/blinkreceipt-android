
The BlinkReceipt SDK enables web cookies and depends on them for full functionality.

By default, the SDK securely stores a copy of cookies in persistent storage, which is later used to synchronize with each WebView session.

## Scenario

Since [CookieManager](https://developer.android.com/reference/android/webkit/CookieManager) is a shared singleton across the app instance, it can introduce several problems.
One of which is when Cookies were explicitly removed from outside the SDK's scope. Consider the following:
```kotlin
// At some point within the client app, cookies were explicitly cleared
CookieManager.getInstance().removeAllCookies { result ->
    // callback
}
```

- at this point, once the cookies are removed, the SDK may encounter unexpected behaviors, such as losing authentication, user sessions, and data persistence of previously linked account(s).

To address this, we decided to implement a Custom Cookie Storage implementation within the SDK. 

**NOTE**: No code changes are required from the client.

## Opt-out from SDK's Custom Cookie Storage implementation
If you wish to opt-out from this approach, just include the following code:
```kotlin
// ...
import com.microblink.core.WebCookiesManager
import com.microblink.core.CookieStorageType

// Set the cookie storage type to None
WebCookiesManager.configure(CookieStorageType.None)
```
Once set, SDK's succeeding WebView Sessions will no longer store the cookies.
