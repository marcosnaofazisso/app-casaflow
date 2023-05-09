package com.marcosviniciusferreira.casaflow.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.adapter.AdapterTransactions;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.Transaction;
import com.marcosviniciusferreira.casaflow.model.User;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeName;
    private TextView generalBalance;

    private FloatingActionButton incomeButton;
    private FloatingActionButton expenseButton;
    private MaterialCalendarView calendarView;

    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userRef;
    private User user;

    private RecyclerView recyclerView;
    private AdapterTransactions adapterTransactions;
    private List<Transaction> transactions = new ArrayList<>();
    private Transaction transaction;

    private Double totalExpense = 0.0;
    private Double totalIncome = 0.0;
    private Double resumeBalance = 0.0;

    private ValueEventListener valueEventListenerUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(userName);

        initializeComponents();
        initializeCalendarSettings();

        resumeUserData();


//        adapterTransactions = new AdapterTransactions(transactions, this);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapterTransactions);
//        recyclerView.setHasFixedSize(true);

        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);

        DatabaseReference userRef = database.child("users").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(User.class);

                welcomeName.setText("Olá, " + user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        incomeButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, IncomeActivity.class));
        });

        expenseButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ExpensesActivity.class));
        });

    }

    private void resumeUserData() {

        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);

        userRef = database
                .child("users")
                .child(idUser);

        valueEventListenerUser = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    totalExpense = user.getTotalExpenses();
                    totalIncome = user.getTotalIncome();

                    resumeBalance = totalIncome - totalExpense;

                    DecimalFormat decimalFormat = new DecimalFormat("0.##");
                    String formattedBalace = decimalFormat.format(resumeBalance);

                    generalBalance.setText("R$ " + formattedBalace);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void updateBalance() {

        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);

        userRef = database
                .child("users")
                .child(idUser);

        if (transaction.getType().equals("INCOME")) {
            totalIncome -= transaction.getValue();
            userRef.child("totalIncome").setValue(totalIncome);

        }if (transaction.getType().equals("EXPENSE")) {
            totalExpense -= transaction.getValue();
            userRef.child("totalExpense").setValue(totalExpense);

        }
    }

    private void initializeCalendarSettings() {

        CharSequence[] translatedMonths = {"Janeiro", "Fevereiro", "Março", "Abril",
                "Maio", "Junho", "Julho", "Agosto",
                "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(translatedMonths);
    }

    private void getCurrentTime() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(currentDate);
    }

    private void initializeComponents() {

        welcomeName = findViewById(R.id.textMainWelcome);
        generalBalance = findViewById(R.id.textGeneralBalance);

        incomeButton = findViewById(R.id.fabAdd);
        expenseButton = findViewById(R.id.fabRemove);

        calendarView = findViewById(R.id.calendarView);

    }
}