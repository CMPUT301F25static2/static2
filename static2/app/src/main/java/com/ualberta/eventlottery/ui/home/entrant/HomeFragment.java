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
public class HomeFragment extends Fragment {
    private Button filterButton, sortButton, myEventsButton, availableEventsButton;
    private EditText searchInputHome;
    private RecyclerView recyclerView;
    private EventAdapter myEventsAdapter, availableEventsAdapter;
    private List<Event> myEventsList;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private Observer<List<Event>> availableEventsObserver;

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


        //Test List
        myEventsList = getmyMockEvents("My Event");

        myEventsAdapter = new EventAdapter(myEventsList);
        availableEventsAdapter = new EventAdapter(new ArrayList<>());
        availableEventsObserver = newData -> {
            availableEventsAdapter.updateEvents(newData);
            availableEventsAdapter.notifyDataSetChanged();
        };

        recyclerView.setAdapter(myEventsAdapter);
        myEventsButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        myEventsButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black));


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

        return view;
    }

    /**
     * Cleans up UI resources to prevent memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showMyEvents() {
        //Stop observing available events when we're showing my events
        homeViewModel.getAvailableEvents().removeObserver(availableEventsObserver);
        myEventsAdapter.updateEvents(myEventsList);
        recyclerView.setAdapter(myEventsAdapter);
        myEventsButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        myEventsButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black));
        availableEventsButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.black));
        availableEventsButton.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.white));

    }
    private void showAvailableEvents(){
        homeViewModel.getAvailableEvents().observe(getViewLifecycleOwner(),availableEventsObserver);
        recyclerView.setAdapter(availableEventsAdapter);

        availableEventsButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        availableEventsButton.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.black));
        myEventsButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.black));
        myEventsButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    //Test
    private List<Event> getmyMockEvents(String prefix) {
        List<Event> list = new ArrayList<>();
        Event modelEvent = new Event("789", "EzKYezj7iLXKlRqCIgFbp8CH1Hh2", "PickleBall Tournament", "Tournament for all skill levels!");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        modelEvent.setRegistrationStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH,+10);
        modelEvent.setRegistrationEnd(cal.getTime());
        modelEvent.addToWaitingList("EzKYezj7iLXKlRqCIgFbp8CH1Hh2");
        modelEvent.addToWaitingList("AzKYezj7iLXKlRqCIgFbp8CH1Hh3");
        modelEvent.addToWaitingList("BzKYezj7iLXKlRqCIgFbp8CH1Hh4");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        list.add(modelEvent);

        modelEvent = new Event("012", "EzKYezj7iLXKlRqCIgFbp8CH1Hh2","Piano Lessons", "Play like Mozart");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        modelEvent.setRegistrationStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH,+10);
        modelEvent.setRegistrationEnd(cal.getTime());
        modelEvent.addToWaitingList("EzKYezj7iLXKlRqCIgFbp8CH1Hh2");
        modelEvent.addToWaitingList("AzKYezj7iLXKlRqCIgFbp8CH1Hh3");
        modelEvent.addToWaitingList("BzKYezj7iLXKlRqCIgFbp8CH1Hh4");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        list.add(modelEvent);
        return list;
    }
}