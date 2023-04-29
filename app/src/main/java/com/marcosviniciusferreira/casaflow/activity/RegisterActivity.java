package com.marcosviniciusferreira.casaflow.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marcosviniciusferreira.casaflow.R;

public class RegisterActivity extends AppCompatActivity {

    private Button buttonRegister;
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        initializeComponents();


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fieldValidation();
            }
        });

    }

    private void fieldValidation() {
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String passwordConfirm = editPasswordConfirmation.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeComponents() {
        buttonRegister = findViewById(R.id.buttonRegister);
        editName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editPasswordConfirmation = findViewById(R.id.editTextPasswordConfirmation);
    }
}