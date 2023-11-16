package com.example.khike;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HikeGridAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<HikeData> hikeDataList;
    private List<HikeData> originalHikeDataList;
    private HashSet<Integer> clickedHikes = new HashSet<>();
    private DBHelper dbHelper;

    private ImageButton imageButton;

    public HikeGridAdapter(){}


    public HikeGridAdapter(Context context, List<HikeData> hikeDataList) {
        this.context = context;
        this.hikeDataList = hikeDataList;
        this.originalHikeDataList = new ArrayList<>(hikeDataList);
    }



    @Override
    public int getCount() {
        return hikeDataList == null ? 0 : hikeDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return hikeDataList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.custome_layout_home, parent , false);
        }

        // Get references to the views in the grid item layout
        CardView cardView = itemView.findViewById(R.id.card_view_hike);
        ImageView imageHike = itemView.findViewById(R.id.img_hike);
        TextView levelHike = itemView.findViewById(R.id.txt_level_hike);
        TextView nameHike = itemView.findViewById(R.id.txt_name_hike);
        TextView locationHike = itemView.findViewById(R.id.txt_location_hike);
        TextView lengthHike = itemView.findViewById(R.id.txt_length_hike);
        imageButton = itemView.findViewById(R.id.imageButton);


        // Get the HikeData object for the current position
        HikeData hikeData = hikeDataList.get(position);

        // Set the data to the views
        imageHike.setImageBitmap(hikeData.getImageBitmap());
        levelHike.setText(hikeData.getDifficulty());
        nameHike.setText(hikeData.getName());
        locationHike.setText(hikeData.getLocation());
        lengthHike.setText(hikeData.getLength());

        if (hikeData.isBookmarked()) {
            imageButton.setImageResource(R.drawable.bookmark_fill);
        } else {
            imageButton.setImageResource(R.drawable.ic_save);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HikeData hikeData = hikeDataList.get(position);

                // Create an intent to start the HikeDetailActivity
                Intent intent = new Intent(context, DetailHiking.class);

                // Pass data to the detail activity using extras (you can pass hikeData.getId() or other identifiers)
                intent.putExtra("hike_id", hikeData.getId());

                // Start the activity
                context.startActivity(intent);

            }

        });



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(context);



                        if (dbHelper.isBookmarked(hikeData)) {
                            // If the hike is already bookmarked, remove it
                            dbHelper.removeHiking(hikeData);
                            hikeData.setBookmarked(false);
                            updateBookmark();
                            imageButton.setImageResource(R.drawable.ic_save);
                            Toast.makeText(context, "Hike removed from bookmarks!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If the hike is not bookmarked, add it
                            dbHelper.bookmarkHike(hikeData);
                            hikeData.setBookmarked(true);
                            imageButton.setImageResource(R.drawable.bookmark_fill);
                            Toast.makeText(context, "Hike bookmarked!", Toast.LENGTH_SHORT).show();

                        }
                notifyDataSetChanged();
            }
        });




        return itemView;
    }




    public void updateData(List<HikeData> newData) {
        this.hikeDataList.clear();
        this.hikeDataList.addAll(newData);
        notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    public void updateBookmark(){
        hikeDataList = dbHelper.getBookmarkedHikes();

        if (hikeDataList != null) {
            for (HikeData hike : hikeDataList) {
                hike.setBookmarked(dbHelper.isHikeBookmarked(hike.getId()));
            }
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.count = originalHikeDataList.size();
                    results.values = originalHikeDataList;
                } else {
                    String searchStr = constraint.toString().toLowerCase();
                    List<HikeData> filteredList = new ArrayList<>();
                    for (HikeData data : originalHikeDataList) {
                        if (data.getName().toLowerCase().contains(searchStr)) {
                            filteredList.add(data);
                        }
                    }
                    results.count = filteredList.size();
                    results.values = filteredList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                hikeDataList = (List<HikeData>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
