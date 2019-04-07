package com.sebas.licenta1.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
    private AlertDialog.Builder loadingBuilder;
    private Dialog loadingDialog;

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
        fetchUser();
    }

    private void showLoadingDialog() {
        loadingDialog.show();
        loadingDialog.getWindow().setLayout(400,400);
    }

    private void fetchUser() {
        appUser = ((MainActivity) getActivity()).getAppUser();
        if(appUser != null) {
            Log.d("Obiectul user:", appUser.toString());
            setDataInFields();
            return;
        }

        showLoadingDialog();

        usersRef.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    appUser = documentSnapshot.toObject(AppUser.class);
                    if(appUser != null) {
                        ((MainActivity) getActivity()).setAppUser(appUser);
                        setDataInFields();
                    } else {
                        loadingDialog.dismiss();
                        Log.d("Error", "User object is empty.");
                    }

                }
            });
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
        dbPhotoUpload(selectedImage);
    }

    private void dbPhotoUpload(Uri imageUri) {
        fileReference = storageRef.child(firebaseUser.getUid());
        showLoadingDialog();
        fileReference.putFile(imageUri)
            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        loadingDialog.dismiss();
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
                        loadingDialog.dismiss();
                    }
                }
        });

    }

    private void loadGlidePhoto() {
        Glide.with(getView())
                .load(appUser.getProfileImgUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingDialog.dismiss();
                        return false;
                    }
                })
                .thumbnail( 0.1f )
                .into(profilePicture);
    }

    private void updateUser(String profileImgUrl) {
        appUser.setProfileImgUrl(profileImgUrl);
        usersRef
            .set(appUser)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loadGlidePhoto();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
            });
    }

    private void defineUI(View v) {
        signOut = v.findViewById(R.id.signOutButton);
        profilePicture = v.findViewById(R.id.profilePicture);
        loadingBuilder = new AlertDialog.Builder(getActivity(),  R.style.AlertDialogTheme);
        loadingBuilder.setView(R.layout.dialog_loading);
        loadingDialog = loadingBuilder.create();
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    private void createListeners(View v) {
        signOut.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
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

        loadGlidePhoto();
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

        AlertDialog signOutDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
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
                }).create();

        signOutDialog.show();
        signOutDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getColor(R.color.colorAccent));
        signOutDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getColor(R.color.colorAccent));
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
