package com.ualberta.eventlottery.ui.home.entrant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventRepository;

/**
 * ViewModel for the Event Details screen.
 */
public class EventDetailsViewModel extends ViewModel {

    private final LiveData<Event> eventLiveData;

    /**
     * Constructs a new EventDetailsViewModel.
     *
     * @param eventId The ID of the event to be displayed.
     */
    public EventDetailsViewModel(String eventId) {
        eventLiveData = EventRepository.getInstance().getEventById(eventId);
    }

    /**
     * Returns the LiveData for the event.
     *
     * @return The LiveData for the event.
     */
    public LiveData<Event> getEventLiveData() {
        return eventLiveData;
    }
}
