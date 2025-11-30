package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventCategory;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.TimeRange;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ViewModel for the Home Fragment that manages event lists.
 */
public class HomeViewModel extends ViewModel {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    private final LiveData<List<Event>> availableEventListLiveData;
    private final MutableLiveData<List<EventCategory>> selectedCategoryFilters = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<DayOfWeek>> selectedDaysOfWeekFilters = new MutableLiveData<>();
    private final MutableLiveData<List<TimeRange>> selectedTimeFilters = new MutableLiveData<>();
    private final MediatorLiveData<List<Event>> filteredAvailableEventList = new MediatorLiveData<>();


    // This LiveData will now be used for the "My Events" tab.
    private final MutableLiveData<List<Event>> myEvents = new MutableLiveData<>();
    private final MediatorLiveData<List<Event>> filteredMyEventList = new MediatorLiveData<>();


    /**
     * Constructs a HomeViewModel and initializes events from EventRepository
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public HomeViewModel() {
        eventRepository = EventRepository.getInstance();
        registrationRepository = RegistrationRepository.getInstance();

        // This correctly gets the live data for "Available" events from the repository
        availableEventListLiveData = eventRepository.getAvailableEvents();
        selectedCategoryFilters.setValue(Stream.of(EventCategory.values())
                .collect(Collectors.toList()));
        selectedDaysOfWeekFilters.setValue(Stream.of(DayOfWeek.values())
                .collect(Collectors.toList()));
        selectedTimeFilters.setValue(Stream.of(TimeRange.values())
                .collect(Collectors.toList()));

        //Observes the availableEventListLiveData and applies filter on new data
        filteredAvailableEventList.addSource(availableEventListLiveData, newData -> {
            filteredAvailableEventList.setValue(
                    applyAllFilters(
                            newData,
                            selectedCategoryFilters.getValue(),
                            selectedDaysOfWeekFilters.getValue(),
                            selectedTimeFilters.getValue()
                    )
            );

        });

        //Category source for available events
        filteredAvailableEventList.addSource(selectedCategoryFilters, newFilters -> {
            filteredAvailableEventList.setValue(
                    applyAllFilters(
                            availableEventListLiveData.getValue(),
                            newFilters,
                            selectedDaysOfWeekFilters.getValue(),
                            selectedTimeFilters.getValue()
                    )
            );
        });

        //Days of the week source for available events
        filteredAvailableEventList.addSource(selectedDaysOfWeekFilters, newDaysOfWeek -> {
            filteredAvailableEventList.setValue(
                    applyAllFilters(
                            availableEventListLiveData.getValue(),
                            selectedCategoryFilters.getValue(),
                            newDaysOfWeek,
                            selectedTimeFilters.getValue()
                    )
            );
        });

        //Time Ranges source for available events
        filteredAvailableEventList.addSource(selectedTimeFilters, newTimeRanges -> {
            filteredAvailableEventList.setValue(
                    applyAllFilters(
                            availableEventListLiveData.getValue(),
                            selectedCategoryFilters.getValue(),
                            selectedDaysOfWeekFilters.getValue(),
                            newTimeRanges
                    )
            );
        });

        //Observes myEvents and applies filter on new data
        filteredMyEventList.addSource(myEvents, newData -> {
            filteredMyEventList.setValue(
                    applyAllFilters(
                            newData,
                            selectedCategoryFilters.getValue(),
                            selectedDaysOfWeekFilters.getValue(),
                            selectedTimeFilters.getValue()
                    )
            );
        });
        filteredMyEventList.addSource(selectedCategoryFilters, newFilters -> {
            filteredMyEventList.setValue(
                    applyAllFilters(
                            myEvents.getValue(),
                            newFilters,
                            selectedDaysOfWeekFilters.getValue(),
                            selectedTimeFilters.getValue()
                    )
            );
        });
        filteredMyEventList.addSource(selectedDaysOfWeekFilters, newFilters -> {
            filteredMyEventList.setValue(
                    applyAllFilters(
                            myEvents.getValue(),
                            selectedCategoryFilters.getValue(),
                            newFilters,
                            selectedTimeFilters.getValue()
                    )
            );
        });
        filteredMyEventList.addSource(selectedTimeFilters, newFilters -> {
            filteredMyEventList.setValue(
                    applyAllFilters(
                            myEvents.getValue(),
                            selectedCategoryFilters.getValue(),
                            selectedDaysOfWeekFilters.getValue(),
                            newFilters
                    )
            );
        });
    }

    /**
     * Returns LiveData containing the list of available events.
     * An available event is an event that is open for registration.
     * @return LiveData object containing a list of available {@code Event} objects.
     */
    public LiveData<List<Event>> getAvailableEvents() {
        return filteredAvailableEventList;
    }

