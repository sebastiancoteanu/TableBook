package com.sebas.licenta1.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sebas.licenta1.BuildConfig;
import com.sebas.licenta1.R;
import com.sebas.licenta1.dto.AppUser;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestoreDb;
    private StorageReference storageRef;
    private StorageReference fileReference;
    private AppUser appUser;
    private ImageButton signOut;
    private DocumentReference usersRef;
    private ImageView profilePicture;

    private static final int GALLERY_REQUEST_CODE = 9000;

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
            case R.id.profilePicture:
                pickFromGallery();
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        defineUI(view);
        createListeners(view);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    finalizePhotoPick(data);
                    break;
            }
    }

    private void finalizePhotoPick(Intent data) {
        Uri selectedImage = data.getData();
        profilePicture.setImageURI(selectedImage);
        dbPhotoUpload(selectedImage);
    }

    private void dbPhotoUpload(Uri imageUri) {
        fileReference = storageRef.child(firebaseUser.getUid());
        fileReference.putFile(imageUri)
            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        updateUser(task.getResult().toString());
                    } else {
                        Toast.makeText(getActivity(), "User update failed", Toast.LENGTH_LONG).show();
                    }
                }
        });

    }

    private void updateUser(String profileImgUrl) {
        appUser.setProfileImgUrl(profileImgUrl);
        usersRef
            .set(appUser)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "Database user update", Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void defineUI(View v) {
        signOut = v.findViewById(R.id.signOutButton);
        profilePicture = v.findViewById(R.id.profilePicture);
    }

    private void createListeners(View v) {
        signOut.setOnClickListener(this);
        profilePicture.setOnClickListener(this);

        appUser = ((MainActivity) getActivity()).getAppUser();
        if(appUser != null) {
            Log.d("Obiectul user:", appUser.toString());
            setDataInFields();
            return;
        }

        usersRef.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
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
        storageRef = FirebaseStorage.getInstance().getReference("profilePictures");
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
