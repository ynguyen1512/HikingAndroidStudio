package com.example.khike;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    EditText etFullname, etUser, etPassword, etRePassword;
    Button btnRegister;
    DBHelper dbHelper;
    private ProgressBar spinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = (ProgressBar)findViewById(R.id.progressBar2);
        spinner.setVisibility(View.GONE);

        etFullname = findViewById(R.id.etFullname);
        etUser = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper = new DBHelper(this);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()) {
                    spinner.setVisibility(View.VISIBLE);
                    String fullname, user, password;
                    fullname = etFullname.getText().toString();
                    user = etUser.getText().toString();
                    password = etPassword.getText().toString();

                    boolean registeredSuccess = dbHelper.insertUser(fullname, user, password);

                    if(registeredSuccess)
                        Toast.makeText(Register.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Register.this, "User Registered Failed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(Register.this, "Please correct the errors", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private boolean validateData() {
        String fullname = etFullname.getText().toString().trim();
        String user = etUser.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String rePassword = etRePassword.getText().toString().trim();

        if (fullname.isEmpty()) {
            etFullname.setError("FullName is required");
            etFullname.requestFocus();
            return false;
        }

        if (user.isEmpty()) {
            etUser.setError("User is required");
            etUser.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (rePassword.isEmpty()) {
            etRePassword.setError("RePassword is required");
            etRePassword.requestFocus();
            return false;
        }

        if(!password.equals(rePassword)) {
            etRePassword.setError("Passwords do not match");
            etRePassword.requestFocus();
            return false;
        }

        if(dbHelper.checkUsername(user)) {
            etUser.setError("User Already Exists");
            etUser.requestFocus();
            return false;
        }

        return true;
    }
}