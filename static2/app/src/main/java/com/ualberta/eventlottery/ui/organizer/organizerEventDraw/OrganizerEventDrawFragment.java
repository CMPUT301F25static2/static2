package com.ualberta.eventlottery.ui.organizer.organizerEventDraw;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.static2.databinding.FragmentOrganizerDrawBinding;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class OrganizerEventDrawFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private FragmentOrganizerDrawBinding binding;
    private String eventId;
    private Event currentEvent;
    private EventRepository eventRepo;
    private RegistrationRepository registrationRepo;
    private SimpleDateFormat dateFormat;



    public static OrganizerEventDrawFragment newInstance(String eventId) {
        OrganizerEventDrawFragment fragment = new OrganizerEventDrawFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerDrawBinding.inflate(inflater, container, false);
        return binding.getRoot();
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
        registrationRepo = RegistrationRepository.getInstance();
        dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }

    private void setUpView() {
        currentEvent = eventRepo.findEventById(eventId);
        if (currentEvent == null) {
            Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        binding.tvEventTitle.setText(currentEvent.getTitle());
        binding.tvEventCapacity.setText("Capacity: " + currentEvent.getMaxAttendees());


        if (currentEvent.getStartTime() != null && currentEvent.getEndTime() != null ) {
            String formattedStart = dateFormat.format(currentEvent.getEndTime());
            String formattedEnd = dateFormat.format(currentEvent.getStartTime());

            binding.tvRegistrationTime.setText("Time: " + formattedStart + " - " + formattedEnd);

        } else {
            binding.tvRegistrationTime.setText("Time: TBD");
        }

        List<Registration> registered= registrationRepo.getRegistrationsByEventAndStatus(eventId, EntrantRegistrationStatus.REGISTERED);
        binding.tvEventWaitingNumber.setText(String.valueOf(registered.size()));

        List<Registration> confirmed = registrationRepo.getRegistrationsByEventAndStatus(eventId, EntrantRegistrationStatus.CONFIRMED);
        String ratio = String.valueOf(confirmed.size()) + "/" + String.valueOf(currentEvent.getMaxAttendees());
        binding.tvEventAcceptedRatio.setText(ratio);

        int availableCount = currentEvent.getMaxAttendees() - confirmed.size();
        binding.tvEventSpotsLeft.setText(String.valueOf(availableCount));

        binding.tvMaxEntrantsMsg.setText("Maximum " + availableCount + " entrants spots available");

    }

    private void setUpListener() {
        // return btn
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // draw btn
        binding.btnDraw.setOnClickListener(v -> {
            performLotteryDraw();
        });

        binding.etNumberToDraw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateInputNumber();
            }
        });
    }

    private void validateInputNumber() {
        String input = binding.etNumberToDraw.getText().toString().trim();

        if (input.isEmpty()) {
            return;
        }

        try {
            int numberToDraw = Integer.parseInt(input);
            int availableSpots = currentEvent.getMaxAttendees() - registrationRepo.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.CONFIRMED);
            int waitingCount = registrationRepo.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.WAITING);

            // check if exceed available spots
            if (numberToDraw > availableSpots) {
                binding.etNumberToDraw.setError("Cannot draw more than " + availableSpots + " entrants");
                binding.btnDraw.setEnabled(false);
                binding.btnDraw.setAlpha(0.5f);
            }
            // check if exceed waiting list
            else if (numberToDraw > waitingCount) {
                binding.etNumberToDraw.setError("Only " + waitingCount + " entrants in waiting list");
                binding.btnDraw.setEnabled(false);
                binding.btnDraw.setAlpha(0.5f);
            }

            // check if draw less than 1
            else if (numberToDraw <= 0) {
                binding.etNumberToDraw.setError("Must draw at least 1 entrant");
                binding.btnDraw.setEnabled(false);
                binding.btnDraw.setAlpha(0.5f);
            }

            // valid input
            else {
                binding.etNumberToDraw.setError(null);
                binding.btnDraw.setEnabled(true);
                binding.btnDraw.setAlpha(1.0f);
            }
        } catch (NumberFormatException e) {
            binding.etNumberToDraw.setError("Please enter a valid number");
            binding.btnDraw.setEnabled(false);
            binding.btnDraw.setAlpha(0.5f);
        }
    }


    private void performLotteryDraw() {
        String input = binding.etNumberToDraw.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter number of entrants to draw", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int numberToDraw = Integer.parseInt(input);
            int availableSpots = currentEvent.getMaxAttendees() - registrationRepo.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.CONFIRMED);
            int waitingCount = registrationRepo.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.WAITING);

            // check if exceed available spots
            if (numberToDraw > availableSpots) {
                Toast.makeText(requireContext(), "Cannot draw more than available spots: " + availableSpots, Toast.LENGTH_LONG).show();
                return;
            }

            if (numberToDraw > waitingCount) {
                Toast.makeText(requireContext(), "Not enough entrants in waiting list", Toast.LENGTH_LONG).show();
                return;
            }

            if (numberToDraw <= 0) {
                Toast.makeText(requireContext(), "Must draw at least 1 entrant", Toast.LENGTH_SHORT).show();
                return;
            }

            // draw
            executeRandomDraw(numberToDraw);

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void executeRandomDraw(int numberToDraw) {
        List<Registration> waitingRegistrations = registrationRepo.getRegistrationsByStatus(eventId, EntrantRegistrationStatus.WAITING);

        if (waitingRegistrations.isEmpty()) {
            Toast.makeText(requireContext(), "No entrants in waiting list", Toast.LENGTH_SHORT).show();
            return;
        }

        Collections.shuffle(waitingRegistrations);

        int actualDrawCount = Math.min(numberToDraw, waitingRegistrations.size());
        List<Registration> selectedRegistrations = waitingRegistrations.subList(0, actualDrawCount);

        // update each selected registration's status
        for (Registration registration : selectedRegistrations) {
            registration.setStatus(EntrantRegistrationStatus.SELECTED);
            registrationRepo.updateRegistration(registration);

            // TODO: send notifictaions to all selected entrants
        }

        // show result
        showDrawResult(actualDrawCount, selectedRegistrations);

        refreshUI();
    }

    private void showDrawResult(int actualDrawCount, List<Registration> selectedRegistrations) {
        String message = "Successfully selected " + actualDrawCount + " entrants!\n";
        message += "They have been notified to confirm their attendance.";

        new AlertDialog.Builder(requireContext())
                .setTitle("Lottery Draw Complete")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();



    }

    private void refreshUI() {
        // update waiting list
        List<Registration> waiting = registrationRepo.getRegistrationsByStatus(eventId, EntrantRegistrationStatus.WAITING);
        binding.tvEventWaitingNumber.setText(String.valueOf(waiting.size()));

        // update confirmed list and available spots
        List<Registration> confirmed = registrationRepo.getRegistrationsByStatus(eventId, EntrantRegistrationStatus.CONFIRMED);
        String ratio = confirmed.size() + "/" + currentEvent.getMaxAttendees();
        binding.tvEventAcceptedRatio.setText(ratio);

        int availableSpots = currentEvent.getMaxAttendees() - confirmed.size();
        binding.tvEventSpotsLeft.setText(String.valueOf(availableSpots));

        binding.tvMaxEntrantsMsg.setText("Maximum " + availableSpots + " entrants spots available");

        // clear input
        binding.etNumberToDraw.setText("");
        binding.btnDraw.setEnabled(false);
        binding.btnDraw.setAlpha(0.5f);
    }



}
