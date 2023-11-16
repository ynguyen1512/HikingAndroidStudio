package com.example.khike;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Observation extends AppCompatActivity {

    private GridView gridView;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);

        //Action bar

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Your back icon
        }

        // Retrieve the ID passed from CurrentActivity
        Intent intent = getIntent();
        id = getIntent().getIntExtra("MY_ID_KEY", -1);

        DBHelper dbHelper = new DBHelper(this);
        List<ObservationData> observations = dbHelper.getObservationsByHikeId(id);

        gridView = findViewById(R.id.gv_Observation);
        ObservationAdapter adapter = new ObservationAdapter(Observation.this, observations);
        gridView.setAdapter(adapter);

        ImageView btn_add_observation = findViewById(R.id.btn_create_observation);
        btn_add_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Observation.this, CreateObservation.class);
                // Get the current date and time
                Calendar calendar = Calendar.getInstance();
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.getTime());

                // Put them as extras in the intent
                intent.putExtra("currentDate", currentDate);
                intent.putExtra("currentTime", currentTime);
                intent.putExtra("MY_ID_KEY", id); // Passing the ID to CreateObservation

                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshObservations();
    }

    private void refreshObservations() {
        DBHelper dbHelper = new DBHelper(this);
        List<ObservationData> observations = dbHelper.getObservationsByHikeId(id);
        ObservationAdapter adapter = new ObservationAdapter(Observation.this, observations);
        gridView.setAdapter(adapter);
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
}