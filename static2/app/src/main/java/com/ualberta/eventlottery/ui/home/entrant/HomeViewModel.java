package com.ualberta.eventlottery.ui.home.entrant;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel for the Home Fragment that manages event lists.
 */
public class HomeViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    private final LiveData<List<Event>> availableEventListLiveData;
    // This LiveData will now be used for the "My Events" tab.
    private final MutableLiveData<List<Event>> myEvents = new MutableLiveData<>();

    /**
     * Constructs a HomeViewModel and initializes events from EventRepository
     */
    public HomeViewModel() {
        eventRepository = EventRepository.getInstance();
        registrationRepository = RegistrationRepository.getInstance();

        // This correctly gets the live data for "Available" events from the repository
        availableEventListLiveData = eventRepository.getAvailableEvents();
    }

    /**
     * Returns LiveData containing the list of available events.
     * An available event is an event that is open for registration.
     * @return LiveData object containing a list of available {@code Event} objects.
     */
    public LiveData<List<Event>> getAvailableEvents() {
        return availableEventListLiveData;
    }

    /**
     * Getter for the new "My Events" LiveData
     *
     * @return The LiveData for the list of events the user has registered for.
     */
    public LiveData<List<Event>> getMyEvents() {
        return myEvents;
    }

    /**
     * Method to trigger loading the user's registered events
     */
    public void loadMyRegisteredEvents() {
        String currentUserId = UserManager.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            myEvents.setValue(new ArrayList<>()); // Post empty list if not logged in
            return;
        }


        registrationRepository.getRegistrationsByEntrant(currentUserId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                if (registrations == null || registrations.isEmpty()) {
                    myEvents.setValue(new ArrayList<>()); // Post empty list if no registrations
                    return;
                }

                // 2. Extract event IDs from the registrations
                List<String> eventIds = registrations.stream()
                        .map(Registration::getEventId)
                        .filter(id -> id != null && !id.isEmpty())
                        .collect(Collectors.toList());

                if (eventIds.isEmpty()) {
                    myEvents.setValue(new ArrayList<>());
                    return;
                }

                // 3. Fetch event details for those IDs
                eventRepository.getEventsByIds(eventIds, new EventRepository.EventListCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        // Here you could add filtering for upcoming events if needed
                        myEvents.setValue(events);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("HomeViewModel", "Failed to fetch event details for My Events", e);
                        myEvents.setValue(null); // Or an empty list
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HomeViewModel", "Failed to load registrations for My Events", e);
                myEvents.setValue(null); // Or an empty list
            }
        });
    }
}
