package com.sebas.licenta1.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.sebas.licenta1.BuildConfig;
import com.sebas.licenta1.R;
import com.sebas.licenta1.adapters.PlaceSummaryAdapter;
import com.sebas.licenta1.dto.PlaceSummary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {
    private static String placesBaseUrl = "https://fierce-mesa-40211.herokuapp.com/places?location=";
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ArrayList<PlaceSummary> places = new ArrayList<PlaceSummary>();
    private RecyclerView placesRecycler;
    private RecyclerView.Adapter placesAdapter;
    private AlertDialog.Builder loadingBuilder;
    private Dialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        configurePlaces();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        defineUI(view);
        fetchPlaces(view);
    }

    private void configureRecycler() {
        placesRecycler = (getView()).findViewById(R.id.places);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        placesRecycler.setLayoutManager(mLayoutManager);
        placesAdapter = new PlaceSummaryAdapter(places);
        placesRecycler.setAdapter(placesAdapter);
    }

    private void showLoadingDialog() {
        loadingDialog.show();
        loadingDialog.getWindow().setLayout(400,400);
    }

    private void defineUI(View v) {
        placesRecycler = v.findViewById(R.id.places);
        loadingBuilder = new AlertDialog.Builder(getActivity(),  R.style.AlertDialogTheme);
        loadingBuilder.setView(R.layout.dialog_loading);
        loadingDialog = loadingBuilder.create();
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    private void fetchPlaces(View v) {
        String requestUrl = placesBaseUrl + "44.429725,26.102877";
        requestQueue = Volley.newRequestQueue(v.getContext());
        showLoadingDialog();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for(int i = 0; i < results.length(); i++) {
                                JSONObject place = results.getJSONObject(i);

                                String name = place.getString("name");
                                String placeId = place.getString("place_id");
                                String vicinity = place.getString("vicinity");

                                PlaceSummary placeSummary = new PlaceSummary(placeId, name, vicinity);
                                places.add(placeSummary);
                            }
                            configureRecycler();
                            loadingDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loadingDialog.dismiss();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void configurePlaces() {
        Places.initialize(getActivity().getApplicationContext(), BuildConfig.GooglePlacesApiKey);
        PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());
    }
}
