
# Blink Survey SDK

The Blink Survey SDK enables you to fetch and display surveys to your users, using either the stock sdk UI or your own custom UI.

## Setup
If this is your first time integrating Blink into your project please refer to the following documentation, otherwise skip to the `Dependencies` section.

Please follow the [Project Integration and Initialization](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-project-integration-and-initialization), [R8/Proguard](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#r8--proguard), and the application class/manifest step in the [Scanning Your First Receipt](https://github.com/BlinkReceipt/blinkreceipt-android/blob/master/blinkreceipt-recognizer/README.md#-scanning-your-first-receipt) sections to properly add and initialize recognizer sdk.


Dependencies

To add the sdk to your android project please follow these steps:

1. Add the following maven repository to your build.gradle or settings.gradle, depending on your implementation:

    ```groovy
    repositories {
      maven { url  "https://maven.microblink.com" }
    }
    ```

2. Add the following to your dependency section in your app `build.gradle`.

```groovy
dependencies {
    implementation(platform("com.microblink.blinkreceipt:blinkreceipt-bom:1.8.0"))

    implementation("com.microblink.blinkreceipt:blinkreceipt-surveys")
}
```

You may notice that there are Kotlin dependencies as part of this list. This is because the SDK is partially written in Kotlin. If your app does not use Kotlin that is ok you will just need to configure the app to use kotlin, but this will not require you to change programming languages as Kotlin is compatible with java.

Project build.gradle
``` groovy
buildscript {

    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10"
    }
}
```
<br />
<br />
<br />

## How To Access Surveys
Surveys are delivered to the user in one of 2 ways.

1. The "triggered" surveys which are delivered after a scan session with the the Recognizer SDK. Within the `ScanResults` object
   there will be a field called surveys. This will comprise of a `List<Survey>`.

2. The "non-triggered" surveys which are delivered via the `SurveyLoader`. This class when instantiated uses the `"com.microblink.ClientUserId"` defined
   in the AndroidManifest or set manually on the SDK to fetch surveys for that user. This too will deliver a `List<Survey>` to the user when called (if any surveys are available).

<br />
<br />
<br />

## How to Display a Survey to a User with Stock SDK UI

The Surveys SDK ships with a stock implementation of a UI to display to a user.

There are 2 main classes needed to utilize the Stock UI in the Surveys SDK

1. SurveyViewModel
2. SurveyFragment

The `SurveyViewModel` is an Android lifecycle component. It extends the Android ViewModel. This component is used as the bridge between your user's data and the stock UI. Once you have your `Survey` selected from the `List<Survey>` returned then instantiate the ViewModel and set the survey on the view model.

<br />

** NOTE: You Must Create the ViewModelProvider with the HOST Activity as the `ViewModelStoreOwner` or else this will nto work **
```java
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        ...

        Survey survey = surveys.get(0);

        SurveyViewModel viewModel = new ViewModelProvider(this).get(SurveyViewModel.class);

        viewModel.survey(survey)
    }
```

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...

        val viewModel = ViewModelProvider(this).get(SurveyViewModel::class.java).apply {
            survey(survey)

            surveyCompletionLiveData.observe(this@SurveyActivity) {
                supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss()
            }
        }
    }
```

Next, you will want to add the stock UI implemented as a Fragment into your Activity. The SurveyFragment can instantiated with the static function `newInstance()`. This static function has an overloaded implementation that takes int a `Style` attribute. To see how to customize the colors of the stock UI please refer to the [Theming the Stock UI](#theming-the-stock-ui) section of the file.

JAVA
```java
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        ...

        Survey survey = surveys.get(0);

        ...

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.survey_id_container, SurveyFragment.newInstance())
            .commitAllowingStateLoss();
    }
```

KOTLIN
```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...

        Survey survey = surveys.get(0);

        ...

        supportFragmentManager()
            .beginTransaction()
            .add(R.id.survey_id_container, SurveyFragment.newInstance())
            .commitAllowingStateLoss();
    }
