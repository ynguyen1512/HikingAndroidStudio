package com.example.khike.ui.saved;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.khike.DBHelper;
import com.example.khike.HikeData;
import com.example.khike.HikeGridAdapter;
import com.example.khike.R;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment  {

    private GridView gridView;
    private List<HikeData> bookmarkedHikes;
    private HikeGridAdapter adapter;
    private DBHelper dbHelper;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        // Initialize the GridView
        gridView = view.findViewById(R.id.gvSaved); // Adjust the ID based on your layout

        // Initialize the DBHelper
        dbHelper = new DBHelper(getContext());
//        // Get the list of bookmarked hikes
//        bookmarkedHikes = dbHelper.getBookmarkedHikes();
//
//        // Set the adapter
//        adapter = new HikeGridAdapter(getContext(), bookmarkedHikes);
//        gridView.setAdapter(adapter);
        refreshHikeData();

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        refreshHikeData();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


//    private void refreshHikeData() {
//        // Fetch the updated list of bookmarked hikes
//        List<HikeData> updatedData = dbHelper.getBookmarkedHikes();
//
//        // Update the adapter with the new list
////        adapter.updateData(bookmarkedHikes);
//        if (updatedData != null) {
//            for (HikeData hike : updatedData) {
//                hike.setBookmarked(dbHelper.isHikeBookmarked(hike.getId()));
//            }
//        }
//        if (adapter != null && updatedData != null) {
//            adapter.updateData(updatedData);
//        } else {
//            Log.e("Debug", "adapter or updatedData is null");
//        }
//
//    }

    private void refreshHikeData() {
        // Fetch the updated list of bookmarked hikes
        bookmarkedHikes = dbHelper.getBookmarkedHikes();

        // Set the adapter
        adapter = new HikeGridAdapter(getContext(), bookmarkedHikes);
        gridView.setAdapter(adapter);


        if (bookmarkedHikes != null) {
            for (HikeData hike : bookmarkedHikes) {
                hike.setBookmarked(dbHelper.isHikeBookmarked(hike.getId()));
            }
        }

    }


}