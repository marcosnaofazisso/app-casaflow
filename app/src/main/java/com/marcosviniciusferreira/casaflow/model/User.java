package com.marcosviniciusferreira.casaflow.model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;

public class User {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private boolean hasAcceptedTermsOfUse;

    private Double totalExpense = 0.0;
    private Double totalIncome = 0.0;

    public User() {
    }

    public void save() {
        DatabaseReference database = FirebaseConfig.getDatabase();

        database.child("users")
                .child(this.id)
                .setValue(this);
    }

    public void saveVisitor() {
        DatabaseReference database = FirebaseConfig.getDatabase();

        database.child("visitors")
                .child(this.id)
                .setValue(this);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isHasAcceptedTermsOfUse() {
        return hasAcceptedTermsOfUse;
    }

    public void setHasAcceptedTermsOfUse(boolean hasAcceptedTermsOfUse) {
        this.hasAcceptedTermsOfUse = hasAcceptedTermsOfUse;
    }
}
