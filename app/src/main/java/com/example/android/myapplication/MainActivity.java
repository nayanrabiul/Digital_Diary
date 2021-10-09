package com.example.android.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity  {
    EditText etRegEmail;
    EditText etRegPassword;
    EditText name;
    TextView tvRegisterHere;
    Spinner mSpinner;
    Button btnSignin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        }
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            mAuth = FirebaseAuth.getInstance();

            etRegEmail = findViewById(R.id.email);
            etRegPassword = findViewById(R.id.password);
            tvRegisterHere = findViewById(R.id.register);

            btnSignin = findViewById(R.id.signIn);

            btnSignin.setOnClickListener(view -> {
                loginUser();
            });
            btnSignin.setOnClickListener(view -> {
                loginUser();
            });

            tvRegisterHere.setOnClickListener(view ->{
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            });
        }

        private void loginUser(){
        if(mAuth.getCurrentUser() != null){
            Toast.makeText(getApplicationContext(), "User is logged in", Toast.LENGTH_LONG).show();
        }else{
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
                etRegEmail.setError("Email cannot be empty");
                etRegEmail.requestFocus();
            }else if (TextUtils.isEmpty(password)){
                etRegPassword.setError("Password cannot be empty");
                etRegPassword.requestFocus();
            }else{
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(getApplicationContext(), "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }}
    }