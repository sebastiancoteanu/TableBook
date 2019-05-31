package com.sebas.licenta1.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sebas.licenta1.R;
import com.sebas.licenta1.entities.AppUser;
import com.sebas.licenta1.viewmodels.UserViewModel;

public class SplashActivity extends AppCompatActivity {
    private UserViewModel userVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userVm = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and registered then update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            userVm.getUser().observe(this, new Observer<AppUser>() {
                @Override
                public void onChanged(@Nullable AppUser user) {
                    if (user != null) {
                        switchActivity(MainActivity.class, true);
                    } else {
                        switchActivity(IntroActivity.class, true);
                    }
                }
            });
        } else {
            switchActivity(IntroActivity.class, true);
        }
    }

    private void switchActivity(Class c, Boolean closeCurrent) {
        Intent intent = new Intent(this, c);
        if(closeCurrent) {
            finish();
        }
        startActivity(intent);
    }
}
