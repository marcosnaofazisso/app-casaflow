package com.marcosviniciusferreira.casaflow.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
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

public class ExpensesActivity extends AppCompatActivity {

    private CurrencyEditText editValue;
    private TextInputEditText editDate, editCategory, editDescription;
    private FloatingActionButton fabExpense;

    private Double totalExpenses;
    private Double updatedExpenses;

    private FirebaseAuth auth;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        auth = FirebaseConfig.getFirebaseAuth();
        database = FirebaseConfig.getDatabase();

        initializeComponents();
        getUserTotalExpenses();

        editDate.setText(DateCustom.actualDate());

        fabExpense.setOnClickListener(v -> {

            String value = transformedToString(editValue.getRawValue());
            String date = editDate.getText().toString();
            String category = editCategory.getText().toString();
            String description = editDescription.getText().toString();

            Double expenseValue = Double.parseDouble(value);

            if (validateFields()) {
                Transaction transaction = new Transaction(
                        date, category, description,
                        "EXPENSE", expenseValue);

                transaction.save(date);
                updatedExpenses = totalExpenses + expenseValue;
                updateExpenses(updatedExpenses);
                finish();

            }

        });


    }

    private String transformedToString(Long numberLong) {
        Double number = Double.parseDouble(String.valueOf(numberLong)) / 100.0;
        return String.valueOf(number);
    }

    private void getUserTotalExpenses() {
        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);
        DatabaseReference userRef = database.child("users").child(idUser);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                totalExpenses = user.getTotalExpense();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateExpenses(Double updatedExpenses) {
        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);
        DatabaseReference userRef = database.child("users").child(idUser);

        userRef.child("totalExpense").setValue(updatedExpenses);
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
            Toast.makeText(ExpensesActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    private void initializeComponents() {

        editValue = findViewById(R.id.editExpenseValue);
        editDate = findViewById(R.id.editExpenseDate);
        editCategory = findViewById(R.id.editExpenseCategory);
        editDescription = findViewById(R.id.editExpenseDescription);
        fabExpense = findViewById(R.id.fabExpenses);

        Locale locale = new Locale("pt", "BR");
        editValue.setLocale(locale);
        editValue.setDecimalDigits(2);
    }
}