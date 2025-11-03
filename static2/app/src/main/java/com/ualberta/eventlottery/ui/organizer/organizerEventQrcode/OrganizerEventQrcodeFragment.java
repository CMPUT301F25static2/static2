package com.ualberta.eventlottery.ui.organizer.organizerEventQrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.organizerEventShowcase.OrganizerEventShowcaseFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerEventQrcodeBinding;
import com.ualberta.static2.databinding.FragmentOrganizerEventShowcaseBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrganizerEventQrcodeFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private FragmentOrganizerEventQrcodeBinding binding;
    private String eventId;
    private Event currentEvent;
    private EventRepository eventRepo;

    public static OrganizerEventQrcodeFragment newInstance(String eventId) {
        OrganizerEventQrcodeFragment fragment = new OrganizerEventQrcodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerEventQrcodeBinding.inflate(inflater, container, false);
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
        eventRepo = EventRepository.getInstance();
    }

    private void setUpView() {
        currentEvent = eventRepo.findEventById(eventId);

        binding.tvEventTitle.setText(currentEvent.getTitle());

        if (currentEvent.getPosterUrl() != null && !currentEvent.getPosterUrl().isEmpty()) {
            // TODO: use the image loading library to load images from web urls
        } else {
            binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
        }

        if (currentEvent.getQrCodeUrl() != null && !currentEvent.getQrCodeUrl().isEmpty()) {
            // TODO: use the image loading library to load images from web urls
        } else {
            binding.ivEventQrcode.setImageResource(R.drawable.qrcode);
        }
    }

    private void setUpListener() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }


}
