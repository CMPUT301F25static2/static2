package com.ualberta.eventlottery.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.ualberta.eventlottery.model.Event;

/**
 * LiveData class for a single Event document from Firestore.
 * This class listens for real-time updates to a specific document.
 */
public class EventLiveData extends LiveData<Event> implements EventListener<DocumentSnapshot> {

    private final DocumentReference docRef;
    private ListenerRegistration registration;

    public EventLiveData(DocumentReference docRef) {
        this.docRef = docRef;
    }

    @Override
    protected void onActive() {
        super.onActive();
        // When this LiveData becomes active, start listening for document changes.
        registration = docRef.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        // When this LiveData is no longer observed, remove the listener to save resources.
        if (registration != null) {
            registration.remove();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.e("EventLottery", "Listen failed on document.", e);
            return;
        }

        if (snapshot != null && snapshot.exists()) {
            // Convert the DocumentSnapshot to an Event object and update the LiveData value.
            Event event = EventRepository.fromDocument(snapshot);
            setValue(event);
        } else {
            Log.d("EventLottery", "Current data: null");
            setValue(null); // Document was deleted or does not exist
        }
    }
}
