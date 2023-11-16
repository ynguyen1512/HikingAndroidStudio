package com.example.khike;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CreateExplore extends AppCompatActivity {
    private EditText edtNameExplore, edtLocationExplore;
    private TextView txtDateExplore;
    private Button btnSave, selectionImg;

    private ImageView exploreImg;

    private static final int SELECT_PICTURE = 1;

    private Integer retrievedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_explore);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        edtNameExplore = findViewById(R.id.edt_name_explore);
        edtLocationExplore = findViewById(R.id.edt_location_explore);
        txtDateExplore = findViewById(R.id.edt_date_explore);
        exploreImg = findViewById(R.id.img_explore);
        selectionImg = findViewById(R.id.select_image_button);

        btnSave = findViewById(R.id.btn_save);

        // Retrieve the extras and set them to the Create fields
        Intent intent = getIntent();

        if (intent.hasExtra("explore_id")) {
            retrievedId = intent.getIntExtra("explore_id", -1);
        }
        if (intent.hasExtra("currentDate")) {
            txtDateExplore.setText(intent.getStringExtra("currentDate"));
        }


        View.OnClickListener imagePickerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        };

        // Assign the same listener to both the ImageView and Button
        exploreImg.setOnClickListener(imagePickerListener);
        selectionImg.setOnClickListener(imagePickerListener);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(validateData()) {
                    String name = edtNameExplore.getText().toString().trim();
                    String location = edtLocationExplore.getText().toString().trim();
                    String date = txtDateExplore.getText().toString().trim();

                    // Convert ImageView's bitmap into a Bitmap object
                    exploreImg.setDrawingCacheEnabled(true);
                    exploreImg.buildDrawingCache();
                    Bitmap imageBitmap = exploreImg.getDrawingCache();

                    DBHelper dbHelper = new DBHelper(CreateExplore.this);
                    dbHelper.addExplore(name,location, date, imageBitmap);
                    Toast.makeText(CreateExplore.this, "Explore saved successfully.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CreateExplore.this, "Failed to save explore.", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                exploreImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateData() {
        String name = edtNameExplore.getText().toString().trim();
        String location = edtNameExplore.getText().toString().trim();

        // Validate name
        if (name.isEmpty()) {
            edtNameExplore.setError("Date is required");
            edtNameExplore.requestFocus();
            return false;
        }

        // Validate location
        if (location.isEmpty()) {
            edtLocationExplore.setError("Date is required");
            edtLocationExplore.requestFocus();
            return false;
        }

        // Validate image
        if (exploreImg.getDrawable() == null) {
            Toast.makeText(CreateExplore.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void back() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("explore_id", -1);
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