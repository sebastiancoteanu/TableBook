package com.sebas.licenta1.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sebas.licenta1.entities.AppUser;
import com.sebas.licenta1.entities.PlaceDetails;
import com.sebas.licenta1.entities.Reservation;

import java.util.ArrayList;
import java.util.Map;

public class UserViewModel extends ViewModel {
    private MutableLiveData<AppUser> user;
    private MutableLiveData<ArrayList<PlaceDetails>> favoritePlaces;
    private MutableLiveData<PlaceDetails> favoritePlace;
    private static final CollectionReference userRef =
            FirebaseFirestore.getInstance().collection("users");

    public MutableLiveData<AppUser> getUser() {
        if(user == null) {
            user = new MutableLiveData<>();
            loadUser();
        }
        return user;
    }

    public MutableLiveData<PlaceDetails> getFavoritePlace(PlaceDetails placeDetails) {
        if(favoritePlaces == null) {
            favoritePlace = new MutableLiveData<>();
            loadFavoritePlace(placeDetails.getPlaceID());
        }
        return favoritePlace;
    }

    public Task<Void> addReservation(Reservation reservation) {
        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .collection("reservations")
            .document()
            .set(reservation);
    }

    public Task<Void> addToFavorite(PlaceDetails placeDetails) {
        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .collection("favoritePlaces")
            .document(placeDetails.getPlaceID())
            .set(placeDetails);
    }

    public Task<Void> removeFromFavorite(PlaceDetails placeDetails) {
        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .collection("favoritePlaces")
            .document(placeDetails.getPlaceID())
            .delete();
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

    private Task<DocumentSnapshot> loadFavoritePlace(String placeId) {
        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .collection("favoritePlaces")
            .document(placeId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()) {
                    favoritePlace.setValue(documentSnapshot.toObject(PlaceDetails.class));
                } else {
                    favoritePlace.setValue(null);
                }
            });
    }

    private Task<DocumentSnapshot> loadUser() {
        return userRef
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // user is registered in database
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()) {
                        user.setValue(documentSnapshot.toObject(AppUser.class));
                    } else {
                        user.setValue(null);
                    }
                }
            });
    }
}
