Blink Receipt Digital sdk allows for full Gmail Integration.

## <a name=gmail></a> **Gmail Client**

The `GmailClient` is the corner stone of the gmail sdk integration. It is the access point for reading and parsing emails from clients. It leverages Google's task framework to allow for seamless and clear multi-threading functionality.

To instantiate the `GmailClient` you must provide the constructor 3 non-null and non-zero arguments.

1. Context: `Context`. When using the client within an Android `Activity` you can pass in `this` for the argument value. If using the client within an Android `Fragment` you can pass in `requireActivity()`.

2. Thread Count: `int` which determines the number of threads to use for processing `e-receipt` emails. The internal default value we use is `4`.

3. Client Id: `String` which comes from  Google's api services. Please refer [here](https://developers.google.com/identity/protocols/oauth2/native-app) for more information on how to create an Android client id.

<br />

#### Code sample for Gmail Client Instantiation:
=== "Kotlin"
    ```kotlin
        // Activity Example
        class GmailActivity: Activity() {
    
            override fun onCreate(savedInstanceState: Bundle?) {
                val gmailClient = GmailClient(this, 4)
            }
    
        }
    
        // Fragment Example
        class GmailFragment: Fragment() {
    
            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                val gmailClient = GmailClient(requireActivity(), 4)
            }
    
        }
    ```

<br />

### **Logging In To Gmail**

Users may log in to gmail via the client's `login()` function. There is an overloaded login function which takes in an Android `Activity`, `login(Activity activity)`. Passing the `Activity` allows for our components to be lifecycle aware and not leak memory. This parameter is optional though and not required for any explicit extra functionality.

The login call returns a Google `Task` of type `GoogleSignInAccount`. The `GoogleSignInAccount` is an object which contains the basic account information of the signed in Google user. The reference for GoogleSignInAccount can be found [here](https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInAccount).

`login()` can be called from any thread, because the return type is a `Task<GoogleSignInAccount>`. If you read the previous section of this document on the [Task](digital_tasks.md) framework you should have some familiarity with how this process works.

Let's step through the different login scenario results that can occur and how to provide proper handling of each scenario.


<br />

#### **Successful Login Scenario**

A successful login attemt will return a valid `GoogleSignInAccount`. This sign in account can be captured via an `OnSuccessListener` call.


Here is an example of some happy case scenarios when calling the login() function.

=== "Kotlin"
    ```kotlin
        fun loginUser() {
            val task: Task<GoogleSignInAccount> = gmailClient.login()
    
            task.addOnSuccessListener {
    
            }
        }
    ```

<br />

#### **Unsuccessful Login Scenario**

A login attempt can fail for a number of reasons. Google has integrated its sign in flow into the Android system. For safety reasons, calling login does not always automatically sign the default user in to your application. It is possible that Google may require extra authentication when you attempt to signIn. The Gmail Integration of the sdk provides an easy to use wrapper around Google's authentication handling. When calling login it is possible for exceptions to be thrown. We try to make exception handling as easy as possible for you. Therefore, we have created our own easy to use exceptions that will allow your app proper recourse in the event of a failure.


GmailAuthException

One of the most important exceptions to look out for is the `GmailAuthException`. This exception occurs in the event of any silent authentication exceptions. This exception is extremely important, because it could potentially contain a recourse for a user to take upon an unsuccessful sign in. The exception potentially contains an Android `Intent` this is an intent that has been provided by Google and meant to be launched by the app developer to obtain an explicit approval from the app user. The intent must be triggered with a `startActivityForResults()` call. This call is a method within Android components (Activity, Fragment). It takes in an `Intent` as well as an `int` which denotes the identifying `requestCode`. Starting this activity for result will display an overlaying window that will display the user's registered accounts on the device.

Once a user has selected their desired account the overlaying screen will automatically dismiss and the `onActivityResult(int requestCode, int resultCode, Intent data)` callback overridden within your Android component (Activity or Fragment) will be invoked. There is no need to handle the result and data yourself, the gmail client provides an easy to use function to handle this. Simply call `gmailClient.onAccountAuthorizationActivityResult()`, passing in the `requestCode`, `resultCode`, and `data` respectively.

The `onAccountAuthorizationActivityResult` function also returns a task of type `GoogleSignInAccount`. If the sign is successful then the Intent data passed in usually contains the desired `GoogleSignInAccount` which the client parses and returns to you in the form of a result. If the sign in is NOT successful the returned task will throw an exception notifying your app of the failed result.

**NOTE** Most common cause for an exception in this scenario is the user, when presented with the Google Sign In Screen Overlay, opted not to choose any account and clicked the cancel option in the modal.

**NOTE** THIS `GmailAuthException` SCENARIO WILL MOST LIKELY BE THE USER EXPERIENCE FLOW THE FIRST TIME A USER SIGNS IN TO YOUR APP

**EXAMPLE GMAIL LOGIN IMPLEMENTATION**

