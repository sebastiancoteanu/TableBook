package com.sebas.licenta1.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sebas.licenta1.R;

public class SuccessfulOrder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_order);
    }

    public void closeActivity(View v) {
        finish();
    }
}
