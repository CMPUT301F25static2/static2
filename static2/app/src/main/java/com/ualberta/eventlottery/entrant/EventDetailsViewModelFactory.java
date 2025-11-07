package com.ualberta.eventlottery.entrant;



import androidx.annotation.NonNull;import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ualberta.eventlottery.ui.home.entrant.EventDetailsViewModel;

/**
 * Factory for creating a {@link EventDetailsViewModel} with a constructor that takes an event ID.
 */
public class EventDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final String eventId;

    /**
     * Constructs a new EventDetailsViewModelFactory.
     *
     * @param eventId The ID of the event.
     * @throws IllegalArgumentException if the event ID is null.
     */
    public EventDetailsViewModelFactory(String eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        this.eventId = eventId;
    }

    /**
     * Creates a new instance of the given {@code Class}.
     *
     * @param modelClass a {@code Class} whose instance is requested
     * @return a newly created ViewModel
     */
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
