package com.blinkreceipt.surveys;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.blinkreceipt.surveys.databinding.ActivityMainBinding;
import com.microblink.surveys.SurveyFragment;
import com.microblink.surveys.SurveyLoader;
import com.microblink.surveys.SurveyViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(this.getWindow());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() |
                            WindowInsetsCompat.Type.displayCutout()
            );
            // Apply the insets as padding to the view. Here, set all the dimensions
            // as appropriate to your layout. You can also update the view's margin if
            // more appropriate.
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);

            // Return CONSUMED if you don't want the window insets to keep passing down
            // to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });
        ViewGroupCompat.installCompatInsetsDispatch(rootView);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.getWindow().setStatusBarContrastEnforced(true);
        }

        SurveyViewModel viewModel = new ViewModelProvider(this).get(SurveyViewModel.class);

        Button loadSurveyBtn = findViewById(R.id.load_survey_btn);

        loadSurveyBtn.setOnClickListener(v -> new SurveyLoader().load()
                .addOnSuccessListener(surveys -> {
                    if (surveys != null && !surveys.isEmpty()) {
                        loadSurveyBtn.setVisibility(View.GONE);

                        viewModel.survey(surveys.get(0));
                    } else {
                        Toast.makeText(MainActivity.this, "RESPONSE NULL OR EMPTY", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                e -> Toast.makeText(MainActivity.this, "RESPONSE ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show()));

        SurveyFragment surveyFragment = SurveyFragment.newInstance(R.style.DemoSurvey);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.survey_id_container, surveyFragment)
                .commitAllowingStateLoss();

        viewModel.surveyCompletionLiveData().observe(this, isSurveyComplete -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(surveyFragment)
                    .commitAllowingStateLoss();

            loadSurveyBtn.setVisibility(View.VISIBLE);
        });
    }
}