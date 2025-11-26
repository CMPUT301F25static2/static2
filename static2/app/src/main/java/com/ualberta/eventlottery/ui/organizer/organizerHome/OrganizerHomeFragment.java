package com.ualberta.eventlottery.ui.organizer.organizerHome;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.ui.organizer.organizerEventCreate.OrganizerEventCreateFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventDraw.OrganizerEventDrawFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.eventlottery.utils.CSVExportUtil;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerHomeBinding;

import java.util.List;

/**
 * Fragment for organizer's home screen displaying their events.
 * Allows organizers to view, create, and manage their events.
 *
 * @author static2
 * @version 1.0
 */
public class OrganizerHomeFragment extends Fragment {
    public FragmentOrganizerHomeBinding binding;
    private OrganizerEventAdapter adapter;
    private EventRepository eventRepository;
    private String organizerId;

    /**
     * Creates the fragment's view hierarchy.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes the fragment after view creation.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initViews();
        setupAdapter();
        setupListener();
    }

    /**
     * Initializes data repositories and user information.
     */
    private void initData() {
        eventRepository = EventRepository.getInstance();
        organizerId = UserManager.getCurrentUserId();
    }

    /**
     * Initializes view configurations.
     */
    private void initViews() {
        binding.lvOrganzierEventList.setDivider(null);
        binding.lvOrganzierEventList.setDividerHeight(18);

    }

    /**
     * Sets up the event list adapter and loads organizer's events.
     */
    private void setupAdapter() {
        binding.lvOrganzierEventList.setVisibility(View.GONE);

        eventRepository.getEventsByOrganizer(organizerId, new EventRepository.EventListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                int totalEvents = events.size();
                int totalEntrants = 0;
                int totalMaxAttendees = 0;
                for (Event event : events) {
                    totalEntrants += event.getConfirmedAttendees();
                    totalMaxAttendees += event.getMaxAttendees();
                }

                int fullRate = 0;
                if (totalMaxAttendees > 0) {
                    fullRate = (int) (((double) totalEntrants / totalMaxAttendees) * 100);
                }

                binding.tvTotalEvents.setText(String.valueOf(totalEvents));
                binding.tvTotalEntrants.setText(String.valueOf(totalEntrants));
                binding.tvFullRate.setText(fullRate + "%"    );

                binding.lvOrganzierEventList.setVisibility(View.VISIBLE);
                setupAdapterWithData(events);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("OrganizerHomeFragment", "Failed to load events", e);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(requireContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Configures adapter with event data and sets up click listeners.
     *
     * @param eventList the list of events to display
     */
    private void setupAdapterWithData(List<Event> eventList) {
        adapter = new OrganizerEventAdapter(requireContext(), eventList);
        binding.lvOrganzierEventList.setAdapter(adapter);

        // Set up draw button click listener
        adapter.setOnDrawButtonClickListener(event -> {
            OrganizerEventDrawFragment fragment = OrganizerEventDrawFragment.newInstance(event.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Set up export button click listener
        adapter.setOnExportButtonClickListener(event -> {
            // Export confirmed entrants to CSV
            CSVExportUtil.exportConfirmedEntrants(requireContext(), event, new CSVExportUtil.ExportCallback() {
                @Override
                public void onSuccess(android.net.Uri fileUri) {
                    // Success message is already shown by CSVExportUtil
                    Log.d("OrganizerHomeFragment", "CSV export successful for event: " + event.getTitle());
                }

                @Override
                public void onFailure(Exception e) {
                    // Error message is already shown by CSVExportUtil
                    Log.e("OrganizerHomeFragment", "CSV export failed for event: " + event.getTitle(), e);
                }
            });
        });

        // Set up event item click listener
        binding.lvOrganzierEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventList.get(position);
            OrganizerEventInfoFragment fragment = OrganizerEventInfoFragment.newInstance(selectedEvent.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * Sets up button click listeners for creating new events.
     */
    private void setupListener() {
        binding.btnCreateEvent.setOnClickListener(v -> {
            OrganizerEventCreateFragment fragment = new OrganizerEventCreateFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * Cleans up resources when view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
