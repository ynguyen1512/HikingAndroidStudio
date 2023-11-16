package com.example.khike;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class EditHikeActivity extends AppCompatActivity {
    private EditText edtDateHike, edtLengthHike, edtDescription;
    private RadioGroup radioGroupParkingAvailable;
    private static final int SELECT_PICTURE = 1;
    private ImageView hikeImage;
    private Button btnSave;

    private Spinner spinnerDifficulty, spinnerNameHike, spinnerLocation;
    private ArrayAdapter<String> adapterNameHike;
    private ArrayList<String> hikesList;

    private ArrayAdapter<String> adapterLocation;
    private ArrayList<String> locationsList;
    //// Flag to track if we're adding a new item
    private boolean isAddingNewItem = false;

    int hikeId;
    private HikeData hikeData;
    private DBHelper dbHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hike);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        hikeId = getIntent().getIntExtra("hike_id", -1);
        if (hikeId == -1) {
            Toast.makeText(this, "Error: Hike ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }

        // Assume you have these fields to edit

        //Name of hike
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
        ArrayAdapter<CharSequence> adapterDif = ArrayAdapter.createFromResource(EditHikeActivity.this, R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapterDif.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDif);

        edtDescription = findViewById(R.id.edt_description);
        radioGroupParkingAvailable = findViewById(R.id.radioGroup_parking_available);
        hikeImage = findViewById(R.id.img_hike);
        Button selectImageButton = findViewById(R.id.btn_select_image);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        // Listener for spinnerNameHike
        spinnerNameHike.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                if(selectedItem.equals("Add new name of hike")) {
                    isAddingNewItem = true;  // Set flag to true before showing dialog
                    showAddNewItemDialog(spinnerNameHike, adapterNameHike, hikesList);
                } else {
                    // Synchronize the location spinner with the selected name
                    if (!isAddingNewItem) {
                        spinnerLocation.setSelection(position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(EditHikeActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditHikeActivity.this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });


        // Load existing data
        dbHelper = new DBHelper(this);
        hikeData = dbHelper.getHikeById(hikeId);

        // Set initial values

        Glide.with(this)
                .load(hikeData.getImageBitmap()) // Replace with the method that retrieves the image URI/path
                .into(hikeImage);



        // Retrieve the name from hikeData object.
        String desiredHikeName = hikeData.getName();

        int namePosition = adapterNameHike.getPosition(desiredHikeName);

        if(namePosition >= 0){
            spinnerNameHike.setSelection(namePosition);
        } else {
            if(!(desiredHikeName.isEmpty())) {
                // Adding the name to the list and updating the Spinner.
                hikesList.add(hikesList.size() - 1, desiredHikeName);  // Add before "Add new name of hike"
                adapterNameHike.notifyDataSetChanged();
                // Redundant, but ensures synchronization.
                namePosition = adapterNameHike.getPosition(desiredHikeName);
                if(namePosition != -1) {
                    spinnerNameHike.setSelection(namePosition);
                } else {
                    Log.e("ERROR", "Name still not found after adding: " + desiredHikeName);
                }
            } else {
                Toast.makeText(this, "Name not found and cannot add an empty name!", Toast.LENGTH_SHORT).show();
            }
        }

        // Set the Spinner's selected item
//        int locationPosition = adapterLocation.getPosition(hikeData.getLocation());
//        spinnerLocation.setSelection(locationPosition);

        String desiredLocation = hikeData.getLocation();

        int LocationPosition = adapterLocation.getPosition(desiredLocation);

        if(LocationPosition >= 0){
            spinnerLocation.setSelection(LocationPosition);
        } else {
            if(!(desiredLocation.isEmpty())) {
                // Adding the name to the list and updating the Spinner.
                locationsList.add(locationsList.size() - 1, desiredLocation);  // Add before "Add new name of hike"
                adapterLocation.notifyDataSetChanged();
                // Redundant, but ensures synchronization.
                LocationPosition = adapterLocation.getPosition(desiredLocation);
                if(LocationPosition != -1) {
                    spinnerLocation.setSelection(LocationPosition);
                } else {
                    Log.e("ERROR", "Location still not found after adding: " + desiredHikeName);
                }
            } else {
                Toast.makeText(this, "location not found and cannot add an empty location!", Toast.LENGTH_SHORT).show();
            }
        }

        // Set the Spinner's selected item
        int difPosition = adapterDif.getPosition(hikeData.getDifficulty());
        spinnerDifficulty.setSelection(difPosition);

        //length
        String lengthWithUnit = hikeData.getLength(); // Suppose this returns "5 km"
        String length = lengthWithUnit.replace(" km", ""); // This should return "5" if " km" is present, else original string

        edtLengthHike.setText(length);
        edtDateHike.setText(hikeData.getDate());
        edtDescription.setText(hikeData.getDescription());


        String parkingStatus = hikeData.getParking();
        ((RadioButton) radioGroupParkingAvailable.findViewById("Yes".equals(parkingStatus) ? R.id.radio_yes : R.id.radio_no)).setChecked(true);


        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get edited values
                String editedName = spinnerNameHike.getSelectedItem().toString();
                String editedLocation = spinnerLocation.getSelectedItem().toString();
                String editedDifficulty = spinnerDifficulty.getSelectedItem().toString();
                String lengthWithUnit = edtLengthHike.getText().toString();
                String editedLengthHike = lengthWithUnit+ " km";
                String editedDateHike = edtDateHike.getText().toString();
                String editedDescription = edtDescription.getText().toString();
                int selectedId = radioGroupParkingAvailable.getCheckedRadioButtonId();
                String parkingStatus = ((RadioButton) findViewById(selectedId)).getText().toString();

                //validate


                // Extracting the numerical value
                double lengthValue;
                try {
                    lengthValue = Double.parseDouble(lengthWithUnit);
                } catch (NumberFormatException e) {
                    Toast.makeText(EditHikeActivity.this, "Invalid length input", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate if the length is greater than 50
                if (lengthValue >= 50) {
                    Toast.makeText(EditHikeActivity.this, "Length should be less than 50", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean isValid =
                        validateField(edtLengthHike, "Length of hike is required") &
                                validateField(edtDateHike, "Date of hike is required") &
                                validateField(edtDescription, "Description is required") &
                                validateSpinner(spinnerNameHike, "Please select a hike name") &
                                validateSpinner(spinnerLocation, "Please select a location") &
                                validateSpinner(spinnerDifficulty, "Please select a difficulty level");

                if (isValid) {
                    // Update database

                    hikeData.setName(editedName);
                    hikeData.setLocation(editedLocation);
                    hikeData.setDifficulty(editedDifficulty);
                    hikeData.setLength(editedLengthHike);
                    hikeData.setDate(editedDateHike);
                    hikeData.setDescription(editedDescription);
                    hikeData.setParking(parkingStatus);

                    // Update database

                    boolean updateSuccessful = dbHelper.updateHike(hikeId, hikeData);

                    // Check if update was successful
                    if (updateSuccessful) {
                        // Pass editedHikeId back to DetailHikeActivity
                        back();
                    } else {
                        // Notify user that update failed, maybe using a Toast
                        Toast.makeText(EditHikeActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(EditHikeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
                if(hikeData != null) {
                    hikeData.setImage(bitmap);
                }
                hikeImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void back() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("hike_id", hikeId);
        setResult(EditHikeActivity.RESULT_OK, resultIntent);
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

    // Ensure non-empty field, else set an error message.
    private boolean validateField(EditText editText, String errorMsg) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(errorMsg);
            editText.requestFocus();
            return false;
        }
        return true;
    }



    // Validate Spinner selection, else display a Toast message.
    private boolean validateSpinner(Spinner spinner, String errorMsg) {
        if (spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(EditHikeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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