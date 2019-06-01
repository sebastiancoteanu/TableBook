package com.sebas.licenta1.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sebas.licenta1.entities.AppUser;
import com.sebas.licenta1.entities.Reservation;

import java.util.ArrayList;
import java.util.Map;

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

    public Task<Void> addReservation(Reservation reservation) {
        ArrayList<Reservation> reservationList = user.getValue().getReservations();
        if(reservationList == null) {
            reservationList = new ArrayList<>();
        }
        reservationList.add(reservation);

        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .update("reservations", reservationList);
    }

    public Task<Void> createUser(Map<String, Object> updatedFields) {
        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .set(updatedFields);
    }

    public Task<Void> updateUser(Map<String, Object> updatedFields) {
        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .update(updatedFields);
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
