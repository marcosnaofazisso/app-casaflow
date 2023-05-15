package com.marcosviniciusferreira.casaflow.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.User;

public class RegisterActivity extends AppCompatActivity {

    private Button buttonRegister;
    private TextInputEditText editName;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private TextInputEditText editPasswordConfirmation;

    private String name;
    private String email;
    private String password;
    private String passwordConfirm;

    private FirebaseAuth auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        initializeComponents();


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldValidations();


            }
        });

    }

    private void fieldValidations() {
        name = editName.getText().toString();
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();
        passwordConfirm = editPasswordConfirmation.getText().toString();

        if (allFieldsFilled() && passwordsMatches()) {
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);

            registerNewUser();

        }

    }

    private boolean passwordsMatches() {
        if (password.equals(passwordConfirm)) {
            return true;
        } else {
            Toast.makeText(this, "Senha e confirmação precisam ser iguais!", Toast.LENGTH_SHORT).show();
            return false;

        }
    }

    private boolean allFieldsFilled() {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    public void registerNewUser() {
        auth = FirebaseConfig.getFirebaseAuth();

        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    String idUser = Base64Custom.codeBase64(user.getEmail());
                                    user.setId(idUser);
                                    user.save();
                                    finish();

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

                                    Toast.makeText(RegisterActivity.this,
                                            error,
                                            Toast.LENGTH_SHORT).show();

                                }

                            }

                        });
    }

    private void initializeComponents() {
        buttonRegister = findViewById(R.id.buttonRegister);
        editName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editPasswordConfirmation = findViewById(R.id.editTextConfirmPassword);
    }
}