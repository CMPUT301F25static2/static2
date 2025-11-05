package com.ualberta.eventlottery.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ualberta.eventlottery.model.Event;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Live data class for an event list coming from firestore database. This class takes care of
 * listening to a firestore collection.
 *
 * Google search terms:
 *       firebase firestore livedata recyclerview android java
 *       android java recyclerview attached to firebase database
 */
public class EventListLiveData extends LiveData<List<Event>> implements EventListener<QuerySnapshot> {

    private CollectionReference collectionRef = null;
    private Query query = null;
    private ListenerRegistration registration;

    /**
     * Constructs a new {@code EventListLiveData} object with the specified collection reference.
     * @param collectionRef Reference to collection object containing the live data.
     */
    public EventListLiveData(CollectionReference collectionRef) {
        this.collectionRef = collectionRef;
    }

    /**
     * Constructs a new {@code EventListLiveData} object with the specified collection reference.
     * @param query Query object for the events to be included in the live data.
     */
    public EventListLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        super.onActive();
        if(collectionRef != null) {
            registration = collectionRef.addSnapshotListener(this);
        } else if (query != null) {
            registration = query.addSnapshotListener(this);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (registration != null) {
            registration.remove();
        }
    }

    /**
     * {@code onEvent} will be called with the new value or the error if an error occurred. It's
     * guaranteed that exactly one of value or error will be non-{@code null}.
     *
     * Sets the value of the live data using data from {@code snapshots}.
     *
     * @param snapshots The value of the event. {@code null} if there was an error.
     * @param e The error if there was error. {@code null} otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.e("Event Lottery", "Snapshot error", e);
            return;
        }

        List<Event> data = new ArrayList<>();
        if (snapshots != null) {
            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                Event model = doc.toObject(Event.class);
                if (model != null) {
                    data.add(model);
                }
            }
        }
        setValue(data);
    }
}



