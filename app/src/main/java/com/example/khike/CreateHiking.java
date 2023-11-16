package com.example.khike;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class CreateHiking extends AppCompatActivity {

    private EditText  edtDateHike, edtLengthHike, edtDescription;
    private RadioGroup radioGroupParkingAvailable;
    private static final int SELECT_PICTURE = 1;
    private ImageView hikeImage;
    private Spinner spinnerDifficulty, spinnerNameHike, spinnerLocation;

    private ArrayAdapter<String> adapterNameHike;
    private ArrayList<String> hikesList;

    private ArrayAdapter<String> adapterLocation;
    private ArrayList<String> locationsList;
    private boolean isAddingNewItem = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hike);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        //Covert string-array resource to ArrayList
        hikesList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.name_hike)));

        spinnerNameHike = findViewById(R.id.spinner_name);
        adapterNameHike = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hikesList);
        adapterNameHike.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNameHike.setAdapter(adapterNameHike);

        //Location
        //Covert string-array resource to ArrayList
        locationsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.locations)));

        spinnerLocation = findViewById(R.id.spinner_location);
        adapterLocation = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationsList);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapterLocation);

        edtDateHike = findViewById(R.id.edt_date_hike);
        edtLengthHike = findViewById(R.id.edt_length_hike);
        //Difficulty
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        ArrayAdapter<CharSequence> adapterDif = ArrayAdapter.createFromResource(CreateHiking.this, R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapterDif.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDif);

        edtDescription = findViewById(R.id.edt_description);
        radioGroupParkingAvailable = findViewById(R.id.radioGroup_parking_available);
        hikeImage = findViewById(R.id.img_hike);
        Button selectImageButton = findViewById(R.id.btn_select_image);

        View.OnClickListener imagePickerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        };


        // Assign the same listener to both the ImageView and Button
        hikeImage.setOnClickListener(imagePickerListener);
        selectImageButton.setOnClickListener(imagePickerListener);


        // Listener for spinnerNameHike
        spinnerNameHike.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                if(selectedItem.equals("Add new name of hike")) {
                    isAddingNewItem = true;
                    showAddNewItemDialog(spinnerNameHike, adapterNameHike, hikesList);
                } else {
                    if (!isAddingNewItem) {
                        spinnerLocation.setSelection(position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(CreateHiking.this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for spinnerLocation
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedItem = parentView.getItemAtPosition(position).toString();
                if(selectedItem.equals("Add new location")) {
                    isAddingNewItem = true;  // Set flag to true before showing dialog
                    showAddNewItemDialog(spinnerLocation, adapterLocation, locationsList);
                } else {
                    // Synchronize the location spinner with the selected name
                    if (!isAddingNewItem) {
                        spinnerNameHike.setSelection(position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(CreateHiking.this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });


        //handle save button

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateData()) {

                    //Name
                    String name = spinnerNameHike.getSelectedItem().toString();

                    //Location
                    String location = spinnerLocation.getSelectedItem().toString();

                    String date = edtDateHike.getText().toString();
                    String length = edtLengthHike.getText().toString();
                    String lengthWithUnit=length + " km";

                    //Difficulty
                    String difficulty = spinnerDifficulty.getSelectedItem().toString();

                    String description = edtDescription.getText().toString();

                    // Get selected radio button value
                    int selectedParkingId = radioGroupParkingAvailable.getCheckedRadioButtonId();
                    RadioButton radioButtonParking = findViewById(selectedParkingId);
                    String parking = radioButtonParking.getText().toString();

                    // Use the DatabaseHelper to save the data
                    DBHelper db = new DBHelper(CreateHiking.this);
                    Bitmap imageBitmap = ((BitmapDrawable) hikeImage.getDrawable()).getBitmap();
                    db.addHiking(name, location, date, parking, lengthWithUnit, difficulty, description, imageBitmap);

                    Toast.makeText(CreateHiking.this, "Hike added successfully!", Toast.LENGTH_SHORT).show();

                    finish();
                }else {
                    Toast.makeText(CreateHiking.this, "Please correct the errors", Toast.LENGTH_SHORT).show();
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
                hikeImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
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

    private boolean validateData() {
        Integer name = spinnerNameHike.getSelectedItemPosition();
        Integer location = spinnerLocation.getSelectedItemPosition();
        String dateString = edtDateHike.getText().toString().trim();
        String length = edtLengthHike.getText().toString().trim();
        Integer difficulty = spinnerDifficulty.getSelectedItemPosition();
        String description = edtDescription.getText().toString().trim();
        int selectedParkingId = radioGroupParkingAvailable.getCheckedRadioButtonId();

        // Validate name
        if (name == 0) {
            Toast.makeText(CreateHiking.this, "Name is required", Toast.LENGTH_SHORT).show();
            spinnerNameHike.requestFocus();
            return false;
        }

        // Validate location
        if (location == 0) {
            Toast.makeText(CreateHiking.this, "Location is required", Toast.LENGTH_SHORT).show();
            spinnerLocation.requestFocus();
            return false;
        }

        // Validate date
        if (dateString.isEmpty()) {
            edtDateHike.setError("Date is required");
            edtDateHike.requestFocus();
            return false;
        }


        // Validate length
        if (length.isEmpty()&& TextUtils.isDigitsOnly(length)) {
            edtLengthHike.setError("Length incorrect format");
            edtLengthHike.requestFocus();
            return false;
        }


        // Check if length is a valid number and greater than 50
        double lengthValue;
        try {
            lengthValue = Double.parseDouble(length);
        } catch (NumberFormatException e) {
            edtLengthHike.setError("Length incorrect format");
            edtLengthHike.requestFocus();
            return false;
        }

        if (lengthValue >= 50) {
            edtLengthHike.setError("Length should be less than 50");
            edtLengthHike.requestFocus();
            return false;
        }


        // Validate difficulty
        if (difficulty == 0) {
            Toast.makeText(CreateHiking.this, "Difficulty is required", Toast.LENGTH_SHORT).show();
            spinnerDifficulty.requestFocus();
            return false;
        }

        // Validate description
        if (description.isEmpty()) {
            edtDescription.setError("Description is required");
            edtDescription.requestFocus();
            return false;
        }

        // Validate parking radio button selection
        if (selectedParkingId == -1) {
            Toast.makeText(CreateHiking.this, "Please select parking availability", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate image
        if (hikeImage.getDrawable() == null) {
            Toast.makeText(CreateHiking.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    // show dialog when click spinner
    private void showAddNewItemDialog(final Spinner targetSpinner, final ArrayAdapter<String> targetAdapter, final ArrayList<String> targetList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newItem = input.getText().toString();
                addNewItemToSpinner(newItem, targetSpinner, targetAdapter, targetList);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                targetSpinner.setSelection(0);  // Reset spinner selection to "Select a hike"
                dialog.cancel();
            }
        });

        builder.show();
    }

    // add a new item in adapter
    private void addNewItemToSpinner(String newItem, Spinner targetSpinner, final ArrayAdapter<String> targetAdapter, final ArrayList<String> targetList) {
        targetList.add(targetList.size() - 1, newItem); // Add before "Add new name of hike"
        targetAdapter.notifyDataSetChanged();
        targetSpinner.setSelection(targetAdapter.getPosition(newItem));
    }

    // Handle date field
    public void showDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view1, year1, monthOfYear, dayOfMonth) -> {
                    String dateString = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                    ((EditText) view).setText(dateString);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

}