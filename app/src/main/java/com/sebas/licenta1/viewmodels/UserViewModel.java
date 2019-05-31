package com.sebas.licenta1.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sebas.licenta1.entities.AppUser;

public class UserViewModel extends ViewModel {
    private MutableLiveData<AppUser> user;
    private static final CollectionReference userRef =
            FirebaseFirestore.getInstance().collection("users");

    public MutableLiveData<AppUser> getUser() {
        if(user == null) {
            user = new MutableLiveData<AppUser>();
            loadUser();
        }
        return user;
    }

    public void updateUser(final AppUser user) {
        userRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getUser().setValue(user);
            }
        });
    }

    private void loadUser() {
        userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // user is registered in database
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()) {
                        user.setValue(documentSnapshot.toObject(AppUser.class));
                    }
                }
                }
            });
    }
}
