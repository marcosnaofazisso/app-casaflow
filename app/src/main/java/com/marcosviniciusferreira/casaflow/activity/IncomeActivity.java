package com.marcosviniciusferreira.casaflow.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.helper.DateCustom;
import com.marcosviniciusferreira.casaflow.model.Transaction;
import com.marcosviniciusferreira.casaflow.model.User;

import java.text.DecimalFormat;
import java.util.Locale;

public class IncomeActivity extends AppCompatActivity {

    private CurrencyEditText editValue;
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

        auth = FirebaseConfig.getFirebaseAuth();
        database = FirebaseConfig.getDatabase();

        initializeComponents();
        getUserTotalIncome();

        editDate.setText(DateCustom.actualDate());


        fabIncome.setOnClickListener(v -> {

            String value = transformedToString(editValue.getRawValue());
            String date = editDate.getText().toString();
            String category = editCategory.getText().toString();
            String description = editDescription.getText().toString();

            Double incomeValue = Double.parseDouble(value);


            if (validateFields()) {
                Transaction transaction = new Transaction(
                        date, category, description,
                        "INCOME", incomeValue);

                transaction.save(date);
                updatedIncome = totalIncome + incomeValue;
                updateIncome(updatedIncome);
                finish();

            }

        });


    }

    private void getUserTotalIncome() {
        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);
        DatabaseReference userRef = database.child("users").child(idUser);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                totalIncome = user.getTotalIncome();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateIncome(Double updatedIncome) {
        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);
        DatabaseReference userRef = database.child("users").child(idUser);

        userRef.child("totalIncome").setValue(updatedIncome);
    }

    private String transformedToString(Long numberLong) {
        Double number = Double.parseDouble(String.valueOf(numberLong)) / 100.0;
        return String.valueOf(number);
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

        Locale locale = new Locale("pt", "BR");
        editValue.setLocale(locale);
        editValue.setDecimalDigits(2);
    }
}