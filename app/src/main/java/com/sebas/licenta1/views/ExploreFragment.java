package com.sebas.licenta1.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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
import com.sebas.licenta1.entities.PlaceSummary;
import com.sebas.licenta1.utils.EndlessRecyclerOnScrollListener;
import com.sebas.licenta1.utils.LoadingDialog;

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
    private LoadingDialog loadingDialog;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int SETTINGS_INTENT = 9001;
    private String provider;
    Location location;

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
        configureRecycler();
        createListeners();

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        } else {
            requestLocationUpdates();
        }
    }

    private void configureRecycler() {
        placesRecycler = (getView()).findViewById(R.id.places);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        placesRecycler.setLayoutManager(mLayoutManager);
        placesAdapter = new PlaceSummaryAdapter(places, new PlaceSummaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PlaceSummary placeSummary) {
                Intent intent = new Intent(getContext(), PlaceDetailsActivity.class);
                intent.putExtra("googleData", placeSummary);
                getContext().startActivity(intent);
            }
        });
        placesRecycler.setAdapter(placesAdapter);
        placesRecycler.setNestedScrollingEnabled(false);

        placesRecycler.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                placesRecycler.stopScroll();
                fetchPlaces(getContext());
            }
        });
    }

    private void defineUI(View v) {
        placesRecycler = v.findViewById(R.id.places);
        loadingDialog = new LoadingDialog(getActivity());
    }

    private void fetchPlaces(Context context) {
        String requestUrl = placesBaseUrl + "44.429725,26.102877";
        requestQueue = Volley.newRequestQueue(context);
        loadingDialog.show();

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
                                Float rating = Float.valueOf(place.getString("rating"));
                                Integer ratingsNumber = place.getInt("user_ratings_total");

                                Integer priceLevel = -1;
                                if(place.has("price_level")) {
                                    priceLevel = place.getInt("price_level");
                                }

                                PlaceSummary placeSummary = new PlaceSummary(placeId, name, vicinity, rating, ratingsNumber, priceLevel);
                                places.add(placeSummary);
                            }
                            placesAdapter.notifyDataSetChanged();
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

    private void createListeners() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // get location once
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                location = locationManager.getLastKnownLocation(provider);
                fetchPlaces(getContext());
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasLocationPermission()) {
                        requestLocationUpdates();
                    }

                } else {
                    showNoPermissionDialog();
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_INTENT) {
            locationManager.removeUpdates(locationListener);
            if(locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
                fetchPlaces(getContext());
            }
        }
    }

    public void requestLocationUpdates() {
        provider = locationManager.getBestProvider(new Criteria(), false);
        if(!locationManager.isProviderEnabled(provider)) {
            showNoLocationDialog();
        } else {
            location = locationManager.getLastKnownLocation(provider);
            fetchPlaces(getContext());
        }
    }

    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    public void showNoPermissionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setMessage("Please grant the location permission")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLocationPermission();
                    }
                }).create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getColor(R.color.colorAccent));
    }

    public void showNoLocationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setMessage("Please enable location")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(settingsIntent, SETTINGS_INTENT);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative statement
                    }
                }).create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getColor(R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getColor(R.color.colorAccent));
    }
}
