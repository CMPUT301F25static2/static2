package com.ualberta.eventlottery.ui.home.entrant;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EventDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final String eventId;

    public EventDetailsViewModelFactory(String eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EventDetailsViewModel.class)) {
            // This will call the constructor: new EventDetailsViewModel(eventId)
            return (T) new EventDetailsViewModel(eventId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
