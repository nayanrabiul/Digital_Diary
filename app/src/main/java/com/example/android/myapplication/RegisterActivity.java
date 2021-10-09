package com.example.android.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {
    EditText etRegEmail;
    EditText etRegPassword;
    EditText name;
    String[] country = { "Student", "Teacher"};
    Spinner mSpinner;
    Button btnRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
            Toast.makeText(RegisterActivity.this, "User is logged in", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();
        etRegEmail = findViewById(R.id.editEmail);
        etRegPassword = findViewById(R.id.editPass);
        btnRegister =  findViewById(R.id.register1);
        name = findViewById(R.id.editName1);
        mSpinner = findViewById(R.id.spinner1);
        progressBar = findViewById(R.id.progressbar1);

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.spinner1);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createUser();
            }
        });

    }
    private void createUser(){
        progressBar.setVisibility(View.VISIBLE);
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        Log.w("xzc",email+password);

        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        User user = new User(email,name.getText().toString().trim(),mSpinner.getSelectedItem().toString().trim());
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "registration_success", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "registration_error", Toast.LENGTH_LONG).show();                                }
                            }
                        });
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, "registration_error", Toast.LENGTH_LONG).show();                    }
                }
            });
        }

    }
}