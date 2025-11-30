package com.ualberta.eventlottery.ui.home.entrant;import android.Manifest;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.ualberta.eventlottery.model.Entrant;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.notification.NotificationController;
import com.ualberta.eventlottery.repository.EntrantRepository;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.service.LocationService;
import com.ualberta.eventlottery.ui.notifications.NotificationTemplate;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentEventDetailsBinding;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass for entrants to view event details.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private FragmentEventDetailsBinding binding;

    private String mEventId;
    private EventRepository eventRepository;
    private RegistrationRepository registrationRepository;
    private LocationService locationService;

    private Registration currentUserRegistration = null;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private Event currentEvent;
    private enum ActionType {
        REGISTER,
        WITHDRAW,
        ACCEPT,
        CANCEL
    }

    private ActionType currentAction = ActionType.REGISTER;
    private static NotificationController notificationController;
    private EntrantRepository entrantRepository;

    /**
     * Required empty public constructor
     */
    public EventDetailsFragment() {

    }

    /**
     * Creates a new instance of EventDetailsFragment.
     * @param eventId The ID of the event to display.
     * @return A new instance of fragment EventDetailsFragment.
     */
    public EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveArguments();
        initData();
        setupLocationPermissionLauncher();
        notificationController = new NotificationController(requireContext());

    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view
     *                           itself, but this can be used to generate the LayoutParams of
     *                           the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();

        if (mEventId == null) {
            Toast.makeText(getContext(), "Error: Event ID not found", Toast.LENGTH_LONG).show();
            binding.eventDetailsTitle.setText("Event Not Found");
            binding.registerButton.setEnabled(false);
            return;
        }

        loadEventData();
    }

    /**
     * Retrieves event ID from fragment arguments.
     */
    private void receiveArguments() {
        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    /**
     * Initializes data repositories and other services.
     */
    private void initData() {
        eventRepository = EventRepository.getInstance();
        registrationRepository = RegistrationRepository.getInstance();
        locationService = new LocationService(getContext());
        entrantRepository = EntrantRepository.getInstance();
    }

    /**
     * Sets up the launcher for requesting location permissions.
     */
    private void setupLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    boolean coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted || coarseLocationGranted) {
                        captureLocationAndRegister();
                    } else {
                        Toast.makeText(getContext(), "Location permission is required for this event", Toast.LENGTH_LONG).show();
                        resetButtonState();
                    }
                }
        );
    }

    /**
     * Sets up click listeners for UI elements.
     */
    private void setupListeners() {
        binding.backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(EventDetailsFragment.this).popBackStack()
        );
        binding.registerButton.setOnClickListener(v -> handleActionButtonPress());
        binding.acceptButton.setOnClickListener(v -> acceptInvitation());
        binding.rejectButton.setOnClickListener(v -> withdrawFromEvent());

    }


    /**
     * Loads event data from the repository and populates the UI.
     */
    private void loadEventData() {
        eventRepository.findEventById(mEventId, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                if (event != null && isAdded()) {
                    currentEvent = event;
                    populateUi();
                    checkUserRegistrationStatus();
                } else if (isAdded()) {
                    handleLoadFailure(new Exception("Event data could not be loaded."));
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    handleLoadFailure(e);
                }
            }
        });
    }

    private void handleLoadFailure(Exception e) {
        binding.eventDetailsTitle.setText("Event data could not be loaded.");
        binding.registerButton.setEnabled(false);
        Log.e("EventDetailsFragment", "Error loading event data", e);
    }


    /**
     * Checks the registration status of the current user for the event.
     */
    private void checkUserRegistrationStatus() {
        String currentUserId = UserManager.getCurrentUserId();
        if (currentUserId == null || mEventId == null) {
            binding.registerButton.setVisibility(View.GONE);
            return;
        }

        binding.registerButton.setEnabled(false);
        registrationRepository.findRegistrationByEventAndUser(mEventId, currentUserId, new RegistrationRepository.RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                currentUserRegistration = registration;
                updateButtonUi();
                binding.registerButton.setEnabled(true);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("CheckRegistration", "Failed to check registration status", e);
                binding.registerButton.setEnabled(true); // Allow retry
            }
        });
    }

    /**
     * Updates the UI of the register/withdraw button based on the user's registration status.
     */
    /**
     * Updates UI button visibility according to registration status
     */

    private void updateButtonUi() {
        if (getContext() == null) return;

        // Hide both layouts first
        binding.registerButton.setVisibility(View.GONE);
        binding.selectedButtonsLayout.setVisibility(View.GONE);

        if (currentUserRegistration == null) {
            binding.registerButton.setText("Register");
            binding.registerButton.setVisibility(View.VISIBLE);
            currentAction = ActionType.REGISTER;
            return;
        }

        String status = currentUserRegistration.getStatus().name();
        Log.d("Status", status);

        switch (status) {
            case "WAITING":
                binding.registerButton.setVisibility(View.VISIBLE);
                binding.registerButton.setText("Withdraw");
                currentAction = ActionType.WITHDRAW;
                break;

            case "SELECTED":
                binding.selectedButtonsLayout.setVisibility(View.VISIBLE);
                currentAction = ActionType.ACCEPT;
                break;

            case "CONFIRMED":
                binding.registerButton.setVisibility(View.VISIBLE);
                binding.registerButton.setText("Cancel Spot");
                currentAction = ActionType.CANCEL;
                break;

            default:
                binding.registerButton.setVisibility(View.VISIBLE);
                binding.registerButton.setText("Register");
                currentAction = ActionType.REGISTER;
                break;
        }
    }

    private void handleActionButtonPress() {
        switch (currentAction) {
            case REGISTER:
                registerForEvent();
                break;
            case WITHDRAW:
                withdrawFromEvent();
                break;
            case ACCEPT:
                acceptInvitation();
                break;
            case CANCEL:
                cancelInvitation();
                break;
        }
    }

    /**
     * Registers the current user for the event.
     */
    private void registerForEvent() {
        String currentUserId = UserManager.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(getContext(), "You must be signed in to register", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEventId == null || currentEvent == null) return;

        binding.registerButton.setEnabled(false);
        binding.registerButton.setText("Registering...");

        // Check if geolocation is required for this event
        if (currentEvent.isLocationRequired()) {
            handleGeolocationRegistration();
        } else {
            proceedWithRegistration(null);
        }
    }

    private void handleGeolocationRegistration() {
        if (!locationService.hasLocationPermissions()) {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            captureLocationAndRegister();
        }
    }


    private void captureLocationAndRegister() {
        locationService.getCurrentLocation()
                .thenAccept(location -> {
                    if (location != null) {
                        proceedWithRegistration(location);
                    } else {
                        Toast.makeText(getContext(), "Unable to get location. Please try again.", Toast.LENGTH_LONG).show();
                        resetButtonState();
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(getContext(), "Location capture failed: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    resetButtonState();
                    return null;
                });
    }

    private void proceedWithRegistration(@Nullable Location location) {
        String currentUserId = UserManager.getCurrentUserId();
        if (currentUserId == null) return;

        registrationRepository.registerUser(mEventId, currentUserId, location, new RegistrationRepository.RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                Toast.makeText(getContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                currentUserRegistration = registration;
                updateButtonUi();
                binding.registerButton.setEnabled(true);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                currentUserRegistration = null;
                updateButtonUi();
                binding.registerButton.setEnabled(true);
            }
        });
    }

    private void resetButtonState() {
        binding.registerButton.setEnabled(true);
        updateButtonUi();
    }

    /**
     * Withdraws the current user from the event.
     */
    private void withdrawFromEvent() {
        if (currentUserRegistration == null || currentUserRegistration.getId() == null) {
            Toast.makeText(getContext(), "Cannot withdraw: Registration not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.registerButton.setEnabled(false);
        binding.registerButton.setText("Withdrawing...");

        registrationRepository.deleteRegistration(currentUserRegistration.getId(), new RegistrationRepository.BooleanCallback() {
            @Override
            public void onSuccess(boolean result) {
                Toast.makeText(getContext(), "Successfully withdrawn from event", Toast.LENGTH_SHORT).show();
                currentUserRegistration = null;
                updateButtonUi();
                binding.registerButton.setEnabled(true);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Withdrawal failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("WithdrawFailure", "Error withdrawing from event", e);
                updateButtonUi();
                binding.registerButton.setEnabled(true);
            }
        });
    }
    /**
     * Accepts spot in the event.
     */
    private void acceptInvitation() {
        if (currentUserRegistration == null || currentUserRegistration.getId() == null) {
            Toast.makeText(getContext(), "Cannot accept spot: Registration not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.registerButton.setEnabled(false);
        binding.registerButton.setText("Accepting...");

        registrationRepository.acceptInvitation(currentUserRegistration.getId(), new RegistrationRepository.RegistrationCallback() {
            @Override
            public void onSuccess(Registration updatedRegistration) {
                currentUserRegistration = updatedRegistration;
                updateButtonUi();
                binding.registerButton.setEnabled(true);

                if (currentEvent != null) {
                    String entrantId = currentUserRegistration.getEntrantId();
                    String organizerId = currentEvent.getOrganizerId();

                    // Minimal fix: fetch entrant name
                    EntrantRepository.getInstance().findEntrantById(entrantId, new EntrantRepository.EntrantCallback() {
                        @Override
                        public void onSuccess(Entrant entrant) {
                            String entrantName = entrant.getName();
                            String title = NotificationTemplate.getAcceptedOrganizer(entrantName, currentEvent.getTitle());
                            notificationController.sendNotification(title, "", currentEvent.getId(), List.of(organizerId),"general");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("AcceptInvitation", "Failed to fetch entrant name", e);
                            String title = NotificationTemplate.getAcceptedOrganizer(entrantId, currentEvent.getTitle());
                            notificationController.sendNotification(title, "", currentEvent.getId(), List.of(organizerId),"general");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to accept: " + e.getMessage(), Toast.LENGTH_LONG).show();
                updateButtonUi();
                binding.registerButton.setEnabled(true);
            }
        });
    }
    /**
     * Cancels spot in the event.
     */
    private void cancelInvitation() {
        if (currentUserRegistration == null || currentUserRegistration.getId() == null) {
            Toast.makeText(getContext(), "Cannot cancel: Registration not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.registerButton.setEnabled(false);
        binding.registerButton.setText("Cancelling...");

        registrationRepository.deleteRegistration(currentUserRegistration.getId(), new RegistrationRepository.BooleanCallback() {
            @Override
            public void onSuccess(boolean result) {
                Toast.makeText(getContext(), "Successfully cancelled spot in event", Toast.LENGTH_SHORT).show();
                // Capture entrantId before clearing registration
                String entrantId = currentUserRegistration.getEntrantId();
                currentUserRegistration = null;
                updateButtonUi();
                binding.registerButton.setEnabled(true);

                if (currentEvent != null) {
                    String organizerId = currentEvent.getOrganizerId();

                    // Minimal fix: fetch entrant name
                    EntrantRepository.getInstance().findEntrantById(entrantId, new EntrantRepository.EntrantCallback() {
                        @Override
                        public void onSuccess(Entrant entrant) {
                            String entrantName = entrant.getName();
                            String title = NotificationTemplate.getNotAcceptedOrganizer(entrantName, currentEvent.getTitle());
                            notificationController.sendNotification(title, "", currentEvent.getId(), List.of(organizerId),"general");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("CancelInvitation", "Failed to fetch entrant name", e);
                            String title = NotificationTemplate.getNotAcceptedOrganizer(entrantId, currentEvent.getTitle());
                            notificationController.sendNotification(title, "", currentEvent.getId(), List.of(organizerId),"general");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Cancellation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("WithdrawFailure", "Error cancelling spot in event", e);
                updateButtonUi();
                binding.registerButton.setEnabled(true);
            }
        });
    }




    /**
     * Populates the UI with important event details for an entrant.
     */
    private void populateUi() {
        if (currentEvent == null || getContext() == null) return;

        // --- Basic Info ---
        binding.eventDetailsTitle.setText(currentEvent.getTitle());
        binding.eventDetailsDescription.setText(currentEvent.getDescription());

        // --- Date and Time ---
        SimpleDateFormat dateSdf = new SimpleDateFormat("MMM dd, yyyy", Locale.CANADA);
        String fromToText = "Dates TBD";
        if (currentEvent.getStartTime() != null && currentEvent.getEndTime() != null) {
            fromToText = dateSdf.format(currentEvent.getStartTime()) + " - " + dateSdf.format(currentEvent.getEndTime());
        }
        binding.eventDetailsDate.setText(fromToText);

        String timeText = "Time TBD";
        if (currentEvent.getDailyStartTime() != null) {
            timeText = "Starts at " + currentEvent.getDailyStartTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
        }
        binding.eventDetailsTime.setText(timeText);

        // --- Location ---
        String location = currentEvent.getLocation();
        binding.eventDetailsLocation.setText(location != null && !location.isEmpty() ? location : "Location TBD");

        // --- Poster Image ---
        if (currentEvent.getPosterUrl() != null && !currentEvent.getPosterUrl().isEmpty()) {
            binding.ivEventPosterImg.setVisibility(View.VISIBLE); // Make the ImageView visible
            Glide.with(getContext())
                    .load(Uri.parse(currentEvent.getPosterUrl()))
                    .placeholder(R.drawable.placeholder_background)
                    .error(R.drawable.placeholder_background)
                    .into(binding.ivEventPosterImg);
        } else {
            binding.ivEventPosterImg.setVisibility(View.GONE); // Hide the ImageView if there is no poster
        }
    }


    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
