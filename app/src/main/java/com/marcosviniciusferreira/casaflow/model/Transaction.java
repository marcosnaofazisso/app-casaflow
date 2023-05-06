package com.marcosviniciusferreira.casaflow.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;
import com.marcosviniciusferreira.casaflow.helper.Base64Custom;

public class Transaction {

    private String date;
    private String category;
    private String description;
    private String type;
    private double value;

    private String key;

    public Transaction() {
    }

    public void save() {

        FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
        String idUser = Base64Custom.codeBase64(auth.getCurrentUser().getEmail());

        DatabaseReference database = FirebaseConfig.getDatabase();
        database.child("transactions")
                .child(idUser)
                .push()
                .setValue(this);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
