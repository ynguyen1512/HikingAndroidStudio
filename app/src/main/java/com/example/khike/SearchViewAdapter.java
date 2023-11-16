package com.example.khike;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class SearchViewAdapter extends BaseAdapter implements Filterable {


    private Context context;
    private List<HikeData> originalData;
    private List<HikeData> filteredData;
    private ValueFilter valueFilter;

    public SearchViewAdapter(Context context, List<HikeData> originalData) {
        this.context = context;
        this.originalData = originalData;
        this.filteredData = originalData;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Implement your logic to populate the grid view items here
        return null;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<HikeData> filterList = new ArrayList<>();
                for (int i = 0; i < originalData.size(); i++) {
                    if ((originalData.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(originalData.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = originalData.size();
                results.values = originalData;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (List<HikeData>) results.values;
            notifyDataSetChanged();
        }
    }

}
