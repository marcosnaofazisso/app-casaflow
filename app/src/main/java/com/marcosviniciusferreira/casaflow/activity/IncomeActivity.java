package com.marcosviniciusferreira.casaflow.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.Transaction;

public class IncomeActivity extends AppCompatActivity {

    private EditText editValue;
    private TextInputEditText editDate, editCategory, editDescription;
    private FloatingActionButton fabIncome;

    private Double totalIncome;
    private Double updatedIncome;

    private FirebaseAuth auth;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        initializeComponents();

        fabIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = editValue.getText().toString();
                String date = editDate.getText().toString();
                String category = editCategory.getText().toString();
                String description = editDescription.getText().toString();

                if (validateFields()) {
                    Transaction transaction = new Transaction(
                            date, category, description,
                            "INCOME", Double.parseDouble(value));

                    updatedIncome = totalIncome + Double.parseDouble(value);
                    updateIncome(updatedIncome);

//                    finish();

                }

            }
        });


    }

    private void updateIncome(Double updatedIncome) {
        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);
        DatabaseReference userRef = database.child("users").child(idUser);

        userRef.child("totalIncome").setValue(updatedIncome);
    }

    private boolean validateFields() {

        String value = editValue.getText().toString();
        String date = editDate.getText().toString();
        String category = editCategory.getText().toString();
        String description = editDescription.getText().toString();

        if (value.isEmpty() ||
                date.isEmpty() ||
                category.isEmpty() ||
                description.isEmpty()) {
            Toast.makeText(IncomeActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    private void initializeComponents() {

        editValue = findViewById(R.id.editIncomeValue);
        editDate = findViewById(R.id.editIncomeDate);
        editCategory = findViewById(R.id.editIncomeCategory);
        editDescription = findViewById(R.id.editIncomeDescription);
        fabIncome = findViewById(R.id.fabIncome);
    }
}