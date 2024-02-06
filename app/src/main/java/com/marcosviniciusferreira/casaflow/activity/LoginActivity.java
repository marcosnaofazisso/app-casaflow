package com.marcosviniciusferreira.casaflow.activity;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.User;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private Button buttonEnter;
    private TextInputEditText textEmail;

    private TextInputEditText textPassword;
    private TextView textRegister;
    private TextView textVisitor;
    private ProgressBar loginProgressBar;

    private String email;
    private String password;

    private User user;
    private FirebaseAuth auth;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();
        initComponents();

        // Check for Dark Mode
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        if (isDarkMode) {
            textEmail.setBackgroundColor(getResources().getColor(R.color.purple_200));
            textPassword.setBackgroundColor(getResources().getColor(R.color.purple_200));

            textEmail.setHintTextColor(getResources().getColor(R.color.purple_500));
            textPassword.setHintTextColor(getResources().getColor(R.color.purple_500));
        } else {
            textEmail.setBackgroundColor(getResources().getColor(R.color.casaflow));
            textPassword.setBackgroundColor(getResources().getColor(R.color.casaflow));

        }

        buttonEnter.setOnClickListener(v -> {
            isLoading = true;
            if (fieldsValidated()) {
                user = new User();
                user.setEmail(email);
                user.setPassword(password);
                signIn(false);
            }
        });


        textRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        textVisitor.setOnClickListener(v -> {
            isLoading = true;
            signIn(true);
        });

        textVisitor.setVisibility(View.GONE);
    }

    private void signIn(Boolean visitor) {
        auth = FirebaseConfig.getFirebaseAuth();
        handleLoadingProgressBar();
        if (!visitor) {
            auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        isLoading = false;
                        openMainActivity(visitor);

                    } else {
                        isLoading = false;
                        String error = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            error = "Usuário inválido. Por favor, verifique";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            error = "Dados inválidos. Por favor, tente novamente!";

                        } catch (Exception e) {
                            error = "Erro ao cadastrar usuário: " + e.getMessage();
                        }

                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {

            createVisitorCredentials();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    isLoading = false;
                                    openMainActivity(visitor);

                                } else {
                                    isLoading = false;
                                    String error = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        error = "Usuário inválido. Por favor, verifique";
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        error = "Dados inválidos. Por favor, tente novamente!";

                                    } catch (Exception e) {
                                        error = "Erro ao cadastrar usuário: " + e.getMessage();
                                    }

                                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();

                                }
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

                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();

                    }

                }

            });
            handleLoadingProgressBar();

        }
    }

    private void handleLoadingProgressBar() {
        if (isLoading) loginProgressBar.setVisibility(View.VISIBLE);
        else loginProgressBar.setVisibility(View.GONE);
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

    private boolean fieldsValidated() {
        email = textEmail.getText().toString();
        password = textPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) return true;
        else {
            Toast.makeText(this, "Preencha os campos para realizar login", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void initComponents() {
        buttonEnter = findViewById(R.id.buttonLogin);
        textEmail = findViewById(R.id.inputEmail);
        textPassword = findViewById(R.id.inputPassword);
        textRegister = findViewById(R.id.textRegister);
        textVisitor = findViewById(R.id.textVisitor);
        loginProgressBar = findViewById(R.id.loginProgressBar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        isLoading = false;
    }
}