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

public class OrganizerEventShowcaseFragment extends Fragment {
    private static final String ARG_EVENT_ID = "event_id";
    private FragmentOrganizerEventShowcaseBinding binding;
    private String eventId;
    private EventRepository eventRepository;
    private SimpleDateFormat dateFormat;

    public static OrganizerEventShowcaseFragment newInstance(String eventId) {
        OrganizerEventShowcaseFragment fragment = new OrganizerEventShowcaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerEventShowcaseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiveArguments();
        initData();
        setUpView();
        setUpListener();
        addEntrantsFragment();
    }


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

    private void initData() {
        eventRepository = EventRepository.getInstance();
        dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }

    private void setUpView() {
        eventRepository.findEventById(eventId, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                binding.tvEventTitle.setText(event.getTitle());
                binding.tvEventDescription.setText(event.getDescription());
                binding.tvEventCapacity.setText("Capacity:" + String.valueOf(event.getMaxAttendees()));
                binding.tvEventCurrentEntrantsCount.setText(event.getConfirmedCount() + " entrants");

                int fillRate = (event.getConfirmedCount() * 100) / event.getMaxAttendees();
                binding.tvEventFillRate.setText(String.valueOf(fillRate) + "%");

                binding.tvEventFillRatio.setText(event.getConfirmedCount() + "/" + event.getMaxAttendees());



                if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                    // TODO: use the image loading library to load images from web urls
                } else {
                    binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
                    binding.ivEventGallery.setImageResource(R.drawable.placeholder_background);
                }

                if (event.getStartTime() != null && event.getEndTime() != null) {
                    String startTime = dateFormat.format(event.getStartTime());
                    String endTime = dateFormat.format(event.getEndTime());
                    String formattedTime =startTime + "  -  " + endTime;

                    binding.tvStartToEnd.setText(formattedTime);
                } else {
                    binding.tvStartToEnd.setText("TBD");
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

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

    private void addEntrantsFragment() {
        EntrantsFragment entrantsFragment = EntrantsFragment.newInstance(eventId);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_entrants_conatiner, entrantsFragment)
                .commit();
    }
}