    /**
     * Getter for the new "My Events" LiveData
     *
     * @return The LiveData for the list of events the user has registered for.
     */
    public LiveData<List<Event>> getMyEvents() {
        return filteredMyEventList;
    }

    public List<EventCategory> getSelectedCategories() {
        return selectedCategoryFilters.getValue();
    }

    public List<DayOfWeek> getSelectedDaysOfWeek() {
        return selectedDaysOfWeekFilters.getValue();
    }

    public List<TimeRange> getSelectedTimeRanges() {
        return selectedTimeFilters.getValue();
    }

    public void applyCategoryFilters(List<EventCategory> selectedCategories) {
        if (selectedCategories.size() == 0) {
            selectedCategoryFilters.setValue(Stream.of(EventCategory.values())
                    .collect(Collectors.toList()));
        } else {
            this.selectedCategoryFilters.setValue(selectedCategories);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void applyDaysOfWeekFilter(List<DayOfWeek> selectedDaysOfWeek) {
        if (selectedDaysOfWeek.size() == 0) {
            selectedDaysOfWeekFilters.setValue(Stream.of(DayOfWeek.values())
                    .collect(Collectors.toList()));
        } else {
            this.selectedDaysOfWeekFilters.setValue(selectedDaysOfWeek);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void applyTimeRangeFilter(List<TimeRange> selectedTimeRanges) {
        if (selectedTimeRanges.size() == 0) {
            selectedTimeFilters.setValue(Stream.of(TimeRange.values())
                    .collect(Collectors.toList()));
        } else {
            this.selectedTimeFilters.setValue(selectedTimeRanges);
        }
    }

    private List<Event> applyCategoryFilters(List<Event> currentData, List<EventCategory> categories) {
        if (currentData == null) {
            return new ArrayList<>();
        }

        List<Event> resultList = new ArrayList<>();
        if (categories == null || categories.isEmpty()) {
            resultList.addAll(currentData);
        } else {
            for (Event event : currentData) {
                if (event.getCategory() == null) {
                    continue;
                }
                String category = event.getCategory();

                if ((category == null && categories.size() == 0) || (category != null && categories.contains(EventCategory.valueOf(category.toUpperCase())))) {
                    resultList.add(event);
                }
            }
        }
        return resultList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Event> applyDaysOfWeekFilter(List<Event> currentData, List<DayOfWeek> daysOfWeek) {
        if (currentData == null) {
            return new ArrayList<>();
        }

        Calendar cal = Calendar.getInstance();

        List<Event> resultList = new ArrayList<>();

        if (daysOfWeek == null || daysOfWeek.isEmpty() || daysOfWeek.size() == DayOfWeek.values().length) {
            resultList.addAll(currentData);
        } else {
            for (Event event : currentData) {
                if (event.getStartTime() == null) {
                    continue;
                }

                cal.setTime(event.getStartTime());
                if (daysOfWeek.contains(DayOfWeek.of(cal.get(Calendar.DAY_OF_WEEK)))) {
                    resultList.add(event);
                }
            }
        }
        return resultList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Event> applyTimeRangesFilter(List<Event> currentData, List<TimeRange> timeRanges) {
        if (currentData == null) {
            return new ArrayList<>();
        }

        List<Event> resultList = new ArrayList<>();

        if (timeRanges == null || timeRanges.isEmpty() || timeRanges.size() == TimeRange.values().length) {
            resultList.addAll(currentData);
        } else {
            for (Event event : currentData) {
                if (event.getDailyStartTime() == null) {
                    continue;
                }
               for (TimeRange tr : timeRanges) {
                   if (tr.isInRange(event.getDailyStartTime())) {
                       resultList.add(event);
                   }
               }
            }
        }
        return resultList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Event> applyAllFilters(List<Event> currentData, List<EventCategory> selectedCategories, List<DayOfWeek> selectedDaysOfWeek, List<TimeRange> selectedTimeRanges) {
        List<Event> filteredEvents = applyCategoryFilters(currentData, selectedCategories);
        filteredEvents = applyDaysOfWeekFilter(filteredEvents, selectedDaysOfWeek);
        filteredEvents = applyTimeRangesFilter(filteredEvents, selectedTimeRanges);
        return filteredEvents;
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
