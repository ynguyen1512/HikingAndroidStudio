package com.example.khike;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassword extends AppCompatActivity {
    private DBHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Your back icon
        }

        EditText edtOldPass = findViewById(R.id.edt_oldPass);
        EditText edtNewPass = findViewById(R.id.edt_newPass);
        EditText edtReNewPass = findViewById(R.id.edt_ReNewPass);
        Button btnSave = findViewById(R.id.btn_Save);

        // Assuming you have a method in DBHelper to get the password for a user
        dbHelper = new DBHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        String currentPassword = dbHelper.getPasswordForUser(userId);
        edtOldPass.setText(currentPassword);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = edtOldPass.getText().toString();
                String newPassword = edtNewPass.getText().toString();
                String retypePassword = edtReNewPass.getText().toString();

                if (!newPassword.equals(retypePassword)) {
                    Toast.makeText(ChangePassword.this, "New password and retype password do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!oldPassword.equals(currentPassword)) {
                    Toast.makeText(ChangePassword.this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isUpdated = dbHelper.updatePassword(userId, newPassword);

                if (isUpdated) {
                    Toast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // close the activity or redirect as needed
                } else {
                    Toast.makeText(ChangePassword.this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void back() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("USER_ID", userId);
        finish();
    }

    // Handle back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}