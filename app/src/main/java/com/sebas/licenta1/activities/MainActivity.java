package com.sebas.licenta1.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sebas.licenta1.R;
import com.sebas.licenta1.dto.AppUser;
import com.sebas.licenta1.dto.UserDataHolder;
import com.sebas.licenta1.utils.LoadingDialog;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private LoadingDialog loadingDialog;
    private DocumentReference usersRef;
    private AppUser appUser;

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
        configureDb();
        fetchUser();
    }

    private void configureDb() {
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = firestoreDb.collection("users").document(firebaseUser.getUid());
    }

    private void fetchUser() {
        appUser = UserDataHolder.getInstance().getAppUser();
        if(appUser != null) {
            Log.d("Obiectul user:", appUser.toString());
            return;
        }

        if(loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }

        loadingDialog.show();

        usersRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loadingDialog.dismiss();
                        appUser = documentSnapshot.toObject(AppUser.class);
                        if(appUser != null) {
                            UserDataHolder.getInstance().setAppUser(appUser);
                            Fragment defaultFragment = new ExploreFragment();

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    defaultFragment).commit();
                        } else {
                            loadingDialog.dismiss();
                            Log.d("Error", "User object is empty.");
                        }
                    }
                });
    }

    private void configureUI() {
        defineView();
    }

    private void defineView() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }
}