package com.ualberta.eventlottery.ui.organizer.organizerEventDraw;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.notification.NotificationController;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.static2.databinding.FragmentOrganizerDrawBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for conducting lottery draws for event registrations.
 * Allows organizers to randomly select entrants from waiting list based on available spots.
 *
 * @author static2
 * @version 1.0
 */
public class OrganizerEventDrawFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private FragmentOrganizerDrawBinding binding;
    private String eventId;
    private Event currentEvent;

    private EventRepository eventRepository;
    private RegistrationRepository registrationRepository;
    private NotificationController notificationController;
    private SimpleDateFormat dateFormat;

    /**
     * Creates a new instance with the specified event ID.
     *
     * @param eventId the ID of the event to conduct draw for
     * @return new OrganizerEventDrawFragment instance
     */
    public static OrganizerEventDrawFragment newInstance(String eventId) {
        OrganizerEventDrawFragment fragment = new OrganizerEventDrawFragment();
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
        binding = FragmentOrganizerDrawBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes the fragment after view creation.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiveArguments();
        initData();
        loadEventData();
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
        registrationRepository = RegistrationRepository.getInstance();
        notificationController = new NotificationController(requireContext());
        dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }

    /**
     * Loads event data from repository.
     */
    private void loadEventData() {
        eventRepository.findEventById(eventId, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                currentEvent = event;
                setUpView();
                setUpListener();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    /**
     * Sets up the view with event data and registration counts.
     */
    private void setUpView() {
        if (currentEvent == null) {
            Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        binding.tvEventTitle.setText(currentEvent.getTitle());
        binding.tvEventCapacity.setText("Capacity: " + currentEvent.getMaxAttendees());

        // Set registration time range
        if (currentEvent.getStartTime() != null && currentEvent.getEndTime() != null) {
            String formattedStart = dateFormat.format(currentEvent.getEndTime());
            String formattedEnd = dateFormat.format(currentEvent.getStartTime());
            binding.tvRegistrationTime.setText(formattedStart + "  -  " + formattedEnd);
        } else {
            binding.tvRegistrationTime.setText("Registration Time: TBD");
        }

        loadRegistrationCounts();
    }

    /**
     * Loads and displays registration counts for different statuses.
     */
    private void loadRegistrationCounts() {
        registrationRepository.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.CONFIRMED, new RegistrationRepository.CountCallback() {
            @Override
            public void onSuccess(int confirmedCount) {
                registrationRepository.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.WAITING, new RegistrationRepository.CountCallback() {
                    @Override
                    public void onSuccess(int waitingCount) {
                        binding.tvEventWaitingNumber.setText(String.valueOf(waitingCount));

                        String ratio = confirmedCount + "/" + currentEvent.getMaxAttendees();
                        binding.tvEventAcceptedRatio.setText(ratio);

                        int availableCount = currentEvent.getMaxAttendees() - confirmedCount;
                        binding.tvEventSpotsLeft.setText(String.valueOf(availableCount));

                        binding.tvMaxEntrantsMsg.setText("Maximum " + Math.min(availableCount, waitingCount) + " entrants to draw");

                        // Clear input and disable draw button initially
                        binding.etNumberToDraw.setText("");
                        binding.btnDraw.setEnabled(false);
                        binding.btnDraw.setAlpha(0.5f);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), "Failed to load waiting count", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load confirmed count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets up click listeners and input validation.
     */
    private void setUpListener() {
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Draw button
        binding.btnDraw.setOnClickListener(v -> {
            performLotteryDraw();
        });

        // Input validation
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

    /**
     * Validates the input number for lottery draw.
     * Checks against available spots and waiting list size.
     */
    private void validateInputNumber() {
        String input = binding.etNumberToDraw.getText().toString().trim();

        if (input.isEmpty()) {
            return;
        }

        try {
            int numberToDraw = Integer.parseInt(input);

            registrationRepository.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.CONFIRMED, new RegistrationRepository.CountCallback() {
                @Override
                public void onSuccess(int confirmedCount) {
                    registrationRepository.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.WAITING, new RegistrationRepository.CountCallback() {
                        @Override
                        public void onSuccess(int waitingCount) {
                            int availableSpots = currentEvent.getMaxAttendees() - confirmedCount;

                            // Validate against available spots
                            if (numberToDraw > availableSpots) {
                                binding.etNumberToDraw.setError("Cannot draw more than " + availableSpots + " entrants");
                                binding.btnDraw.setEnabled(false);
                                binding.btnDraw.setAlpha(0.5f);
                            }
                            // Validate against waiting list size
                            else if (numberToDraw > waitingCount) {
                                binding.etNumberToDraw.setError("Only " + waitingCount + " entrants in waiting list");
                                binding.btnDraw.setEnabled(false);
                                binding.btnDraw.setAlpha(0.5f);
                            }
                            // Validate minimum draw count
                            else if (numberToDraw <= 0) {
                                binding.etNumberToDraw.setError("Must draw at least 1 entrant");
                                binding.btnDraw.setEnabled(false);
                                binding.btnDraw.setAlpha(0.5f);
                            }
                            // Valid input
                            else {
                                binding.etNumberToDraw.setError(null);
                                binding.btnDraw.setEnabled(true);
                                binding.btnDraw.setAlpha(1.0f);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            binding.etNumberToDraw.setError("Error loading waiting list");
                            binding.btnDraw.setEnabled(false);
                            binding.btnDraw.setAlpha(0.5f);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    binding.etNumberToDraw.setError("Error loading event data");
                    binding.btnDraw.setEnabled(false);
                    binding.btnDraw.setAlpha(0.5f);
                }
            });

        } catch (NumberFormatException e) {
            binding.etNumberToDraw.setError("Please enter a valid number");
            binding.btnDraw.setEnabled(false);
            binding.btnDraw.setAlpha(0.5f);
        }
    }

    /**
     * Performs the lottery draw with validated input.
     */
    private void performLotteryDraw() {
        String input = binding.etNumberToDraw.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter number of entrants to draw", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int numberToDraw = Integer.parseInt(input);

            // Validate with current counts
            registrationRepository.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.CONFIRMED, new RegistrationRepository.CountCallback() {
                @Override
                public void onSuccess(int confirmedCount) {
                    registrationRepository.getRegistrationCountByStatus(eventId, EntrantRegistrationStatus.WAITING, new RegistrationRepository.CountCallback() {
                        @Override
                        public void onSuccess(int waitingCount) {
                            int availableSpots = currentEvent.getMaxAttendees() - confirmedCount;

                            // Check available spots
                            if (numberToDraw > availableSpots) {
                                Toast.makeText(requireContext(), "Cannot draw more than available spots: " + availableSpots, Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Check waiting list size
                            if (numberToDraw > waitingCount) {
                                Toast.makeText(requireContext(), "Not enough entrants in waiting list", Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Check minimum draw count
                            if (numberToDraw <= 0) {
                                Toast.makeText(requireContext(), "Must draw at least 1 entrant", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Execute the draw
                            executeRandomDraw(numberToDraw);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(requireContext(), "Failed to load waiting list", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(requireContext(), "Failed to load event data", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Executes random selection from waiting list.
     */
    private void executeRandomDraw(int numberToDraw) {
        registrationRepository.getWaitingRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> waitingRegistrations) {
                if (waitingRegistrations.isEmpty()) {
                    Toast.makeText(requireContext(), "No entrants in waiting list", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Shuffle and select random entrants
                Collections.shuffle(waitingRegistrations);
                int actualDrawCount = Math.min(numberToDraw, waitingRegistrations.size());
                List<Registration> selectedRegistrations = waitingRegistrations.subList(0, actualDrawCount);

                // Update selected registrations status
                updateSelectedRegistrations(selectedRegistrations, actualDrawCount);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load waiting list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates selected registrations to SELECTED status and sends notifications.
     */
    private void updateSelectedRegistrations(List<Registration> selectedRegistrations, int actualDrawCount) {
        // Prepare user IDs for notifications
        List<String> selectedUserIds = new ArrayList<>();
        for (Registration registration : selectedRegistrations) {
            selectedUserIds.add(registration.getEntrantId());
        }

        // Update each registration status
        for (Registration registration : selectedRegistrations) {
            registration.setStatus(EntrantRegistrationStatus.SELECTED);
            registrationRepository.updateRegistration(registration, new RegistrationRepository.BooleanCallback() {
                @Override
                public void onSuccess(boolean result) {
                    showDrawResult(actualDrawCount, selectedRegistrations);
                    loadEventData(); // Refresh the view

                    // Send notifications to all selected entrants
                    sendNotificationsToSelectedUsers(selectedUserIds);
                }

                @Override
                public void onFailure(Exception e) {
                    // Individual update failures are handled silently
                }
            });
        }
    }

    /**
     * Sends notifications to users who were selected in the lottery draw.
     */
    private void sendNotificationsToSelectedUsers(List<String> selectedUserIds) {
        if (currentEvent != null && !selectedUserIds.isEmpty()) {
            String notificationTitle = "Congratulations! You've been selected!";
            String notificationBody = "You have been selected from the waiting list for the event: " +
                    currentEvent.getTitle() + ". Please confirm your attendance within 48 hours.";

            notificationController.sendNotification(notificationTitle, notificationBody, currentEvent.getId(), selectedUserIds);
        }
    }

    /**
     * Shows result dialog after successful draw.
     */
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
}