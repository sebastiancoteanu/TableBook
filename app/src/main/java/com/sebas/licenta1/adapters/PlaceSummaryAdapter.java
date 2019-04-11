package com.sebas.licenta1.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sebas.licenta1.R;
import com.sebas.licenta1.dto.PlaceSummary;

import java.util.ArrayList;

public class PlaceSummaryAdapter extends RecyclerView.Adapter<PlaceSummaryAdapter.ViewHolder> {
    private ArrayList<PlaceSummary> places;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        PlaceSummary place = places.get(i);

        viewHolder.name.setText(place.getName());
        viewHolder.vicinity.setText(place.getVicinity());
    }


    @Override
    public int getItemCount() {
        if (places != null) {
            return places.size();
        } else {
            return 0;
        }
    }

    public PlaceSummaryAdapter(ArrayList<PlaceSummary> places) {
        this.places = places;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final TextView vicinity;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            vicinity = view.findViewById(R.id.vicinity);
        }
    }
}
