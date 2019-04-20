package com.sebas.licenta1.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sebas.licenta1.R;
import com.sebas.licenta1.activities.PlaceDetailsActivity;
import com.sebas.licenta1.dto.PlaceSummary;

import java.util.ArrayList;

public class PlaceSummaryAdapter extends RecyclerView.Adapter<PlaceSummaryAdapter.ViewHolder> {
    private ArrayList<PlaceSummary> places;
    private Context parentContext;

    public interface OnItemClickListener {
        void onItemClick(PlaceSummary placeSummary);
    }

    private final OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        parentContext = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final PlaceSummary place = places.get(i);

        viewHolder.bind(places.get(i), listener);

        viewHolder.name.setText(place.getName());
        viewHolder.vicinity.setText(place.getVicinity());
        viewHolder.rating.setRating(place.getRating());
        viewHolder.ratingsNumber.setText(String.valueOf(place.getRatingsNumber()));
        switch (place.getPriceLevel()) {
            case 1:
                viewHolder.priceLevel.setText(parentContext.getString(R.string.inexpensive));
                break;
            case 2:
                viewHolder.priceLevel.setText(parentContext.getString(R.string.moderate));
                break;
            case 3:
                viewHolder.priceLevel.setText(parentContext.getString(R.string.expensive));
                break;
            case 4:
                viewHolder.priceLevel.setText(parentContext.getString(R.string.veryExpensive));
                break;
            default:
                viewHolder.priceLevel.setText("");
        }
    }


    @Override
    public int getItemCount() {
        if (places != null) {
            return places.size();
        } else {
            return 0;
        }
    }

    public PlaceSummaryAdapter(ArrayList<PlaceSummary> places, OnItemClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final TextView vicinity;
        public final RatingBar rating;
        public final TextView ratingsNumber;
        public final TextView priceLevel;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            vicinity = view.findViewById(R.id.vicinity);
            rating = view.findViewById(R.id.ratingBar);
            ratingsNumber = view.findViewById(R.id.ratingsNumber);
            priceLevel = view.findViewById(R.id.priceLevel);
        }

        public void bind(final PlaceSummary placeSummary, final OnItemClickListener listener) {
            itemView.findViewById(R.id.place_card).setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(placeSummary);
                }
            });
        }
    }
}
