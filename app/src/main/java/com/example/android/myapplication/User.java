package com.example.android.myapplication;

public class User {
    public  String email;
    public  String name;
    public String type;

    public User(String email, String name, String type) {

        this.email = email;
        this.name = name;
        this.type = type;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
