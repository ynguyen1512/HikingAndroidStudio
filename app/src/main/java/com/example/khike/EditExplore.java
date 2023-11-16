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

import com.bumptech.glide.Glide;

import java.io.IOException;

public class EditExplore extends AppCompatActivity {
    private EditText edtNameExplore, edtLocationExplore;
    private TextView txtDateExplore, txtDateEditExplore;
    private Button btnSave, selectionImg;

    private ImageView exploreImg;
    Integer exploreId;

    private DBHelper dbHelper;
    private ExploreData exploreData;

    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_explore);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        edtNameExplore = findViewById(R.id.edt_name_explore);
        edtLocationExplore = findViewById(R.id.edt_location_explore);
        txtDateExplore = findViewById(R.id.edt_date_explore);
        txtDateEditExplore = findViewById(R.id.edt_date_edit_explore);
        exploreImg = findViewById(R.id.img_explore);
        selectionImg = findViewById(R.id.select_image_button);

        btnSave = findViewById(R.id.btn_save);

        // Retrieve the extras and set them to the Create fields
        Intent intent = getIntent();

        if (intent.hasExtra("explore_id")) {
            exploreId = intent.getIntExtra("explore_id", -1);
        }
        if (intent.hasExtra("currentDate")) {
            txtDateEditExplore.setText(intent.getStringExtra("currentDate"));
        }

        if(exploreId == -1){
            Toast.makeText(this, "Error: Explore ID not found!", Toast.LENGTH_SHORT).show();
            finish();  // close this activity and return to the previous one
            return;
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

        // Use the DBHelper to fetch explore details based on the exploreID
        dbHelper = new DBHelper(this);
        exploreData = (ExploreData) dbHelper.getExploreDataById(exploreId);

        // Populate UI elements with the retrieved data
        Glide.with(this)
                .load(exploreData.getImageBitmap())
                .into(exploreImg);

        edtNameExplore.setText(exploreData.getNameHike());
        edtLocationExplore.setText(exploreData.getLocationHike());
        txtDateExplore.setText(exploreData.getDatePost());


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {
                    // 1. Retrieve the updated data from the UI elements
                    String updatedName = edtNameExplore.getText().toString();
                    String updatedLocation = edtLocationExplore.getText().toString();
                    String updatedDate = txtDateEditExplore.getText().toString();

                    // Create an updated observation object
                    exploreData.setNameHike(updatedName);
                    exploreData.setLocationHike(updatedLocation);
                    exploreData.setDatePost(updatedDate);

                    // 2. Update the database with the new information
                    dbHelper.updateExplore(exploreId, exploreData);

                    Toast.makeText(EditExplore.this, "Explore updated successfully", Toast.LENGTH_SHORT).show();
                    // 3. Return to the previous activity
                    finish();
                } else {
                    Toast.makeText(EditExplore.this, "Error updating explore", Toast.LENGTH_SHORT).show();
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
                exploreData.setImage(bitmap);
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
            edtNameExplore.setError("Name is required");
            edtNameExplore.requestFocus();
            return false;
        }

        // Validate location
        if (location.isEmpty()) {
            edtLocationExplore.setError("Location is required");
            edtLocationExplore.requestFocus();
            return false;
        }

        // Validate image
        if (exploreImg.getDrawable() == null) {
            Toast.makeText(EditExplore.this, "Please select an image", Toast.LENGTH_SHORT).show();
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