package com.marcosviniciusferreira.casaflow.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marcosviniciusferreira.casaflow.R;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;
import com.marcosviniciusferreira.casaflow.model.User;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeName;
    private TextView emailInfo;

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
        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);

        DatabaseReference userRef = database.child("users").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(User.class);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(user);
                Log.i("USER =======>>>>>", json);


                welcomeName.setText("Olá, " + user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        emailInfo.setText("Olá, " + userEmail);

    }

    private void initializeComponents() {

        welcomeName = findViewById(R.id.textMainWelcome);
        emailInfo = findViewById(R.id.textMainEmail);
    }
}