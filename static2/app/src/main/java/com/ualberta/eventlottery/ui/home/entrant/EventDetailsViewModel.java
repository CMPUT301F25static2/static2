package com.ualberta.eventlottery.ui.home.entrant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventRepository;

public class EventDetailsViewModel extends ViewModel {

    private final LiveData<Event> eventLiveData;

    public EventDetailsViewModel(String eventId) {
        // You will need to implement getEventById in your repository
        // This assumes EventRepository is a Singleton
        eventLiveData = EventRepository.getInstance().getEventById(eventId);
    }

    public LiveData<Event> getEventLiveData() {
        return eventLiveData;
    }
}
