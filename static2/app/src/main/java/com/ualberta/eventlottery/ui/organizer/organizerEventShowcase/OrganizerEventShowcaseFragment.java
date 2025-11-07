package com.ualberta.eventlottery.ui.organizer.organizerEventShowcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.fragment.EntrantsFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventQrcode.OrganizerEventQrcodeFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerEventShowcaseBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment for showcasing event details and statistics to organizers.
 *
 * @author static2
 * @version 1.0
 */
public class OrganizerEventShowcaseFragment extends Fragment {
    private static final String ARG_EVENT_ID = "event_id";
    private FragmentOrganizerEventShowcaseBinding binding;
    private String eventId;
    private EventRepository eventRepository;
    private SimpleDateFormat dateFormat;

    /**
     * Creates a new instance with the specified event ID.
     *
     * @param eventId the ID of the event to showcase
     * @return new OrganizerEventShowcaseFragment instance
     */
    public static OrganizerEventShowcaseFragment newInstance(String eventId) {
        OrganizerEventShowcaseFragment fragment = new OrganizerEventShowcaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates the fragment's view hierarchy.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerEventShowcaseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Cleans up resources when view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Initializes the fragment after view creation.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiveArguments();
        initData();
        setUpView();
        setUpListener();
        addEntrantsFragment();
    }

    /**
     * Retrieves event ID from fragment arguments.
     */
    private void receiveArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_EVENT_ID)) {
            eventId = args.getString(ARG_EVENT_ID);
            Toast.makeText(requireContext(), "Received Event ID: " + eventId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No event ID received", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
    }

    /**
     * Initializes data repositories and formatters.
     */
    private void initData() {
        eventRepository = EventRepository.getInstance();
        dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }

    /**
     * Sets up the view with event data and statistics.
     */
    private void setUpView() {
        eventRepository.findEventById(eventId, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                // Set basic event information
                binding.tvEventTitle.setText(event.getTitle());
                binding.tvEventDescription.setText(event.getDescription());
                binding.tvEventCapacity.setText("Capacity: " + event.getMaxAttendees());
                binding.tvEventCurrentEntrantsCount.setText(event.getConfirmedCount() + " entrants");

                // Calculate and display fill rate
                int fillRate = (event.getConfirmedCount() * 100) / event.getMaxAttendees();
                binding.tvEventFillRate.setText(fillRate + "%");
                binding.tvEventFillRatio.setText(event.getConfirmedCount() + "/" + event.getMaxAttendees());

                // Set event images
                if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                    // TODO: use the image loading library to load images from web urls
                } else {
                    binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
                    binding.ivEventGallery.setImageResource(R.drawable.placeholder_background);
                }

                // Set event time range
                if (event.getStartTime() != null && event.getEndTime() != null) {
                    String startTime = dateFormat.format(event.getStartTime());
                    String endTime = dateFormat.format(event.getEndTime());
                    String formattedTime = startTime + "  -  " + endTime;
                    binding.tvStartToEnd.setText(formattedTime);
                } else {
                    binding.tvStartToEnd.setText("TBD");
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Error handling is optional here as the view will remain in loading state
            }
        });
    }

    /**
     * Sets up click listeners for navigation and actions.
     */
    private void setUpListener() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.btnEventShowQrcode.setOnClickListener(v -> {
            OrganizerEventQrcodeFragment fragment = OrganizerEventQrcodeFragment.newInstance(eventId);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * Adds the entrants fragment to display registered participants.
     */
    private void addEntrantsFragment() {
        EntrantsFragment entrantsFragment = EntrantsFragment.newInstance(eventId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_entrants_conatiner, entrantsFragment)
                .commit();
    }
}