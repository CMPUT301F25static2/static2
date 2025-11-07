package com.ualberta.eventlottery.ui.home.entrant;

import android.util.Log;

import androidx.lifecycle.LiveData;import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EventListLiveData;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    private final LiveData<List<Event>> availableEventListLiveData; // Changed from EventListLiveData
    private final MutableLiveData<List<Event>> historyEvents = new MutableLiveData<>();

    public HomeViewModel() {
        eventRepository = EventRepository.getInstance();
        registrationRepository = RegistrationRepository.getInstance();

        availableEventListLiveData = eventRepository.getAvailableEvents();
    }

    public LiveData<List<Event>> getAvailableEvents() {
        return availableEventListLiveData;
    }

    // Getter for the new history events LiveData
    public LiveData<List<Event>> getHistoryEvents() {
        return historyEvents;
    }

    // Method to trigger loading the registration history
    public void loadHistoryEvents() {
        String currentUserId = UserManager.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            historyEvents.setValue(new ArrayList<>()); // Post empty list if not logged in
            return;
        }

        // 1. Get all registrations for the current user
        registrationRepository.getRegistrationsByEntrant(currentUserId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                if (registrations == null || registrations.isEmpty()) {
                    historyEvents.setValue(new ArrayList<>()); // Post empty list if no registrations
                    return;
                }


                List<String> eventIds = registrations.stream()
                        .map(Registration::getEventId)
                        .filter(id -> id != null && !id.isEmpty())
                        .collect(Collectors.toList());

                if (eventIds.isEmpty()) {
                    historyEvents.setValue(new ArrayList<>());
                    return;
                }

                eventRepository.getEventsByIds(eventIds, new EventRepository.EventListCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        historyEvents.setValue(events);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("HomeViewModel", "Failed to fetch event details for history", e);
                        historyEvents.setValue(null); // Or an empty list to avoid crashes
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HomeViewModel", "Failed to load registration history", e);
                historyEvents.setValue(null); // Or an empty list
            }
        });
    }
}
