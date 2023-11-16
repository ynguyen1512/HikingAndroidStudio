package com.example.khike;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailHiking extends AppCompatActivity {

    private ImageView hikeImageView;
    private TextView nameTextView;
    private TextView locationTextView;
    private TextView levelTextView;
    private TextView lengthTextView;
    private TextView parkingTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;

    private Button btnModify, btnDelete, btnConversation;

    private DBHelper dbHelper;

    private static final int EDIT_HIKE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_hike);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Your back icon
        }

        // Initialize UI elements
        hikeImageView = findViewById(R.id.img_hike);
        nameTextView = findViewById(R.id.txt_name_hike);
        locationTextView = findViewById(R.id.txt_location_hike);
        levelTextView = findViewById(R.id.txt_level_hike);
        lengthTextView = findViewById(R.id.txt_length_hike);
        parkingTextView = findViewById(R.id.txt_parking_hike);
        dateTextView = findViewById(R.id.txt_date_hike);
        descriptionTextView = findViewById(R.id.txt_des_hike);
        btnModify = findViewById(R.id.btn_modify_hike);
        btnDelete = findViewById(R.id.btn_delete_hike);
        btnConversation = findViewById(R.id.btn_observation_hike);

        // Retrieve the hike ID or other identifier from the Intent extras
        int hikeId = getIntent().getIntExtra("hike_id", -1);

        if (hikeId != -1) {
            // Use the DBHelper to fetch hike details based on the hikeId
            dbHelper = new DBHelper(this);
            HikeData hikeData = dbHelper.getHikeById(hikeId);

            Bitmap imageBitmap = hikeData.getImageBitmap();
            if(imageBitmap != null) {
                hikeImageView.setImageBitmap(imageBitmap);
            }
            nameTextView.setText(hikeData.getName());
            locationTextView.setText(hikeData.getLocation());
            levelTextView.setText(hikeData.getDifficulty());
            lengthTextView.setText(hikeData.getLength());
            parkingTextView.setText(hikeData.getParking());
            dateTextView.setText(hikeData.getDate());
            descriptionTextView.setText(hikeData.getDescription());
        }


        //Handle btnModify
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the edit activity
                Intent intent = new Intent(DetailHiking.this, EditHikeActivity.class);
                intent.putExtra("hike_id", hikeId);
                startActivityForResult(intent, EDIT_HIKE_REQUEST_CODE);
            }
        });

        //Handle btnDelete
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmation dialog to make sure user wants to delete
                new AlertDialog.Builder(DetailHiking.this)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this hike?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Delete from database
                                boolean deleteSuccessful = dbHelper.deleteHike(hikeId);

                                if (deleteSuccessful) {
                                    Toast.makeText(DetailHiking.this, "Delete successful!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(DetailHiking.this, "Delete failed!", Toast.LENGTH_SHORT).show();
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        //Handle btnConversation
        btnConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ConversationActivity
                Intent intent = new Intent(DetailHiking.this, Observation.class);

                // Pass extra data (here an ID as a string) to the new Activity
                intent.putExtra("MY_ID_KEY", hikeId); // Replace with actual ID value

                // Or if you have an int ID
                // intent.putExtra("MY_ID_KEY", your_int_id_here);

                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int hikeId = data.getIntExtra("hike_id", -1);
        if (requestCode == EDIT_HIKE_REQUEST_CODE && resultCode == RESULT_OK) {
            refreshHikeData(hikeId);
        } else{
            Toast.makeText(this, "Hike details updated!", Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshHikeData(int hikeId) {
        HikeData updatedHikeData = dbHelper.getHikeById(hikeId);

        if(updatedHikeData != null) {
            nameTextView.setText(updatedHikeData.getName());
            locationTextView.setText(updatedHikeData.getLocation());
            dateTextView.setText(updatedHikeData.getDate());
            parkingTextView.setText(updatedHikeData.getParking());
            lengthTextView.setText(updatedHikeData.getLength());
            levelTextView.setText(updatedHikeData.getDifficulty());
            descriptionTextView.setText(updatedHikeData.getDescription());
            hikeImageView.setImageBitmap(updatedHikeData.getImageBitmap());
        } else {
            Toast.makeText(this, "Error loading updated hiking data", Toast.LENGTH_SHORT).show();
        }
    }


// Handle back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
