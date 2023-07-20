package com.marcosviniciusferreira.casaflow.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;

public class IntroductionsActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    private FirebaseAuth auth;
    private boolean isVisitor = false;
    private Button buttonAlreadyRegistered;
    private Button buttonIntroRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonAlreadyRegistered = findViewById(R.id.buttonAlreadyRegistered);
        buttonIntroRegister = findViewById(R.id.buttonIntroRegister);

        setButtonNextVisible(false);
        setButtonBackVisible(false);


        addSlide(
                new FragmentSlide.Builder()
                        .fragment(R.layout.intro_1)
                        .background(android.R.color.white)
                        .backgroundDark(R.color.purple_200)
                        .build()
        );
        addSlide(
                new FragmentSlide.Builder()
                        .fragment(R.layout.intro_2)
                        .background(android.R.color.white)
                        .backgroundDark(R.color.purple_200)
                        .build()
        );
        addSlide(
                new FragmentSlide.Builder()
                        .fragment(R.layout.intro_3)
                        .background(android.R.color.white)
                        .backgroundDark(R.color.purple_200)
                        .build()
        );
        addSlide(
                new FragmentSlide.Builder()
                        .fragment(R.layout.intro_4)
                        .background(android.R.color.white)
                        .backgroundDark(R.color.purple_200)
                        .build()
        );

        addSlide(new FragmentSlide.Builder()
                .fragment(R.layout.intro_register)
                .background(android.R.color.white)
                .backgroundDark(R.color.purple_200)
                .canGoForward(false)
                .build()
        );


    }

    public void startRegistering(View view) {
        startActivity(new Intent(view.getContext(), RegisterActivity.class));

    }

    public void startLogging(View view) {
        view.setBackgroundColor(Color.WHITE);
        startActivity(new Intent(view.getContext(), LoginActivity.class));

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForLoggedUser();
    }

    private void checkForLoggedUser() {
        auth = FirebaseConfig.getFirebaseAuth();
        if (auth.getCurrentUser() != null) {
            if (auth.getCurrentUser().getDisplayName() == "Visitante") {
                isVisitor = true;

            }
            openMainActivity();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("stringVisitor", String.valueOf(isVisitor));
        startActivity(intent);
        finish();

    }
}