package com.sebas.licenta1.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sebas.licenta1.R;
import com.sebas.licenta1.dto.PlaceDetails;
import com.sebas.licenta1.dto.PlaceSummary;
import com.sebas.licenta1.utils.LoadingDialog;

import javax.annotation.Nullable;

public class PlaceDetailsActivity extends AppCompatActivity {
    private PlacesClient placesClient;
    private CollectionReference placeRef;
    private FirebaseFirestore firestoreDb;
    private PlaceDetails placeDetails;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        defineUI();
        configureDb();
        createListeners();
    }

    private void configureDb() {
        firestoreDb = FirebaseFirestore.getInstance();
        placeRef = firestoreDb.collection("places");
        loadingDialog.show();

        placeRef
            .whereEqualTo("placeID", "ChIJt0sqxD__sUAR7gcIoiuNcvU")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    placeDetails = queryDocumentSnapshots.getDocuments().get(0).toObject(PlaceDetails.class);
                    setDataInView(placeDetails);
                    loadingDialog.dismiss();
                }
            });
    }

    private void setDataInView(PlaceDetails placeDetails) {
        PlaceSummary googleData = (PlaceSummary) getIntent().getSerializableExtra("googleData");

        String name = placeDetails.getName();
        String address = placeDetails.getAddress();

        if(name == null) {
            name = googleData.getName();
        }
        if(address == null) {
            address = googleData.getVicinity();
        }


        ((TextView) findViewById(R.id.name)).setText(name);
        ((TextView) findViewById(R.id.address)).setText(address);

        ((TextView) findViewById(R.id.description)).setText(placeDetails.getDescription());
        ((TextView) findViewById(R.id.ambientType)).setText(placeDetails.getAmbientType());
        ((TextView) findViewById(R.id.foodType)).setText(placeDetails.getFoodType());
        ((TextView) findViewById(R.id.preBooking)).setText(placeDetails.getPreBooking());

        String expensiveness = getExpensiveness(googleData.getPriceLevel());
        ((TextView) findViewById(R.id.expensiveness)).setText(expensiveness);
        ((RatingBar) findViewById(R.id.ratingBar)).setRating(googleData.getRating());
        ((TextView) findViewById(R.id.ratingsNumber)).setText(googleData.getRatingsNumber().toString());
    }

    private String getExpensiveness(Integer priceLevel) {
        String expensiveness;

        switch (priceLevel) {
            case 0:
                expensiveness = "Free";
                break;
            case 1:
                expensiveness = "Inexpensive";
                break;
            case 2:
                expensiveness = "Moderate";
                break;
            case 3:
                expensiveness = "Expensive";
                break;
            case 4:
                expensiveness = "Very expensive";
                break;
            default:
                expensiveness = "Unknown prices";
                break;
        }

        return expensiveness;
    }

    private void defineUI() {
        loadingDialog = new LoadingDialog(this);
    }

    private void createListeners() {
        ImageView backButton =  findViewById(R.id.backIcon);
        backButton.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                // go to previous state
                finish();
            }
        });
    }
}
