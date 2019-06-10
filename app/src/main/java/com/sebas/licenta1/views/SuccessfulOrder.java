package com.sebas.licenta1.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sebas.licenta1.R;

public class SuccessfulOrder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_order);
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, "Successful order", null);
    }

    public void closeActivity(View v) {
        finish();
    }
}