=== "Kotlin"
    ```kotlin
    class GmailInboxFragment : Fragment() {
    
        private lateinit var gmailClient: GmailClient
    
        //...Instantiate GmailClient in one of the lifecycle methods
    
        // Attempt to login
        fun login() {
            gmailClient.login()
                    .addOnSuccessListener {
    
                    }.addOnFailureListener { e ->
                        if (e is GmailAuthException) {
                            startActivityForResult(e.signInIntent, e.requestCode)
                        } else {
                            //Set error display
                        }
                    }
        }
    
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
    
            gmailClient.onAccountAuthorizationActivityResult(requestCode, resultCode, data)
               .addOnSuccessListener { signInAccount ->
    
            }.addOnFailureListener {
                //Set error display
            }
        }
    }
    ```

Once a user has successfully signed in we are good to go! We can now move on to retrieving emails from a signed in user.

<br />

### **Verifying Gmail Log In And Retrieving Already Signed In Users**

So as not to constantly bombard users with a typical sign in flow, we provide the `verify()` function on the `GmailClient`. The `verify()` call returns a `Task<Boolean>`. This boolean result returnes either `true` or `false`. If the result is `false` then this indicates that the sdk has no record of a signed in user. As a result you must take the user through the original sign in flow mentioned in the previous section.

In the event, the result returns a `true` value, this indicates that we have an account signed in with Google. In which case, you can call `credentials()` on the `GmailClient`. The `credentials()` call returns a `Task<GoogleSignInAccount>`. This will silently fetch the signed in user's account and return it to the caller via the `Task`.

**EXAMPLE GMAIL VERIFY AND CREDENTIALS IMPLEMENTATION**

=== "Kotlin"
    ```kotlin
         private fun verifyUser() {
            gmailClient.verify()
                .addOnSuccessListener {
    
                }.addOnFailureListener {
    
                }
        }
    ```

<br />

### **Logging Out of Gmail**

Users may log out of gmail via the client's `logout()` function. The logout function will return a `Task<Boolen>`. This function completes 2 objectives. First, it signs the currently signed in user out from Gmail/Google SDK. This means that the user can no longer be "silently" signed in to Google. The next time the `verify()` is called a `false` result should be returned via the `Task` return object. This also means that the `credentials()` call will not return a `GoogleSignInAccount` result, but instead throw an exception (not unlike the initial user flow for sign in we covered before). Secondly, the `logout()` call will clear any cached "date" threshold we use for email searching. You should never receive a `Boolean` result of `false` for the signout task. It will always either be true or throw an exception in the unlikely event of an error.

**EXAMPLE GMAIL LOGOUT IMPLEMENTATION**

=== "Kotlin"
    ```kotlin
        private fun logoutUser() {
            gmailClient.logout().addOnSuccessListener {
    
            }
        }
    ```

<br />

### **Reading Messages And Getting Results**

After a user has been signed in to their Gmail Account, we can now fetch their emails and find any receipts they may have stored in their email. Before we initiate a search, we want to make sure we have properly configured the `GmailClient`. All sdk email clients have properties that can be configured to optimize searching. Here is a list of the following properties

| Property Name | Type | Default Value | Client Function |                           Description                           |
|---------------|------|---------------|-----------------|-----------------------------------------------------------------|
|  dayCutoff    | Int  |     14        | dayCutoff(int days) | Maximum number of days look back in a users inbox for receipts             |
| filterSensitive | Boolean | false    | filterSensitive(boolean filterSensitive)| When set to true the sdk will not return product results for products deemed to be sensitive i.e. adult products |
| subProducts   | Boolean | false      | subProducts(boolean subProducts)| Enable sdk to return subproducts found on receipts under parent products i.e. "Burrito + Guacamole <- Guac is subproduct"  |
| countryCode   | String  |  "US"      | countryCode(String countryCode) | Helps classify products and apply internal product intelligence  |

Once the client is configured then we are ready to start parsing emails. On the `GmailClient` call `messages(@NonNull Activity activity)` to begin the message reading. This call returns a `Task<List<ScanResults>>`. The calling of this function completes a series of tasks internally, before potentially returning a `List<ScanResults>`. Upon a successful execution, the task will emit a result of `List<ScanResults>`. If no results were able to be found, then the list will be empty. If results were found then each item in the list will represent a successfully scanned receipt. Please use the ScanResults data to display information to users or use for internal use.

<br />

**EXAMPLE GMAIL READ MESSAGES**

=== "Kotlin"
    ```kotlin
    
        fun messages() {
          client.messages(requireActivity())
           .addOnSuccessListener { results ->
    
           }.addOnFailureListener {
    
           }
        }
    ```

### GmailClient Destroy Client
We always want to make sure we are adhereing to any component's lifecycle. Therefore, it is very important to call destroy within the component. This will clean up any pending calls, and allocated resources.

=== "Kotlin"
    ```kotlin
    override fun onDestroy() {
        super.onDestroy()
    
        client.close()
    }
    ```