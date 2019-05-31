package com.sebas.licenta1.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sebas.licenta1.R;
import com.sebas.licenta1.entities.PlaceDetails;
import com.sebas.licenta1.entities.PlaceSummary;
import com.sebas.licenta1.utils.LoadingDialog;
import com.sebas.licenta1.viewmodels.PlaceViewModel;

public class PlaceDetailsActivity extends AppCompatActivity {
    private PlaceDetails placeDetails;
    private PlaceSummary googleData;
    private LoadingDialog loadingDialog;
    private Button bookButton;
    private PlaceViewModel placeVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        placeVm = ViewModelProviders.of(this).get(PlaceViewModel.class);

        defineUI();
        getIntentData();
        getPlaceById();
        createListeners();
    }

    private void getPlaceById() {
        loadingDialog.show();
        placeVm.getPlace(googleData.getPlaceId()).observe(this, new Observer<PlaceDetails>() {
            @Override
            public void onChanged(@Nullable PlaceDetails place) {
                placeDetails = place;
                setDataInView();
                loadingDialog.dismiss();
            }
        });
    }

    private void setDataInView() {
        if(placeDetails != null) {
            ((TextView) findViewById(R.id.name)).setText(placeDetails.getName());
            ((TextView) findViewById(R.id.address)).setText(placeDetails.getAddress());
            ((TextView) findViewById(R.id.description)).setText(placeDetails.getDescription());
            ((TextView) findViewById(R.id.ambientType)).setText(placeDetails.getAmbientType());
            ((TextView) findViewById(R.id.foodType)).setText(placeDetails.getFoodType());
            ((TextView) findViewById(R.id.preBooking)).setText(placeDetails.getPreBooking());
        } else {
            ((TextView) findViewById(R.id.name)).setText(googleData.getName());
            ((TextView) findViewById(R.id.address)).setText(googleData.getVicinity());
        }

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
        bookButton = findViewById(R.id.book_button);
    }

    private void createListeners() {
        ImageView backButton =  findViewById(R.id.backIcon);
        backButton.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                // go to previous state
                finish();
            }
        });

        bookButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(placeDetails == null) {
                    Toast.makeText(PlaceDetailsActivity.this, "Location not available",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                intent.putExtra("placeDetails", placeDetails);
                startActivity(intent);
            }
        });
    }

    private void getIntentData() {
        googleData = (PlaceSummary) getIntent().getSerializableExtra("googleData");
    }
}
