package com.example.khike;

import static com.example.khike.DBHelper.COLUMN_FULL_NAME;
import static com.example.khike.DBHelper.COLUMN_USER_NAME;
import static com.example.khike.DBHelper.COLUMN_USER_PASSWORD;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfile extends AppCompatActivity {

    private DBHelper dbHelper;
    private int userId;

    private Button changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        changePassword = findViewById(R.id.btn_change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directly navigate to ChangePassword activity
                Intent intent = new Intent(EditProfile.this, ChangePassword.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Your back icon
        }

        dbHelper = new DBHelper(this);

        // Assuming you're passing the user's ID to this activity
        userId = getIntent().getIntExtra("USER_ID", -1);

        EditText edtFullname = findViewById(R.id.edt_fullname);
        EditText edtUsername = findViewById(R.id.edt_username);
        EditText edtPassword = findViewById(R.id.edt_password);

        // Populate fields with user details
        Cursor userCursor = dbHelper.getUserById(userId);
        if (userCursor.moveToFirst()) {
            @SuppressLint("Range") String currentFullName = userCursor.getString(userCursor.getColumnIndex(COLUMN_FULL_NAME));
            @SuppressLint("Range") String currentUserName = userCursor.getString(userCursor.getColumnIndex(COLUMN_USER_NAME));
            @SuppressLint("Range") String currentPassword = userCursor.getString(userCursor.getColumnIndex(COLUMN_USER_PASSWORD));

            edtFullname.setText(currentFullName);
            edtUsername.setText(currentUserName);
            edtPassword.setText(currentPassword); // Corrected line
        }
        userCursor.close();



        Button btnSave = findViewById(R.id.btn_Save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String newFullname = edtFullname.getText().toString();
                String newUsername = edtUsername.getText().toString();
                String newPassword = edtPassword.getText().toString().trim();

                boolean isUpdated = dbHelper.updateFullNameAndUserName(userId, newFullname, newUsername, newPassword);

                if (isUpdated) {
                    Toast.makeText(EditProfile.this, "User details updated successfully", Toast.LENGTH_SHORT).show();

                    // Update the SharedPreferences if the username has changed
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_CREDENTIALS", MODE_PRIVATE);
                    String currentUsername = sharedPreferences.getString("CURRENT_USER", null);
                    if (currentUsername != null && !currentUsername.equals(newUsername)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CURRENT_USER", newUsername);
                        editor.apply();
                    }

                    finish();
                    // Optionally, you can redirect to another activity or reload the current activity
                } else {
                    Toast.makeText(EditProfile.this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
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