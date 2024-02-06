package com.marcosviniciusferreira.casaflow.activity;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.marcosviniciusferreira.casaflow.R;

public class TermsOfUseActivity extends AppCompatActivity {

    private Button buttonReadTerms;

    private TextView textTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        textTerms = findViewById(R.id.textAllTermsOfUse);

        getSupportActionBar().setTitle("CasaFlow - Termos de Uso");
        getSupportActionBar().setHomeButtonEnabled(true);

        // Check for Dark Mode
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        if (isDarkMode) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_200)));
            textTerms.setTextColor(getResources().getColor(R.color.purple_500));
        }

        buttonReadTerms = findViewById(R.id.buttonReadTerms);

        buttonReadTerms.setOnClickListener(v -> {
            Intent i = new Intent(TermsOfUseActivity.this, RegisterActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            i.putExtra("termsAccepted", true);
            startActivity(i);
            finish();
        });
    }
}