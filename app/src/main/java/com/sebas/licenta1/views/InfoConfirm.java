package com.sebas.licenta1.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sebas.licenta1.R;
import com.sebas.licenta1.entities.AppUser;
import com.sebas.licenta1.utils.LoadingDialog;
import com.sebas.licenta1.viewmodels.UserViewModel;

import java.util.HashMap;
import java.util.Map;

public class InfoConfirm extends AppCompatActivity {
    private EditText emailText;
    private EditText firstNameText;
    private EditText lastNameText;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore firestoreDb;
    private FirebaseUser firebaseUser;
    private UserViewModel userVm;
    private AppUser appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_confirm);

        firestoreDb = FirebaseFirestore.getInstance();
        userVm = ViewModelProviders.of(this).get(UserViewModel.class);

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
        loadingDialog = new LoadingDialog(this);
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

    private void goToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void confirmInfo(View v) {
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String emailAddress = emailText.getText().toString();
        String profileImgUrl = firebaseUser.getPhotoUrl().toString();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("emailAddress", emailAddress);
        userMap.put("profileImgUrl", profileImgUrl);

        loadingDialog.show();

        userVm
            .createUser(userMap)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                loadingDialog.dismiss();
                goToMainScreen();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                loadingDialog.dismiss();
                Log.w("Error", e.getMessage());
                }
            });
    }

}
