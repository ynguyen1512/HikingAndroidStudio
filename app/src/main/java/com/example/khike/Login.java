package com.example.khike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    DBHelper dbHelper;
    Button btnLogin, btnRegister;
    EditText etUsername, etPassword;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        dbHelper = new DBHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(view -> {
            String user, password;
            user = etUsername.getText().toString();

            password = etPassword.getText().toString();

            if(validateData()) {
                spinner.setVisibility(View.VISIBLE);

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);

                // After user login successfully
                SharedPreferences sharedPreferences = getSharedPreferences("USER_CREDENTIALS", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("CURRENT_USER", user);
                editor.apply();

            }else {
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }

        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

    }

    private boolean validateData() {
        String user = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();


        if (user.isEmpty()) {
            etUsername.setError("User is required");
            etUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if(!dbHelper.checkUsername(user)) {
            etUsername.setError("User Not Exists");
            etUsername.requestFocus();
            return false;
        }

        if(!dbHelper.checkUserPass(etUsername.getText().toString(), etPassword.getText().toString())) {
            Toast.makeText(Login.this, "User And Password Not Exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}