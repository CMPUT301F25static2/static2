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

/**
 * Fragment that displays home screen for the entrants.
 * Provides functionality to switch between My Events and Available Events
 *Options provided for filter, sort and search
 */
public class HomeFragment extends Fragment implements EventAdapter.OnEventListener {
    // History button has been removed
    private Button filterButton, sortButton, myEventsButton, availableEventsButton;
    private EditText searchInputHome;
    private RecyclerView recyclerView;
    // History adapter has been removed
    private EventAdapter myEventsAdapter, availableEventsAdapter;
    private List<Event> myEventsList;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private Observer<List<Event>> availableEventsObserver;
    // This observer will now be for the "My Events" data
    private Observer<List<Event>> myEventsObserver;

    /**
     * Creates initializes the view for the home fragment.
     * Sets up the RecyclerView with event adapter, initializing different ui components,
     * and configures event listeners for search, filter, sort, and navigation button.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the root view of the fragment's layout
     */
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
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters and observers
        myEventsAdapter = new EventAdapter(new ArrayList<>(), this); // Now starts empty
        availableEventsAdapter = new EventAdapter(new ArrayList<>(), this);

        availableEventsObserver = newData -> {
            if (newData != null) {
                availableEventsAdapter.updateEvents(newData);
            }
        };

        // Observer for the dynamic "My Events" list
        myEventsObserver = newData -> {
            if (newData != null) {
                myEventsAdapter.updateEvents(newData);
            }
        };

        // Set initial view to "My Events"
        showMyEvents();

        // --- Listeners ---
        filterButton.setOnClickListener(v -> Toast.makeText(getContext(), "Filter options coming soon!", Toast.LENGTH_SHORT).show());
        sortButton.setOnClickListener(v -> Toast.makeText(getContext(), "Sort options coming soon!", Toast.LENGTH_SHORT).show());

        searchInputHome.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Search logic here
                return true;
            }
            return false;
        });

        // Navigation listeners
        myEventsButton.setOnClickListener(v -> showMyEvents());
        availableEventsButton.setOnClickListener(v -> showAvailableEvents());

        return view;
    }

    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event.getId());
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_home_to_details, bundle);
    }

    /**
     * Cleans up UI resources to prevent memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void resetAllButtonStyles() {
        // Now only handles two buttons
        Button[] buttons = {myEventsButton, availableEventsButton};
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

        // Stop observing the other LiveData to prevent getting unwanted updates.
        homeViewModel.getAvailableEvents().removeObservers(getViewLifecycleOwner());

        // Start observing the LiveData for "My Events"
        homeViewModel.getMyEvents().observe(getViewLifecycleOwner(), myEventsObserver);

        // Tell the ViewModel to fetch the data for "My Events"
        homeViewModel.loadMyRegisteredEvents();

        recyclerView.setAdapter(myEventsAdapter);
        resetAllButtonStyles();
        setActiveButtonStyle(myEventsButton);
    }

    private void showAvailableEvents() {

        // Stop observing the other LiveData.
        homeViewModel.getMyEvents().removeObservers(getViewLifecycleOwner());

        // Start observing the LiveData for "Available Events"
        homeViewModel.getAvailableEvents().observe(getViewLifecycleOwner(), availableEventsObserver);

        recyclerView.setAdapter(availableEventsAdapter);
        resetAllButtonStyles();
        setActiveButtonStyle(availableEventsButton);
    }
}