package com.sebas.licenta1.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sebas.licenta1.R;
import com.sebas.licenta1.entities.AppUser;
import com.sebas.licenta1.entities.PlaceDetails;
import com.sebas.licenta1.entities.PlaceSummary;
import com.sebas.licenta1.utils.LoadingDialog;
import com.sebas.licenta1.viewmodels.PlaceViewModel;
import com.sebas.licenta1.viewmodels.UserViewModel;

public class PlaceDetailsActivity extends AppCompatActivity {
    private PlaceDetails placeDetails;
    private PlaceSummary googleData;
    private LoadingDialog loadingDialog;
    private Button bookButton;
    private ToggleButton favoriteButton;
    private PlaceViewModel placeVm;
    private UserViewModel userVm;
    private ScaleAnimation scaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        placeVm = ViewModelProviders.of(this).get(PlaceViewModel.class);
        userVm = ViewModelProviders.of(this).get(UserViewModel.class);

        defineUI();
        getIntentData();
        getPlaceById();
        createListeners();
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, "View Place", null);
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


        userVm.getFavoritePlace(placeDetails).observe(this, placeDetails -> favoriteButton.setChecked(placeDetails != null));
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
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        favoriteButton = findViewById(R.id.button_favorite);
    }

    private void createListeners() {
        ImageView backButton =  findViewById(R.id.backIcon);
        backButton.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                // go to previous state
                finish();
            }
        });

        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.startAnimation(scaleAnimation);
                if(isChecked) {
                    userVm.addToFavorite(placeDetails);
                } else {
                    userVm.removeFromFavorite(placeDetails);
                }
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
