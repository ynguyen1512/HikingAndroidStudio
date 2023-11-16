package com.example.khike.ui.explore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.khike.ExploreAdapter;
import com.example.khike.ExploreData;
import com.example.khike.CreateExplore;
import com.example.khike.DBHelper;
import com.example.khike.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ExploreFragment extends Fragment {

    private String fullName;
    private DBHelper dbHelper;
    private List<ExploreData> exploreDataList;

    private ExploreAdapter adapter;

    private GridView gv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        dbHelper = new DBHelper(getActivity()); // Replace with your context
        gv = view.findViewById(R.id.gv);
//        FloatingActionButton btn_float = view.findViewById(R.id.btn_float_explore);
        ImageView btn_add_explore = view.findViewById(R.id.btn_add_explore);
        loadExploreData();

        btn_add_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExploreData exploreData = new ExploreData();
                Intent intent = new Intent(getContext(), CreateExplore.class);
                // Get the current date and time
                Calendar calendar = Calendar.getInstance();
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

                // Put them as extras in the intent
                intent.putExtra("currentDate", currentDate);
                intent.putExtra("explore_id", exploreData.getId()); // Passing the ID to explore

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExploreData();
    }

    private void loadExploreData() {
        if (dbHelper != null && getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_CREDENTIALS", Context.MODE_PRIVATE);
            String loggedInUserName = sharedPreferences.getString("CURRENT_USER",  null);

            if(loggedInUserName != null) {
                fullName = dbHelper.getFullName(loggedInUserName);
                Log.d("Debug", "Retrieved fullName: " + fullName);
            }

            exploreDataList = dbHelper.getAllExploreData();
            if (exploreDataList != null && !exploreDataList.isEmpty()) {
                adapter = new ExploreAdapter(requireContext(), fullName, exploreDataList);
                gv.setAdapter(adapter);
            } else {
                Log.e("FRAGMENT_ERROR", "No data to display.");
            }
        } else {
            Log.e("DB_ERROR", "DBHelper or getActivity() is null.");
        }
    }
}
