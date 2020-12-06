package com.blinkreceipt.development;

import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import com.microblink.core.AccessToken;
import com.microblink.core.AccessTokenManager;
import com.microblink.surveys.SurveyFragment;
import com.microblink.surveys.SurveyLoader;
import com.microblink.surveys.SurveyViewModel;
import org.json.JSONObject;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurveyViewModel viewModel = new ViewModelProvider(this).get(SurveyViewModel.class);

        findViewById(R.id.load_survey_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SurveyLoader().loadSurveys()
                        .addOnSuccessListener(surveys -> {
                            if (surveys != null && !surveys.isEmpty()) {
                                viewModel.survey(surveys.get(0));
                            } else {
                                Toast.makeText(MainActivity.this, "RESPONSE NULL OR EMPTY", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(
                        e -> Toast.makeText(MainActivity.this, "RESPONSE ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.survey_id_container, SurveyFragment.newInstance(R.style.DemoSurvey))
                .commitAllowingStateLoss();
    }
}