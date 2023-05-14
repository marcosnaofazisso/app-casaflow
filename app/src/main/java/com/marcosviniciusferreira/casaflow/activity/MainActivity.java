package com.marcosviniciusferreira.casaflow.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.adapter.AdapterTransactions;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.Transaction;
import com.marcosviniciusferreira.casaflow.model.User;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

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
    private TextView monthBalance;
    private TextView monthBalanceValue;

    private FloatingActionButton incomeButton;
    private FloatingActionButton expenseButton;
    private MaterialCalendarView calendarView;

    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private User user;
    private DatabaseReference userRef;
    private DatabaseReference transactionsRef;

    private RecyclerView recyclerView;
    private AdapterTransactions adapterTransactions;
    private List<Transaction> transactions = new ArrayList<>();
    private Transaction transaction;

    private Double totalExpense = 0.0;
    private Double totalIncome = 0.0;
    private Double resumeBalance = 0.0;


    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerTransactions;
    private String selectedMonthYear;
    private String monthToBeShown;

    CharSequence[] translatedMonths = {"Janeiro", "Fevereiro", "Março", "Abril",
            "Maio", "Junho", "Julho", "Agosto",
            "Setembro", "Outubro", "Novembro", "Dezembro"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(userName);

        initializeComponents();
        initializeCalendarSettings();


        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);
        DatabaseReference userRef = database.child("users").child(userId);

        adapterTransactions = new AdapterTransactions(transactions, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterTransactions);
        recyclerView.setHasFixedSize(true);

        CalendarDay actualDate = calendarView.getCurrentDate();
        String selectedMonth = String.format("%02d", (actualDate.getMonth() + 1));
        selectedMonthYear = selectedMonth + String.valueOf(actualDate.getYear());
        monthToBeShown = (String) translatedMonths[actualDate.getMonth()];

        resumeUserData();
        resumeMonthBalance();

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String monthSelected = String.format("%02d", (date.getMonth() + 1));
                selectedMonthYear = monthSelected + String.valueOf(date.getYear());
                monthToBeShown = (String) translatedMonths[date.getMonth()];

                transactionsRef.removeEventListener(valueEventListenerTransactions);
                getTransactions();
            }
        });

        swipeTransactions();


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

    private void swipeTransactions() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeTransaction(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    private void removeTransaction(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();

        transaction = transactions.get(position);

        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);

        transactionsRef = database
                .child("transactions")
                .child(idUser)
                .child(selectedMonthYear);

        transactionsRef.child(transaction.getKey()).removeValue();

        adapterTransactions.notifyItemRemoved(position);

        resumeBalance();

    }

    private void resumeBalance() {
        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);

        transactionsRef = database
                .child("users")
                .child(idUser);

        if (transaction.getType().equals("INCOME")) {
            totalIncome -= transaction.getValue();
            userRef.child("totalIncome").setValue(totalIncome);
        }
        if (transaction.getType().equals("EXPENSE")) {
            totalExpense -= transaction.getValue();
            userRef.child("totalExpense").setValue(totalExpense);
        }


    }

    private void getTransactions() {
        String userEmail = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(userEmail);

        transactionsRef = database
                .child("transactions")
                .child(idUser)
                .child(selectedMonthYear);

        valueEventListenerTransactions = transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Transaction currentTransaction = data.getValue(Transaction.class);
                    currentTransaction.setKey(data.getKey());
                    transactions.add(currentTransaction);

                }

                resumeMonthBalance();
                adapterTransactions.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void resumeMonthBalance() {
        Double totalMonthBalance = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("INCOME")) {
                totalMonthBalance += transaction.getValue();
            } else {
                totalMonthBalance -= transaction.getValue();
            }

        }

        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String formattedBalance = decimalFormat.format(totalMonthBalance);

        if(totalMonthBalance >= 0) {
            monthBalanceValue.setTextColor(this.getResources().getColor(R.color.green_check));
        } else {
            monthBalanceValue.setTextColor(this.getResources().getColor(R.color.red_uncheck));
        }
        monthBalance.setText("Total em " + monthToBeShown + ":");
        monthBalanceValue.setText(" R$ " + formattedBalance);


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
                    totalExpense = user.getTotalExpense();
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

    private void initializeCalendarSettings() {
        calendarView.setTitleMonths(translatedMonths);
    }

    private void initializeComponents() {

        welcomeName = findViewById(R.id.textMainWelcome);
        generalBalance = findViewById(R.id.textGeneralBalance);
        monthBalance = findViewById(R.id.textMonthBalance);
        monthBalanceValue = findViewById(R.id.textMonthBalanceValue);

        incomeButton = findViewById(R.id.fabAdd);
        expenseButton = findViewById(R.id.fabRemove);

        calendarView = findViewById(R.id.calendarView);

        recyclerView = findViewById(R.id.recyclerTransactions);

    }

    @Override
    protected void onStart() {
        super.onStart();

        resumeUserData();
        getTransactions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUser);
        transactionsRef.removeEventListener(valueEventListenerTransactions);
    }
}