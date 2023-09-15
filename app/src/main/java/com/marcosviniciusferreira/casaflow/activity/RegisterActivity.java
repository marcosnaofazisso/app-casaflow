package com.marcosviniciusferreira.casaflow.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
    private TextInputEditText editPhone;
    private TextInputEditText editPassword;
    private TextInputEditText editPasswordConfirmation;
    private CheckBox checkBoxTerms;
    private TextView buttonTermsOfUse;

    private String name;
    private String email;
    private String phone;
    private String password;
    private String passwordConfirm;
    private boolean termIsChecked;

    private FirebaseAuth auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        initializeComponents();

        buttonRegister.setOnClickListener(v -> fieldValidations());

        buttonTermsOfUse.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, TermsOfUseActivity.class));
        });

    }

    private void fieldValidations() {
        name = editName.getText().toString();
        email = editEmail.getText().toString();
        phone = editPhone.getText().toString();
        password = editPassword.getText().toString();
        passwordConfirm = editPasswordConfirmation.getText().toString();
        termIsChecked = checkBoxTerms.isChecked();

        if (allFieldsFilled() && passwordsMatches()) {
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setHasAcceptedTermsOfUse(termIsChecked);
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
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
            return false;
        } else if (!termIsChecked) {
            Toast.makeText(this, "É necessário aceitar os Termos de Uso", Toast.LENGTH_LONG).show();
            return false;

        } else {
            return true;
        }

    }

    public void registerNewUser() {
        auth = FirebaseConfig.getFirebaseAuth();

        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this,
                        task -> {

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

                        });
    }

    private void initializeComponents() {
        buttonRegister = findViewById(R.id.buttonRegister);
        editName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.editTextEmail);
        editPhone = findViewById(R.id.editTextPhone);
        editPassword = findViewById(R.id.editTextPassword);
        editPasswordConfirmation = findViewById(R.id.editTextConfirmPassword);
        checkBoxTerms = findViewById(R.id.checkBoxTermsOfUse);
        buttonTermsOfUse = findViewById(R.id.buttonTermsOfUse);
    }
}