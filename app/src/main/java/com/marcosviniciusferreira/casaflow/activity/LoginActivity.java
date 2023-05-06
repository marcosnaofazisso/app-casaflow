package com.marcosviniciusferreira.casaflow.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.model.User;

public class LoginActivity extends AppCompatActivity {

    private Button buttonEnter;
    private TextInputEditText textEmail;
    private TextInputEditText textPassword;
    private TextView textRegister;

    private String email;
    private String password;

    private User user;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        initComponents();

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsValidated()) {
                    user = new User();
                    user.setEmail(email);
                    user.setPassword(password);
                    signIn();
                }
            }
        });


        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void signIn() {
        auth = FirebaseConfig.getFirebaseAuth();
        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            openMainActivity();

                        } else {
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

                            Toast.makeText(LoginActivity.this,
                                    error,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
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
        buttonEnter = findViewById(R.id.buttonEntrar);
        textEmail = findViewById(R.id.inputEmail);
        textPassword = findViewById(R.id.inputPassword);
        textRegister = findViewById(R.id.textRegister);
    }
}