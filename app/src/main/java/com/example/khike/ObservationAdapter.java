package com.example.khike;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ObservationAdapter extends BaseAdapter {

    private Context context;
    private List<ObservationData> observationDataList;
    private LayoutInflater inflater;

    public ObservationAdapter(Context context, List<ObservationData> observationDataList) {
        this.context = context;
        this.observationDataList = observationDataList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return observationDataList != null ? observationDataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return observationDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.custom_layout_observation, parent, false);
            holder.observationName = convertView.findViewById(R.id.txt_name_observation);
            holder.observationDate = convertView.findViewById(R.id.txt_date_observation);
            holder.observationTime = convertView.findViewById(R.id.txt_time_observation);
            holder.observationTitle = convertView.findViewById(R.id.txt_note_obv_data2);
            holder.observationDes = convertView.findViewById(R.id.txt_note_obv_data1);
            holder.observationImage = convertView.findViewById(R.id.img_observation);
            holder.cardView = convertView.findViewById(R.id.card_view_observation);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ObservationData observation = observationDataList.get(position);

        holder.observationName.setText(observation.getName());
        holder.observationDate.setText(observation.getDate());
        holder.observationTime.setText(observation.getTime());
        holder.observationTitle.setText(observation.getTitles());
        holder.observationDes.setText(observation.getDescriptions());
        holder.observationImage.setImageBitmap(observation.getImageBitmap());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObservationData ObsData = observationDataList.get(position);

                // Create an intent to start the HikeDetailActivity
                Calendar calendar = Calendar.getInstance();
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.getTime());

                // Create an intent to start the EditObservationActivity
                Intent intent = new Intent(context, EditObservation.class);

                // Pass data to the detail activity using extras (you can pass hikeData.getId() or other identifiers)
                intent.putExtra("observation_id", ObsData.getId());
                intent.putExtra("currentDate", currentDate);
                intent.putExtra("currentTime", currentTime);

                // Optional: Adding flags to the intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Starting the activity
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    // Log the exception for debugging purposes
                    Log.e("ObservationAdapter", "Error starting EditObservationActivity", e);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this observation?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DBHelper dbHelper = new DBHelper(v.getContext());
                                ObservationData ObsData = observationDataList.get(position);
                                boolean deleteSuccessful = dbHelper.deleteObservation(ObsData.getId());

                                if (deleteSuccessful) {
                                    // Remove the item from your data set
                                    observationDataList.remove(position);

                                    // Notify the adapter that your data set has changed -> this will refresh your ListView
                                    notifyDataSetChanged();
                                    Toast.makeText(v.getContext(), "Delete successful!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(v.getContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView observationName;
        TextView observationDate;
        TextView observationTime;
        TextView observationTitle;
        TextView observationDes;
        ImageView observationImage;

        CardView cardView;

        ImageButton btnDelete;
    }
}
