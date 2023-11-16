package com.example.khike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExploreAdapter extends BaseAdapter {

    private Context context;
    private List<ExploreData> exploreDataList;

    private String fullName;
    private LayoutInflater inflater;

    private DBHelper dbHelper;


    public ExploreAdapter(Context context, String fullName, List<ExploreData> exploreDataList) {
        this.context = context;
        this.fullName = fullName;
        this.exploreDataList = exploreDataList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dbHelper = new DBHelper(context);
    }



    @Override
    public int getCount() {
        return exploreDataList != null ? exploreDataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return exploreDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.custom_explore_layout, parent, false);
            holder.avatarImageView = convertView.findViewById(R.id.avatar_img);
            holder.userNameTextView = convertView.findViewById(R.id.name_user);
            holder.dateTextView = convertView.findViewById(R.id.date_post);
            holder.mainImageView = convertView.findViewById(R.id.img_main);
            holder.hikeNameTextView = convertView.findViewById(R.id.name_hike);
            holder.hikeLocationTextView = convertView.findViewById(R.id.hike_location);
            holder.btn_more = convertView.findViewById(R.id.btn_more);
            holder.btn_like = convertView.findViewById(R.id.btn_like_explore);
            holder.btn_comment = convertView.findViewById(R.id.btn_cmt_explore);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ExploreData explore = exploreDataList.get(position);
        holder.avatarImageView.setImageResource(R.drawable.avatar);
        holder.userNameTextView.setText(fullName);
        holder.dateTextView.setText(explore.getDatePost());
        holder.hikeNameTextView.setText(explore.getNameHike());
        holder.hikeLocationTextView.setText(explore.getLocationHike());
        holder.mainImageView.setImageBitmap(explore.getImageBitmap());
        holder.btn_like.setTag(R.drawable.heart);  // Initial state
        ExploreData exploreData = exploreDataList.get(position);



        holder.btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "TextView clicked");
                TextView likeTextView = (TextView) v;
                Integer resourceTag = (Integer) likeTextView.getTag();
                Log.d("Debug", "Resource Tag: " + resourceTag);

                if (resourceTag != null && resourceTag.equals(R.drawable.heart)) {
                    Log.d("Debug", "Setting to full heart");
                    likeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_selected, 0, 0, 0);
                    likeTextView.setTag(R.drawable.heart_selected);
                } else {
                    Log.d("Debug", "Setting to empty heart");
                    likeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                    likeTextView.setTag(R.drawable.heart);
                }


            }
        });

        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.explore_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.action_edit) {
                            ExploreData explore = exploreDataList.get(position);

                            Calendar calendar = Calendar.getInstance();
                            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
                            Intent intent = new Intent(context, EditExplore.class);

                            intent.putExtra("explore_id", explore.getId());
                            intent.putExtra("currentDate", currentDate);
                            // Starting the activity
                            context.startActivity(intent);
                            return true;
                        } else if (itemId == R.id.action_delete) {

                            confirmDeletion(explore);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return convertView;
    }


    private static class ViewHolder {
        public ImageView  mainImageView;
        public ShapeableImageView avatarImageView;
        public TextView userNameTextView, dateTextView, hikeNameTextView, hikeLocationTextView,  btn_like, btn_comment, btn_more;

    }

    public void confirmDeletion(final ExploreData explore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to delete this explore?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int rowsDeleted = dbHelper.deleteExplore(explore.getId());
                if (rowsDeleted > 0) {
                    Log.d("DB_DEBUG", "Successfully deleted explore with ID: " + explore.getId());
                    // Remove the explore from the list and notify the adapter
                    exploreDataList.remove(explore);
                    notifyDataSetChanged();
                } else {
                    Log.d("DB_DEBUG", "Failed to delete explore.");
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

}
