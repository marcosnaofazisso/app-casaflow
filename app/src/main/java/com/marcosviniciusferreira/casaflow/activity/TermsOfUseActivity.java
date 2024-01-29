package com.marcosviniciusferreira.casaflow.activity;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.marcosviniciusferreira.casaflow.R;

public class TermsOfUseActivity extends AppCompatActivity {

    private Button buttonReadTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setTitle("CasaFlow - Termos de Uso");
        getSupportActionBar().setHomeButtonEnabled(true);

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