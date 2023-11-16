package com.example.khike.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.khike.Login;
import com.example.khike.CreateHiking;
import com.example.khike.DBHelper;
import com.example.khike.HikeData;
import com.example.khike.HikeGridAdapter;
import com.example.khike.Navigation;
import com.example.khike.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment{
    private DBHelper dbHelper;
    private HikeGridAdapter adapter;

    private List<HikeData> hikeDataList;

    private ArrayAdapter<String> adapterDifficulty;
    private ArrayList<String> difficultyList;
    private ArrayAdapter<String> adapterLocation;
    private ArrayList<String> locationList;

    private SeekBar seekBarLength;
    private Button spinnerButton;
    private TextView txtPercent,logoutBtn;
    private ImageView logoutImg, navigationImg;

    private GridView gv;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dbHelper = new DBHelper(requireContext()); // Replace with your context


       hikeDataList = dbHelper.getAllHiking();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        gv = view.findViewById(R.id.gvHike);
        SearchView searchView = view.findViewById(R.id.searchView);
        adapter = new HikeGridAdapter(requireContext(), hikeDataList);
        gv.setAdapter(adapter);

//        logout
        logoutBtn = view.findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        logoutImg = view.findViewById(R.id.logout_img);
        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        navigationImg = view.findViewById(R.id.navigationImg);
        navigationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Navigation.class);
                startActivity(intent);
            }
        });

//         Initialize the ArrayList and ArrayAdapter for the difficulty levels spinner
        difficultyList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.difficulty_search)));
        Spinner spinnerDifficulty = view.findViewById(R.id.spinner_difficulty_search);
        adapterDifficulty = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, difficultyList);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);

        // Initialize the ArrayList and ArrayAdapter for the difficulty levels spinner
        locationList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.locations_search)));
        Spinner spinnerLocation = view.findViewById(R.id.spinner_location_search);
        adapterLocation = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationList);
        adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapterLocation);


        spinnerButton = view.findViewById(R.id.custom_spinner_button);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                closeSoftKeyboard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        ImageView add_record_btn = view.findViewById(R.id.add_record_btn);
        add_record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateHiking.class);
                startActivity(intent);
            }
        });
        spinnerButton.setTypeface(null, Typeface.NORMAL);
        spinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomSpinner();
            }
        });

        // handle search by difficulty
        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshHikeData();  // Reload data from the database first
                String selectedDifficulty = parent.getItemAtPosition(position).toString();
                filterByDifficulty(selectedDifficulty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adapter.getFilter().filter(null);
            }
        });


        // handle search by location
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshHikeData();
                String selectedLocation = parent.getItemAtPosition(position).toString();
                filterByLocation(selectedLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adapter.getFilter().filter(null);
            }
        });

        return view;

    }

    private void logout() {
        // Directly clear the SharedPreferences and navigate to login screen.
        SharedPreferences preferences = getContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showCustomSpinner() {
        // Initialize a PopupWindow
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_spinner_layout, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        txtPercent = popupView.findViewById(R.id.txt_percent_length);
        seekBarLength = popupView.findViewById(R.id.seek_bar);

        seekBarLength.setMax(50);
        seekBarLength.setProgress(0);
        txtPercent.setText(0 + "km");
        seekBarLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle seekbar change
                txtPercent.setText(String.valueOf(progress) + " km");
                if(progress == 0) {
                    refreshHikeData(); // Assuming reloadData is the method that reloads all the data
                } else {
                    refreshHikeData();
                    filterByLength(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Allow touches outside to dismiss the popup
        popupWindow.setOutsideTouchable(true);
        // Show PopupWindow below the button
        popupWindow.showAsDropDown(spinnerButton, 0, 0);
    }



    private void filterByDifficulty(String difficulty) {
        if (difficulty.equals("Difficulty")) {  // Replace with the first item of your location spinner
            refreshHikeData();  // Load all data
        } else {
            List<HikeData> filteredList = new ArrayList<>();
            for (HikeData data : hikeDataList) {
                if (data.getDifficulty().equals(difficulty)) {
                    filteredList.add(data);
                }
            }
            adapter.updateData(filteredList);
        }
    }

    private void filterByLocation(String location) {
        if (location.equals("Location")) {
            refreshHikeData();
        } else {
            List<HikeData> filteredList = new ArrayList<>();
            for (HikeData data : hikeDataList) {
                if (data.getLocation().equals(location)) {
                    filteredList.add(data);
                }
            }
            adapter.updateData(filteredList);
        }
    }

    private void filterByLength(int length) {
        List<HikeData> filteredList = new ArrayList<>();
        for (HikeData data : hikeDataList) {
            try {
                String rawLength = data.getLength().replace(" km", "").trim();
                int hikeLength = Integer.parseInt(rawLength);
                if (hikeLength <= length) {
                    filteredList.add(data);
                }
            } catch (NumberFormatException e) {
                Log.e("FilterByLength", "NumberFormatException: " + e.getMessage());
            }
        }
        adapter.updateData(filteredList);
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshHikeData();

    }

    private void refreshHikeData() {
        List<HikeData> updatedData = dbHelper.getAllHiking();
        if (updatedData != null) {
            for (HikeData hike : updatedData) {
                hike.setBookmarked(dbHelper.isHikeBookmarked(hike.getId()));
            }
        }
        if (adapter != null && updatedData != null) {
            adapter.updateData(updatedData);


        } else {
            Log.e("Debug", "adapter or updatedData is null");
        }

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void closeSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}