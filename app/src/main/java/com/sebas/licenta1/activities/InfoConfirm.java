package com.sebas.licenta1.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sebas.licenta1.R;

import java.util.HashMap;
import java.util.Map;

public class InfoConfirm extends AppCompatActivity {
    private EditText emailText;
    private EditText firstNameText;
    private EditText lastNameText;

    private FirebaseFirestore firestoreDb;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_confirm);

        firestoreDb = FirebaseFirestore.getInstance();

        configureUI();
    }

    private void configureUI() {
        defineView();
        createListeners();
        setDataInFields();
    }

    private void defineView() {
        emailText = findViewById(R.id.emailAddress);
        firstNameText = findViewById(R.id.firstName);
        lastNameText = findViewById(R.id.lastName);
    }

    private void setDataInFields() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String firstName = "";
        String lastName = "";
        String email = firebaseUser.getEmail();
        String displayedName = firebaseUser.getDisplayName();
        if(displayedName != null) {
            firstName = displayedName.substring(0, displayedName.lastIndexOf(" "));
            lastName = displayedName.substring(displayedName.lastIndexOf(" ") + 1);
        }

        emailText.setText(email);
        firstNameText.setText(firstName);
        lastNameText.setText(lastName);
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

    public void confirmInfo(View v) {
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String emailAddress = emailText.getText().toString();
        String id = firebaseUser.getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("emailAddress", emailAddress);

        firestoreDb
                .collection("users")
                .document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        goToMainScreen();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", e.getMessage());
                    }
                });
    }

    private void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

}
