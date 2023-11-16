package com.example.khike;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateObservation extends AppCompatActivity {
    private TextView  txtTitleObv, txtDesObv;
    private EditText edtNameObservation, edtTime, edtDate;
    private ImageView observationImg;
    private List<Pair<EditText, EditText>> dynamicFields = new ArrayList<>();

    private static final int SELECT_PICTURE = 1;
    private Button selectionImg, btnSave,btnAdd;
    private Integer retrievedId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_observation);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Your back icon
        }

        edtNameObservation =findViewById(R.id.edt_name_observation);
        edtDate = findViewById(R.id.edt_date_observation);
        edtTime = findViewById(R.id.edt_time_observation);
        observationImg = findViewById(R.id.img_observation);
        selectionImg = findViewById(R.id.select_image_button);
        txtTitleObv = findViewById(R.id.txt_title_obv);
        txtDesObv = findViewById(R.id.txt_des_obv);
        final LinearLayout container = findViewById(R.id.linear_layout);
        btnAdd = findViewById(R.id.btn_add_observation);
        btnSave = findViewById(R.id.btn_save);

        // Retrieve the extras and set them to the EditText fields
        Intent intent = getIntent();

        if (intent.hasExtra("MY_ID_KEY")) {
            retrievedId = intent.getIntExtra("MY_ID_KEY", -1);
        }
        if (intent.hasExtra("currentDate") && intent.hasExtra("currentTime")) {
            edtDate.setText(intent.getStringExtra("currentDate"));
            edtTime.setText(intent.getStringExtra("currentTime"));
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtTitleObv.getVisibility() == View.GONE) {
                    txtTitleObv.setVisibility(View.VISIBLE);
                    txtDesObv.setVisibility(View.VISIBLE);
                } else {
                    txtTitleObv.setVisibility(View.GONE);
                    txtDesObv.setVisibility(View.GONE);
                }

                // Create a new horizontal LinearLayout
                LinearLayout horizontalLayout = new LinearLayout(CreateObservation.this);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams horizontalParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                horizontalLayout.setLayoutParams(horizontalParams);

                // Create the two EditText fields
                EditText field1 = new EditText(CreateObservation.this);
                EditText field2 = new EditText(CreateObservation.this);

                // Layout parameters for the fields
                LinearLayout.LayoutParams fieldParams1 = new LinearLayout.LayoutParams(
                        0, // 0 width means it will take weight into consideration
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f // weight of 1 means this field will take up half the available width
                );

                LinearLayout.LayoutParams fieldParams2 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );

                // Add some margin if needed
                fieldParams1.setMargins(0, 0, 16, 0); // 16dp margin to the right of field1
                field1.setLayoutParams(fieldParams1);
                field2.setLayoutParams(fieldParams2);

                // Add fields to the horizontal LinearLayout
                horizontalLayout.addView(field1);
                horizontalLayout.addView(field2);

                // Add the fields to the list for future reference
                dynamicFields.add(new Pair<>(field1, field2));
                // Add the LinearLayout to the main container
                container.addView(horizontalLayout);
            }
        });


        View.OnClickListener imagePickerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        };

        // Assign the same listener to both the ImageView and Button
        observationImg.setOnClickListener(imagePickerListener);
        selectionImg.setOnClickListener(imagePickerListener);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {

                    // Retrieve data from UI components
                    String name = edtNameObservation.getText().toString().trim();
                    String date = edtDate.getText().toString().trim();
                    String time = edtTime.getText().toString().trim();


                    // Convert ImageView's bitmap into a Bitmap object
                    observationImg.setDrawingCacheEnabled(true);
                    observationImg.buildDrawingCache();
                    Bitmap imageBitmap = observationImg.getDrawingCache();


                    StringBuilder sb = new StringBuilder();

                    // Loop through the dynamic fields to gather the entered data
                    for (Pair<EditText, EditText> fieldPair : dynamicFields) {
                        String title = fieldPair.first.getText().toString().trim();
                        String description = fieldPair.second.getText().toString().trim();

                        // Concatenate the title and description and add it to the StringBuilder
                        if (!title.isEmpty() && !description.isEmpty()) {
                            sb.append(title).append(": ").append(description).append("\n");
                        }
                    }

                    List<Pair<String, String>> titlesAndDescriptions = convertStringToList(sb.toString());
                    String combinedNotes = convertListToString(titlesAndDescriptions);



                    // Use DBHelper to save the observation into the database
                    DBHelper dbHelper = new DBHelper(CreateObservation.this);
                    dbHelper.addObservation(retrievedId, name, date, time, combinedNotes, imageBitmap);
                    Toast.makeText(CreateObservation.this, "Observation saved successfully.", Toast.LENGTH_LONG).show();
                    finish();  // Optionally, return to the previous activity after saving
                } else {
                    Toast.makeText(CreateObservation.this, "Failed to save observation.", Toast.LENGTH_LONG).show();
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
                observationImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void back() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("MY_ID_KEY", -1);
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

    private boolean validateData() {
        String name = edtNameObservation.getText().toString().trim();

        // Validate date
        if (name.isEmpty()) {
            edtNameObservation.setError("Date is required");
            edtNameObservation.requestFocus();
            return false;
        }


        // Validate image
        if (observationImg.getDrawable() == null) {
            Toast.makeText(CreateObservation.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private List<Pair<String, String>> convertStringToList(String combinedNotes) {
        List<Pair<String, String>> result = new ArrayList<>();

        // Splitting the combinedNotes string into separate notes based on newline
        String[] separateNotes = combinedNotes.split("\n");

        for (String note : separateNotes) {
            // Splitting each note into title and description based on the ": " delimiter
            String[] parts = note.split(": ", 2);
            if (parts.length == 2) { // Ensuring that each note has both title and description
                result.add(new Pair<>(parts[0], parts[1]));
            }
        }

        return result;
    }

    private String convertListToString(List<Pair<String, String>> list) {
        StringBuilder sb = new StringBuilder();

        for (Pair<String, String> pair : list) {
            sb.append(pair.first).append(": ").append(pair.second).append("\n");
        }

        return sb.toString();
    }




}


