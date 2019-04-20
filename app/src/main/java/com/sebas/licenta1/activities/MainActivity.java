package com.sebas.licenta1.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.sebas.licenta1.R;
import com.sebas.licenta1.dto.AppUser;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private AppUser appUser = null;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.explore:
                            selectedFragment = new ExploreFragment();
                            break;
                        case R.id.saved:
                            selectedFragment = new SavedFragment();
                            break;
                        case R.id.profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureUI();
    }

    private void configureUI() {
        defineView();
    }

    private void defineView() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        Fragment defaultFragment = new ExploreFragment();
        Bundle arguments = new Bundle();
        arguments.putString("test", "o valoare");
        defaultFragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                defaultFragment).commit();
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}