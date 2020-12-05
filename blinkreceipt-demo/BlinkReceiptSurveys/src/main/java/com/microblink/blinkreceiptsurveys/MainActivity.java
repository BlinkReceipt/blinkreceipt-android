package com.microblink.blinkreceiptsurveys;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import com.microblink.core.Survey;
import com.microblink.core.SurveyAnswer;
import com.microblink.core.SurveyQuestion;
import com.microblink.surveys.SurveyFragment;
import com.microblink.surveys.SurveyViewModel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurveyViewModel viewModel = new ViewModelProvider(this).get(SurveyViewModel.class);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.survey_id_container, SurveyFragment.newInstance(R.style.DemoSurvey))
                .commitAllowingStateLoss();

        viewModel.survey(createFakeSurvey());
    }

    private Survey createFakeSurvey() {
        List<SurveyAnswer> answerList = new ArrayList<>();

        answerList.add(SurveyAnswer.newBuilder().text("Hello World").build());

        SurveyQuestion question = SurveyQuestion.newBuilder().text("What is up?").answers(answerList).build();

        List<SurveyQuestion> surveyQuestions = new ArrayList<>();

        surveyQuestions.add(question);

        return Survey.newBuilder().questions(surveyQuestions).build();
    }
}