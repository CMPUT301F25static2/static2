package com.ualberta.eventlottery.ui.organizer.organizerEventQrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerEventQrcodeBinding;

/**
 * Fragment for displaying event QR code and event details.
 *
 * @author static2
 * @version 1.0
 */
public class OrganizerEventQrcodeFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private FragmentOrganizerEventQrcodeBinding binding;
    private String eventId;
    private EventRepository eventRepository = EventRepository.getInstance();

    /**
     * Creates a new instance with the specified event ID.
     *
     * @param eventId the ID of the event to display QR code for
     * @return new OrganizerEventQrcodeFragment instance
     */
    public static OrganizerEventQrcodeFragment newInstance(String eventId) {
        OrganizerEventQrcodeFragment fragment = new OrganizerEventQrcodeFragment();
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
        binding = FragmentOrganizerEventQrcodeBinding.inflate(inflater, container, false);
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
        setUpView();
        setUpListener();
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
     * Sets up the view with event data and QR code.
     */
    private void setUpView() {
        eventRepository.findEventById(eventId, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                // Set event title
                binding.tvEventTitle.setText(event.getTitle());

                // Set event poster image
                if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                    Glide.with(requireContext())
                            .load(event.getPosterUrl())
                            .placeholder(R.drawable.placeholder_background)
                            .error(R.drawable.placeholder_background)
                            .into(binding.ivEventPosterImg);
                } else {
                    binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
                }

                // Set QR code image
                if (event.getQrCodeUrl() != null && !event.getQrCodeUrl().isEmpty()) {
                    Glide.with(requireContext())
                            .load(event.getQrCodeUrl())
                            .placeholder(R.drawable.qrcode)
                            .error(R.drawable.qrcode)
                            .into(binding.ivEventQrcode);
                } else {
                    binding.ivEventQrcode.setImageResource(R.drawable.qrcode); // Default QR image if URL is missing
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    /**
     * Sets up click listeners for navigation.
     */
    private void setUpListener() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
}