package com.sebas.licenta1.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sebas.licenta1.R;

public class SplashActivity extends AppCompatActivity {

    private FirebaseFirestore firestoreDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firestoreDb = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and registered then update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            // user is signed in with google
            firestoreDb
                .collection("users").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // user is registered in database
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                switchActivity(MainActivity.class, true);
                                return;
                            }
                        }
                        switchActivity(IntroActivity.class, true);
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
