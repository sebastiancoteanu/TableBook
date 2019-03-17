package com.sebas.licenta1.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.sebas.licenta1.BuildConfig;
import com.sebas.licenta1.R;
import com.sebas.licenta1.dao.AppUser;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestoreDb;
    private AppUser appUser;
    private ImageButton signOut;
    private DocumentReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        configureAuth();
        configureDb();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signOutButton:
                signOut();
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        createListeners(view);
    }

    private void createListeners(View v) {
        signOut = v.findViewById(R.id.signOutButton);
        signOut.setOnClickListener(this);

        appUser = ((MainActivity) getActivity()).getAppUser();
        if(appUser != null) {
            Log.d("Obiectul user:", appUser.toString());
            setDataInFields();
            return;
        }

        usersRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                appUser = documentSnapshot.toObject(AppUser.class);
                if(appUser != null) {
                    ((MainActivity) getActivity()).setAppUser(appUser);
                    setDataInFields();
                } else {
                    Log.d("Error", "User object is empty.");
                }

            }
        });
    }

    private void setDataInFields() {
        TextView firstLastName = getView().findViewById(R.id.firstLastName);
        TextView emailAddress = getView().findViewById(R.id.emailAddress);

        firstLastName.setText(appUser.getFullName());
        emailAddress.setText(appUser.getEmailAddress());
    }

    private void configureDb() {
        firestoreDb = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        usersRef = firestoreDb.collection("users").document(firebaseUser.getUid());
    }

    private void configureAuth() {
        String googleOAuthClientId = BuildConfig.GoogleOAuthClientId;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleOAuthClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();
    }

    private void signOut() {
        // Firebase sign out
        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmSignOut();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative statement
                    }
                }).show();
    }

    private void confirmSignOut() {
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        switchIntroActivity();
                    }
                });
    }

    public void switchIntroActivity() {
        Intent intent = new Intent(getActivity(), IntroActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

}