```

Once these steps are completed you will see a rendered UI of the survey you have passed in. From here on out the `SurveyFragment` will handle displaying the UI and recording the user's answer for later submission. Once the user completes the survey, then the `SurveyFragment` will package their response and send it to the Blink backend for saving.

Survey Completion
How do you know when a survey is over? This is where the `SurveyViewModel` comes into play again. Grab the `LiveData<Boolean> surveyCompletionLiveData` from the `SurveyViewModel`. This livedata is your survey completion callback. If a user closes the survey or finishes taking the survey then this LiveData value will be updated with value `true`. If that value is emitted then it is safe for you to remove the Fragment for the UI.

JAVA
```java

    private void setupViewModel() {
        surveyViewModel.surveyCompletionLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean surveyComplete) {
                if (surveyComplete) {
                    getSupportFragmentManager().beginTransaction()
                        .remove(fragment) // reference to SurveyFragment
                        .commitAllowingStateLoss();
                }
            }
        });
    }
```

KOTLIN
```kotlin
    private fun setupViewModel() {
        surveyViewModel.surveyCompletionLiveData.observe(this) { surveyComplete ->
                if (surveyComplete) {
                    supportFragmentManager.beginTransaction()
                    .remove(fragment) // reference to SurveyFragment
                    .commitAllowingStateLoss()
                }
            }
    }
```

<br />
<br />

### Theming the Stock UI
The stock UI is pretty set and standard as far as UI's go but we have provided some custom theming capabilities through the SDK so that it may better integrate with your app's look and feel. To override the default theming of the SurveyFragment instantiate the fragment with your new style id reference. For example, if I have a style called `<style name="DemoSurvey" parent="BlinkSurvey">` then I can instantiate my `SurveyFragment` by calling `SurveyFragment.newInstance(R.style.DemoSurvey)`. This `DemoSurvey` will override the values of the attributes mentioned and use the default themeing from the sdk for the rest. Here is an extensive list of all the attributes you can override in the stock UI.

```xml
    <!-- MULTIPLE CHOICE ANSWER STYLE -->
    <item name="multiple_choice_button_selector"></item> <!-- Multiple Choice Answer background -->
    <item name="multiple_choice_answer_icon"></item> <!-- Multiple Choice Answer Icon -->
    <item name="multiple_choice_answer_text_color"></item> <!-- Multiple Choice Text Color -->

    <!-- FREE TEXT ANSWER STYLE -->
    <item name="free_text_background"></item> <!-- Open Ended Answer container background -->

    <!-- SURVEY CLOSE -->
    <item name="survey_close_icon">@drawable/ic_close</item> <!-- Survey Close Button on the top right-->
    <item name="survey_close_icon_tint">@drawable/blink_icon_tint</item> <!-- Survey Close Button color-->

    <!-- SURVEY QUESTION STYLE -->
    <item name="question_text_color">@color/blink_survey_black_text</item> <!-- Survey Question Text Color-->
    <item name="question_text_size">@dimen/blink_survey_question_text_size</item> <!-- Survey Question Text Size-->
    <item name="question_count_text_size">@dimen/blink_survey_question_count_text_size</item> <!-- Survey Question Counter Text Size-->
    <item name="next_question_button_selector">@drawable/blink_button_selector</item> <!-- Next Question Button color-->
    <item name="next_question_button_text_color">@drawable/blink_white_color_selector</item> <!-- Next Question Button Text Color-->
    <item name="question_multiple_choice_allowed_text_size">@dimen/blink_question_multiple_choice_allowed_text_size
    </item>

    <item name="progress_bar_background_color">@color/light_grey</item> <!-- Completed Progress color-->
    <item name="progress_bar_progress_color">@color/blink_light_blue</item> <!-- Incomplete Progress color-->
```

Here is an example of an overridden Theme in an app

```xml
  <style name="DemoSurvey" parent="BlinkSurvey">
    <item name="next_question_button_text_color">@color/purple_700</item>
    <item name="question_text_color">@color/blink_blue</item>
    <item name="survey_close_icon_tint">@drawable/red_tint_selector</item>
  </style>
