package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements EventAdapter.OnEventListener {
    private Button filterButton, sortButton, myEventsButton, availableEventsButton, historyEventsButton;
    private EditText searchInputHome;
    private RecyclerView recyclerView;
    private EventAdapter myEventsAdapter, availableEventsAdapter, historyEventsAdapter;
    private List<Event> myEventsList;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private Observer<List<Event>> availableEventsObserver;
    private Observer<List<Event>> historyEventsObserver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        searchInputHome = view.findViewById(R.id.searchInputHome);
        filterButton = view.findViewById(R.id.filterButton);
        sortButton = view.findViewById(R.id.sortButton);
        myEventsButton = view.findViewById(R.id.myEventsButton);
        availableEventsButton = view.findViewById(R.id.availableEventsButton);
        historyEventsButton = view.findViewById(R.id.historyEventsButton); // Find the new button
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //Test List
        myEventsList = getmyMockEvents("My Event");

        // Initialize adapters and observers
        myEventsAdapter = new EventAdapter(myEventsList, this);
        availableEventsAdapter = new EventAdapter(new ArrayList<>(), this);
        historyEventsAdapter = new EventAdapter(new ArrayList<>(), this); // Adapter for history

        availableEventsObserver = newData -> {
            if (newData != null) {
                availableEventsAdapter.updateEvents(newData);
            }
        };

        historyEventsObserver = newData -> {
            if (newData != null) {
                historyEventsAdapter.updateEvents(newData);
            }
        };

        // Set initial view
        showMyEvents();


        //Filter and sort placeholders
        filterButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Filter options coming soon!", Toast.LENGTH_SHORT).show());

        sortButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Sort options coming soon!", Toast.LENGTH_SHORT).show());

        //Search Bar implementation
        searchInputHome.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = searchInputHome.getText().toString().trim();
                if (!query.isEmpty()) {
                    Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        //Navigate to different event views
        myEventsButton.setOnClickListener(v -> showMyEvents());
        availableEventsButton.setOnClickListener(v -> showAvailableEvents());
        historyEventsButton.setOnClickListener(v -> showHistoryEvents()); // Set listener for the new button

        return view;
    }

    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event.getId());
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_home_to_details, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void resetAllButtonStyles() {
        Button[] buttons = {myEventsButton, availableEventsButton, historyEventsButton};
        for (Button btn : buttons) {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.black));
            btn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
        }
    }

    private void setActiveButtonStyle(Button activeButton) {
        activeButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black));
    }

    private void showMyEvents() {
        // Stop observing other data sources
        homeViewModel.getAvailableEvents().removeObserver(availableEventsObserver);
        homeViewModel.getHistoryEvents().removeObserver(historyEventsObserver);

        myEventsAdapter.updateEvents(myEventsList);
        recyclerView.setAdapter(myEventsAdapter);

        resetAllButtonStyles();
        setActiveButtonStyle(myEventsButton);
    }

    private void showAvailableEvents() {
        // Stop observing history and start observing available events
        homeViewModel.getHistoryEvents().removeObserver(historyEventsObserver);
        homeViewModel.getAvailableEvents().observe(getViewLifecycleOwner(), availableEventsObserver);

        recyclerView.setAdapter(availableEventsAdapter);

        resetAllButtonStyles();
        setActiveButtonStyle(availableEventsButton);
    }

    private void showHistoryEvents() {
        // Stop observing available events and start observing history
        homeViewModel.getAvailableEvents().removeObserver(availableEventsObserver);
        homeViewModel.getHistoryEvents().observe(getViewLifecycleOwner(), historyEventsObserver);

        // Tell the ViewModel to fetch the data
        homeViewModel.loadHistoryEvents();

        recyclerView.setAdapter(historyEventsAdapter);

        resetAllButtonStyles();
        setActiveButtonStyle(historyEventsButton);
    }

    //Test
    private List<Event> getmyMockEvents(String prefix) {
        List<Event> list = new ArrayList<>();
        Event modelEvent = new Event("789", "EzKYezj7iLXKlRqCIgFbp8CH1Hh2", "PickleBall Tournament", "Tournament for all skill levels!");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        modelEvent.setRegistrationStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, +10);
        modelEvent.setRegistrationEnd(cal.getTime());
        modelEvent.addToWaitingList("EzKYezj7iLXKlRqCIgFbp8CH1Hh2");
        modelEvent.addToWaitingList("AzKYezj7iLXKlRqCIgFbp8CH1Hh3");
        modelEvent.addToWaitingList("BzKYezj7iLXKlRqCIgFbp8CH1Hh4");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        list.add(modelEvent);

        modelEvent = new Event("012", "EzKYezj7iLXKlRqCIgFbp8CH1Hh2", "Piano Lessons", "Play like Mozart");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        modelEvent.setRegistrationStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, +10);
        modelEvent.setRegistrationEnd(cal.getTime());
        modelEvent.addToWaitingList("EzKYezj7iLXKlRqCIgFbp8CH1Hh2");
        modelEvent.addToWaitingList("AzKYezj7iLXKlRqCIgFbp8CH1Hh3");
        modelEvent.addToWaitingList("BzKYezj7iLXKlRqCIgFbp8CH1Hh4");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        list.add(modelEvent);
        return list;
    }
}
