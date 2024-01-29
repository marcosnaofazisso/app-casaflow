package com.marcosviniciusferreira.casaflow.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.User;

import java.util.UUID;

public class IntroductionsActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    private FirebaseAuth auth;
    private boolean isVisitor = false;
    private Button buttonAlreadyRegistered;
    private Button buttonIntroRegister;
    private Button buttonVisitor;

    private String email;
    private String password;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonAlreadyRegistered = findViewById(R.id.buttonAlreadyRegistered);
        buttonIntroRegister = findViewById(R.id.buttonIntroRegister);
        buttonVisitor = findViewById(R.id.buttonVisitor);

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
                        .background(R.color.casaflow)
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

    public void visitorLogin(View view) {
        Boolean visitor = true;
        auth = FirebaseConfig.getFirebaseAuth();

        createVisitorCredentials();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()) {
                auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        openMainActivity(visitor);

                    } else {
                        String error = "";
                        try {
                            throw task1.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            error = "Usuário inválido. Por favor, verifique";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            error = "Dados inválidos. Por favor, tente novamente!";

                        } catch (Exception e) {
                            error = "Erro ao cadastrar usuário: " + e.getMessage();
                        }

                        Toast.makeText(view.getContext(), error, Toast.LENGTH_SHORT).show();

                    }
                });

            } else {

                String error = "";

                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    error = "Digite uma senha mais forte";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    error = "Por favor, digite um e-mail válido!";

                } catch (FirebaseAuthUserCollisionException e) {
                    error = "Conta já existente!";

                } catch (Exception e) {
                    error = "Erro ao cadastrar usuário:" + e.getMessage();
                }

                Toast.makeText(view.getContext(), error, Toast.LENGTH_SHORT).show();

            }

        });
    }

    private void openMainActivity(Boolean visitor) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (visitor) {
            intent.putExtra("stringVisitor", "true");

        } else {
            intent.putExtra("stringVisitor", "false");
        }
        startActivity(intent);
        finish();
    }

    private void createVisitorCredentials() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        email = "visitor@" + uuid + ".com";
        password = uuid;
        String visitorName = "Visitante";
        user = new User();
        user.setName(visitorName);
        user.setEmail(email);
        String idUser = Base64Custom.codeBase64(user.getEmail());
        user.setId(idUser);
        user.setPassword(password);
        user.saveVisitor();
    }
}