```


## How to Display a Survey with a CustomUI

If you wish to use your own UI Controller for Survey handling then there are a few classes that needed in order of a successful UI. Here is a definition of them.

### Survey Controls

The Survey client is the brains of the Survey experience. This class serves 3 purposes
    a. Navigation
    b. User Response Store
    c. Submission of Survey to BlinkReceipt

It is instantiated by taking a `Survey` object into its constructor.

### Displaying Survey Questions
When initialized with the Survey the client is now ready to serve. To receive the next question to display to the user call `client.nextQuestion()`. This will return a `SurveyQuestion` to the caller. This `SurveyQuestion` contains the data needed to display the content to the user. Calling `surveyQuestion.text()` will return a String of the question that should be displayed to the user.

Now there are 2 types of questions in the Blink Survey feature. "Free Text" and "Multiple Choice" style questions which are identified by the enums `SurveyQuestionType.OPEN_ENDED` and `SurveyQuestionType.MULTIPLE_CHOICE` respectively. Use the `type()` method on the `SurveyQuestion` to correctly identify the question type. If a question is of type `SurveyQuestionType.OPEN_ENDED` then there is nothing more to figure out. Simply display some sort of editable field where users can type their response.

However, if a question is of type `SurveyQuestionType.MULTIPLE_CHOICE`, then you need to dig further and grab the multiple choice answers provided with the question. Call the `surveyQuestion.answers()` function to retrieve a list of `SurveyAnswer`. This simple data class contains a field that contains the String of the answer to display. Calling `survey.text()` will give you the answer text to display to the user in your answer group.

Within the type of multiple choice questions there are 2 types of multiple choice questions. The first type is "Single Answer" and the second type is "Multiple Answer". The first type describes a question in which a user may only select ONE of the provided answers as a response. The second type describes a question in which a user may select ONE or MORE answers as a response. To determine if a multiple choice question is one or the other then check the value of `multipleAnswers()` in the `SurveyQuestion`. If it is true then it is a "Multiple Answer" question type and if it is false then it is a "Single Answer" question type.

### Recording Survey Answers

When a user answers the survey whether it be an open-ended or mutliple choice question then the answers need to be given to the `SurveyClient`. In order to provide a response the Survey will understand you will have to use the `SurveyResponse` class. This provides a builder implementation for creation and can be instantiated by calling `SurveyResponse.newBuilder()`.

Here is how to create a Survey Response based on question type

Free Text Response
```java
    SurveyResponse response = SurveyResponse.newBuilder().freeText( answerText ).build();
```

Multiple Choice Response
```java
    List<SurveyAnswer> selectedSurveyAnswers = getUserSelectedAnswers();
    SurveyResponse response = SurveyResponse.newBuilder().surveyAnswersSelected( selectedSurveyAnswers ).build();
```

If the user answered a multiple choice question then fetch the `SurveyAnswer` objects that they selected and pass that into the builder function `surveyAnswersSelected` and call build.

Once the `SurveyResponse` object is built, then we can pass it into the client. This is done by grabbing the `SurveyQuestion` object that was used to ask the question and the newly created `SurveyResponse` object from the user's answer and call `client.recordUserResponse(surveyQuestion, surveyResponse)`.

Once an answer has been recorded calling `client.nextQuestion()` will provide the caller with a new `SurveyQuestion`. Please note that calling `client.nextQuestion()` without providing an answer to the SurveyQuestion will just repeatedly give you the same value.

### Survey Completion
When calling `nextQuestion()`, if the `SurveyQuestion` returned is null, then check if the survey is over by calling `client.surveyOver()` which returns a boolean. Odds are if null is being returned then the survey is over and there are no more questions to display to the user. In this scenario, then call `client.submit()`. There is no callback for the submit() as the client and sdk handles failure, retry, and success scenarios.
