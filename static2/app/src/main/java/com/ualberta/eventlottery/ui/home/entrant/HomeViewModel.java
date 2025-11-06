package com.ualberta.eventlottery.ui.home.entrant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventListLiveData;
import com.ualberta.eventlottery.repository.EventRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final EventListLiveData availableEventListLiveData;


    public HomeViewModel() {
        availableEventListLiveData = EventRepository.getInstance().getAvailableEvents();
    }

    public LiveData<List<Event>> getAvailableEvents() {
        return availableEventListLiveData;
    }
}