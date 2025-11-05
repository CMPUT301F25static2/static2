package com.ualberta.eventlottery.ui.home.entrant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.Event;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final EventListLiveData eventListLiveData;


    public HomeViewModel() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        eventListLiveData = new EventListLiveData(eventsRef);
    }

    // Google search terms:
    //  firebasefirestore iterate collection android java
    //  android java recyclerview attached to firebase database
    //  Firebase database android java design pattern example
    //  firebasefirestore livedata recyclerview android java
    public LiveData<List<Event>> getAvailableEvents() {
        return eventListLiveData;
    }
}