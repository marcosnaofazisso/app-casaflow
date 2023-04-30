package com.marcosviniciusferreira.casaflow.model;

import com.google.firebase.database.DatabaseReference;
import com.marcosviniciusferreira.casaflow.config.FirebaseConfig;

public class User {

    private String id;
    private String name;
    private String email;
    private String password;

    private Double totalExpenses;
    private Double totalIncome;

    public User() {
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public void save() {
        DatabaseReference database = FirebaseConfig.getDatabase();

        User toBeSaved = new User(this.id, this.name, this.email);
        database.child("users")
                .child(this.id)
                .setValue(toBeSaved);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }
}
