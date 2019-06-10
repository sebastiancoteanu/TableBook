package com.sebas.licenta1.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sebas.licenta1.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, "Intro", null);
    }

    public void switchSignIn(View v) {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }

    public void switchSignUp(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
