package com.ualberta.eventlottery.ui.home.entrant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventListLiveData;
import com.ualberta.eventlottery.repository.EventRepository;

import java.util.List;

/**
 * ViewModel for the Home Fragment that manages event lists.
 */
public class HomeViewModel extends ViewModel {

    private final EventListLiveData availableEventListLiveData;


    /**
     * Constructs a HomeViewModel and initializes events from EventRepository
     */
    public HomeViewModel() {
        availableEventListLiveData = EventRepository.getInstance().getAvailableEvents();
    }

    /**
     * Returns LiveData containing the list of available events.
     * An available event is an event that is open for registration.
     * @return LiveData object containing a list of available {@code Event} objects.
     */
    public LiveData<List<Event>> getAvailableEvents() {
        return availableEventListLiveData;
    }
}