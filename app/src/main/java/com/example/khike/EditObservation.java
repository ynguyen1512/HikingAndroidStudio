package com.example.khike;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditObservation extends AppCompatActivity {
    private ImageView ObservationImage;
    private EditText ObservationName;
    private TextView ObservationDateCreated, ObservationDateEdit;
    private TextView ObservationTimeCreated, ObservationTimeEdit;
    private EditText Title;
    private EditText Description;
    private static final int SELECT_PICTURE = 1;
    private Button btnSave, btnSelect;

    private DBHelper dbHelper;
    private ObservationData ObsData;
    private int observationId;

    private RecyclerView recyclerView;
    private List<Pair<String, String>> titlesAndDescriptions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_observation);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Your back icon
        }


        // Initialize UI elements
        ObservationImage = findViewById(R.id.img_observation);
        ObservationName = findViewById(R.id.txt_name_observation);
        ObservationDateCreated = findViewById(R.id.txt_date_observation);
        ObservationTimeCreated = findViewById(R.id.txt_time_observation);
        btnSave = findViewById(R.id.btn_Save);
        btnSelect = findViewById(R.id.btn_select_image);
        ObservationDateEdit = findViewById(R.id.txt_date_observation_edit);
        ObservationTimeEdit = findViewById(R.id.txt_time_observation_edit);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });


        Intent intent = getIntent();
        observationId = intent.getIntExtra("observation_id", -1);
        ObservationDateEdit.setText(intent.getStringExtra("currentDate"));
        ObservationTimeEdit.setText(intent.getStringExtra("currentTime"));

        if (observationId == -1) {
            Toast.makeText(this, "Error: Hike ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

            // Use the DBHelper to fetch hike details based on the hikeId
            dbHelper = new DBHelper(this);
            ObsData = (ObservationData) dbHelper.getObservationDataById(observationId);

            // Populate UI elements with the retrieved data
            Glide.with(this)
                    .load(ObsData.getImageBitmap())
                    .into(ObservationImage);

            ObservationName.setText(ObsData.getName());
            ObservationDateCreated.setText(ObsData.getDate());
            ObservationTimeCreated.setText(ObsData.getTime());

            String[] titles = new String[]{ObsData.getTitles()};
            String[] descriptions = new String[]{ObsData.getDescriptions()};

        GridLayout gridLayout = findViewById(R.id.myGridLayout);

        if (titles.length == descriptions.length) {
            for (int i = 0; i < titles.length; i++) {
                EditText titleTextView = new EditText(this);
                titleTextView.setText(titles[i]);
                GridLayout.LayoutParams titleParams = new GridLayout.LayoutParams();
                titleParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                titleParams.width = 0;
                titleParams.columnSpec = GridLayout.spec(0, 1f);
                titleParams.rowSpec = GridLayout.spec(i);
                titleTextView.setLayoutParams(titleParams);
                gridLayout.addView(titleTextView);

                EditText descTextView = new EditText(this);
                descTextView.setText(descriptions[i]);
                GridLayout.LayoutParams descParams = new GridLayout.LayoutParams();
                descParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                descParams.width = 0;
                descParams.columnSpec = GridLayout.spec(1, 1f);
                descParams.rowSpec = GridLayout.spec(i);
                descTextView.setLayoutParams(descParams);
                gridLayout.addView(descTextView);

            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {
                    // 1. Retrieve the updated data from the UI elements
                    String updatedName = ObservationName.getText().toString().trim();
                    String updatedDate = ObservationDateEdit.getText().toString().trim();
                    String updatedTime = ObservationTimeEdit.getText().toString().trim();

                    List<Pair<String, String>> titlesAndDescriptions = new ArrayList<>();

                    // Assume an even number of EditText children in the GridLayout,
                    // alternating between title and description.
                    for (int i = 0; i < gridLayout.getChildCount(); i += 2) {
                        EditText titleEditText = (EditText) gridLayout.getChildAt(i);
                        EditText descEditText = (EditText) gridLayout.getChildAt(i + 1);

                        String title = titleEditText.getText().toString();
                        String desc = descEditText.getText().toString();

                        titlesAndDescriptions.add(new Pair<>(title, desc));
                    }

                    String combinedNotes = convertListToString(titlesAndDescriptions);

                    // Create an updated observation object
                    ObsData.setId(observationId);
                    ObsData.setName(updatedName);
                    ObsData.setDate(updatedDate);
                    ObsData.setTime(updatedTime);
                    ObsData.setTitlesAndDescriptions(titlesAndDescriptions);
                    ObsData.setNotes(combinedNotes); // Assuming a setNotes method is available

                    // 2. Update the database with the new information
                    dbHelper.updateObservation(observationId,ObsData);

                    Toast.makeText(EditObservation.this, "Observation updated successfully", Toast.LENGTH_SHORT).show();
                    // 3. Return to the previous activity
                    finish();
                } else {
                    Toast.makeText(EditObservation.this, "Error updating observation", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            if(selectedImage == null) {
                Toast.makeText(this, "ERROR_IMAGE_NOT_FOUND", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                if(ObsData != null) {
                    ObsData.setImage(bitmap);
                }
                ObservationImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String convertListToString(List<Pair<String, String>> list) {
        StringBuilder sb = new StringBuilder();

        for (Pair<String, String> pair : list) {
            sb.append(pair.first).append(": ").append(pair.second).append("\n");
        }

        return sb.toString();
    }

    private boolean validateData() {
        String name = ObservationName.getText().toString().trim();

        // Validate date
        if (name.isEmpty()) {
            ObservationName.setError("Date is required");
            ObservationName.requestFocus();
            return false;
        }


        // Validate image
        if (ObservationImage.getDrawable() == null) {
            Toast.makeText(EditObservation.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void back() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("observation_id", -1);
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