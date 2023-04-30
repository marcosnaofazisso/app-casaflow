package com.marcosviniciusferreira.casaflow.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeName;
    private TextView emailInfo;
    private TextView todayDate;

    private TextView totalExpenses;
    private TextView totalIncome;

    private FloatingActionButton incomeButton;
    private FloatingActionButton expenseButton;

    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(userName);

        initializeComponents();

        getCurrentTime();

        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);

        DatabaseReference userRef = database.child("users").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(User.class);
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                String json = gson.toJson(user);

                welcomeName.setText("Olá, " + user.getName());
                totalExpenses.setText("Total de Despesas: R$ " + user.getTotalExpenses());
                totalIncome.setText("Total de Receitas: R$ " + user.getTotalIncome());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        incomeButton.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Funcionalidade em construção!", Toast.LENGTH_SHORT).show());

        expenseButton.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Funcionalidade em construção!", Toast.LENGTH_SHORT).show());


        emailInfo.setText(userEmail);

    }

    private void getCurrentTime() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(currentDate);
        todayDate.setText("Hoje é " + formattedDate);
    }

    private void initializeComponents() {

        welcomeName = findViewById(R.id.textMainWelcome);
        emailInfo = findViewById(R.id.textMainEmail);
        todayDate = findViewById(R.id.textTodayDate);

        totalExpenses = findViewById(R.id.textTotalExpenses);
        totalIncome = findViewById(R.id.textTotalIncome);

        incomeButton = findViewById(R.id.fabAdd);
        expenseButton = findViewById(R.id.fabRemove);
    }
}