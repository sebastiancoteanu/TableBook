package com.sebas.licenta1.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sebas.licenta1.entities.PlaceDetails;

import java.util.List;

import javax.annotation.Nullable;

public class PlaceViewModel extends ViewModel {
    private MutableLiveData<PlaceDetails> place;

    private static final CollectionReference placeRef = FirebaseFirestore.getInstance().collection("places");

    public LiveData<PlaceDetails> getPlace(String placeId) {
        if(place == null) {
            place = new MutableLiveData<PlaceDetails>();
            loadPlace(placeId);
        }
        return place;
    }

    private void loadPlace(String placeId) {
        placeRef
            .whereEqualTo("placeID", placeId)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                    if(documentSnapshots.size() != 0) {
                        place.setValue(documentSnapshots.get(0).toObject(PlaceDetails.class));
                    }
                }
            });
    }
}
