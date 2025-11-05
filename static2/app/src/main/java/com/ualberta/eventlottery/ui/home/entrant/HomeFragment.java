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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private Button filterButton,sortButton,myEventsButton,availableEventsButton;
    private EditText searchInputHome;
    private RecyclerView recyclerView;
    private EventAdapter myEventsAdapter, availableEventsAdapter;
    private List<Event> myEventsList;
    private List<Event> availableEventsList;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

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
        availableEventsList = getAvailableMockEvents();

        myEventsAdapter = new EventAdapter(myEventsList);
        availableEventsAdapter = new EventAdapter(availableEventsList);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showMyEvents() {
        myEventsAdapter.updateEvents(myEventsList);
        recyclerView.setAdapter(myEventsAdapter);
        myEventsButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
        myEventsButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black));
        availableEventsButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.black));
        availableEventsButton.setTextColor(ContextCompat.getColorStateList(requireContext(),R.color.white));

    }
    private void showAvailableEvents(){
        availableEventsAdapter.updateEvents(availableEventsList);
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
    private List<Event> getAvailableMockEvents() {
        List<Event> list = new ArrayList<>();
        Event modelEvent = new Event("123", "EzKYezj7iLXKlRqCIgFbp8CH1Hh2", "Swimming Lessons", "Swimming for beginners");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        modelEvent.setRegistrationStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH,+10);
        modelEvent.setRegistrationEnd(cal.getTime());
        modelEvent.addToWaitingList("EzKYezj7iLXKlRqCIgFbp8CH1Hh2");
        modelEvent.addToWaitingList("AzKYezj7iLXKlRqCIgFbp8CH1Hh3");
        modelEvent.addToWaitingList("BzKYezj7iLXKlRqCIgFbp8CH1Hh4");
        modelEvent.setMaxWaitListSize(4);

        list.add(modelEvent);

        modelEvent = new Event("456", "EzKYezj7iLXKlRqCIgFbp8CH1Hh2","Karate Lessons", "Fitness with a purpose");
        modelEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        modelEvent.setRegistrationStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH,+10);
        modelEvent.setRegistrationEnd(cal.getTime());
        modelEvent.addToWaitingList("EzKYezj7iLXKlRqCIgFbp8CH1Hh2");
        modelEvent.addToWaitingList("AzKYezj7iLXKlRqCIgFbp8CH1Hh3");
        list.add(modelEvent);
        return list;
    }

}