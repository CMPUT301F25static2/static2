package com.ualberta.eventlottery.ui.organizer.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ualberta.eventlottery.model.Entrant;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.notification.NotificationController;
import com.ualberta.eventlottery.repository.EntrantRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.ui.organizer.adapter.EntrantAdapter;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganzerEntrantListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying and managing event entrants by registration status.
 * Shows filtered lists of confirmed, waiting, selected, and cancelled entrants.
 *
 * @author static2
 * @version 1.0
 */
public class EntrantsFragment extends Fragment {

    private FragmentOrganzerEntrantListBinding binding;
    private List<LinearLayout> statusButtons = new ArrayList<>();
    private RegistrationRepository registrationRepository = RegistrationRepository.getInstance();
    private EntrantRepository entrantRepository = EntrantRepository.getInstance();
    private NotificationController notificationController;

    private static final String ARG_EVENT_ID = "event_id";
    private String eventId;

    /**
     * Creates a new instance with the specified event ID.
     *
     * @param eventId the ID of the event to display entrants for
     * @return new EntrantsFragment instance
     */
    public static EntrantsFragment newInstance(String eventId) {
        EntrantsFragment fragment = new EntrantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public EntrantsFragment() {
        // Required empty constructor
    }

    /**
     * Creates the fragment's view hierarchy.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganzerEntrantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes the fragment after view creation.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiveArguments();
        initViews();
        setupClickListeners();
        loadEntrantsData();

        // Initialize notification controller
        notificationController = new NotificationController(requireContext());
    }

    /**
     * Retrieves event ID from fragment arguments.
     */
    private void receiveArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_EVENT_ID)) {
            eventId = args.getString(ARG_EVENT_ID);
            Toast.makeText(requireContext(), "EntrantsFragment received Event ID: " + eventId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No event ID received in EntrantsFragment", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initializes view components and loads registration counts.
     */
    private void initViews() {
        statusButtons.add(binding.btnEntrantsConfirmed);
        statusButtons.add(binding.btnEntrantsWaiting);
        statusButtons.add(binding.btnEntrantsSelected);
        statusButtons.add(binding.btnEntrantsCancelled);

        loadRegistrationCounts();
    }

    /**
     * Loads and displays registration counts for each status.
     */
    private void loadRegistrationCounts() {
        registrationRepository.getRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                int confirmed = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CONFIRMED).count();
                int waiting = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.WAITING).count();
                int selected = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.SELECTED).count();
                int cancelled = (int) registrations.stream().filter(registration -> registration.getStatus() == EntrantRegistrationStatus.CANCELLED).count();
                updateEntrantsCount(confirmed, waiting, selected, cancelled);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load registration counts", Toast.LENGTH_SHORT).show();
                updateEntrantsCount(0, 0, 0, 0);
            }
        });
    }

    /**
     * Sets up click listeners for status filter buttons.
     */
    private void setupClickListeners() {
        binding.btnEntrantsConfirmed.setOnClickListener(v -> {
            updateButtonSelection(binding.btnEntrantsConfirmed);
            loadConfirmedEntrants();
        });

        binding.btnEntrantsWaiting.setOnClickListener(v -> {
            updateButtonSelection(binding.btnEntrantsWaiting);
            loadWaitingEntrants();
        });

        binding.btnEntrantsSelected.setOnClickListener(v -> {
            updateButtonSelection(binding.btnEntrantsSelected);
            loadSelectedEntrants();
        });

        binding.btnEntrantsCancelled.setOnClickListener(v -> {
            updateButtonSelection(binding.btnEntrantsCancelled);
            loadCancelledEntrants();
        });

        // Add click listener for notification button
        binding.btnSendNotifications.setOnClickListener(v -> {
            showNotificationDialog();
        });

        // Set initial selection
        updateButtonSelection(binding.btnEntrantsConfirmed);
    }

    /**
     * Updates button selection state and visual appearance.
     */
    private void updateButtonSelection(LinearLayout selectedButton) {
        for (LinearLayout button : statusButtons) {
            boolean isSelected = button == selectedButton;
            button.setSelected(isSelected);
            button.setBackgroundResource(R.drawable.button_selector);
        }
    }

    /**
     * Shows dialog for entering notification message.
     */
    private void showNotificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Send Notification");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter your notification message...");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String message = input.getText().toString().trim();
            if (!message.isEmpty()) {
                sendNotificationToSelected(message);
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Sends notification to selected entrants.
     */
    private void sendNotificationToSelected(String message) {
        EntrantAdapter adapter = (EntrantAdapter) binding.lvEventEntrantList.getAdapter();
        if (adapter == null) return;

        List<String> selectedEntrantIds = adapter.getSelectedEntrantIds();
        if (selectedEntrantIds.isEmpty()) {
            Toast.makeText(requireContext(), "No entrants selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send notification
        notificationController.sendNotification(
                "Event Notification",
                message,
                eventId,
                selectedEntrantIds
        );

        Toast.makeText(requireContext(), "Notification sent to " + selectedEntrantIds.size() + " entrants", Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads initial entrants data (confirmed by default).
     */
    private void loadEntrantsData() {
        loadConfirmedEntrants();
    }

    /**
     * Sets up the RecyclerView with entrant data.
     */
    private void setupListView(List<Entrant> entrants) {
        binding.lvEventEntrantList.setLayoutManager(new LinearLayoutManager(requireContext()));
        EntrantAdapter adapter = new EntrantAdapter(requireContext(), entrants, eventId);
        binding.lvEventEntrantList.setAdapter(adapter);

        // Set status change listener to refresh counts and data
        adapter.setOnEntrantStatusChangeListener(new EntrantAdapter.OnEntrantStatusChangeListener() {
            @Override
            public void onEntrantStatusChanged() {
                loadRegistrationCounts();

                // Reload current entrants based on selected filter
                if (binding.btnEntrantsConfirmed.isSelected()) {
                    loadConfirmedEntrants();
                } else if (binding.btnEntrantsWaiting.isSelected()) {
                    loadWaitingEntrants();
                } else if (binding.btnEntrantsSelected.isSelected()) {
                    loadSelectedEntrants();
                } else if (binding.btnEntrantsCancelled.isSelected()) {
                    loadCancelledEntrants();
                }
            }
        });
    }

    /**
     * Loads confirmed entrants for the event.
     */
    private void loadConfirmedEntrants() {
        registrationRepository.getConfirmedRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                if (registrations.isEmpty()) {
                    updateAdapterWithEntrants(new ArrayList<>());
                    return;
                }
                loadEntrantsFromRegistrations(registrations);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load confirmed entrants", Toast.LENGTH_SHORT).show();
                updateAdapterWithEntrants(new ArrayList<>());
            }
        });
    }

    /**
     * Loads waiting entrants for the event.
     */
    private void loadWaitingEntrants() {
        registrationRepository.getWaitingRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                if (registrations.isEmpty()) {
                    updateAdapterWithEntrants(new ArrayList<>());
                    return;
                }
                loadEntrantsFromRegistrations(registrations);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load waiting entrants", Toast.LENGTH_SHORT).show();
                updateAdapterWithEntrants(new ArrayList<>());
            }
        });
    }

    /**
     * Loads selected entrants for the event.
     */
    private void loadSelectedEntrants() {
        registrationRepository.getSelectedRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                if (registrations.isEmpty()) {
                    updateAdapterWithEntrants(new ArrayList<>());
                    return;
                }
                loadEntrantsFromRegistrations(registrations);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load selected entrants", Toast.LENGTH_SHORT).show();
                updateAdapterWithEntrants(new ArrayList<>());
            }
        });
    }

    /**
     * Loads cancelled entrants for the event.
     */
    private void loadCancelledEntrants() {
        registrationRepository.getRegistrationsByStatus(eventId, EntrantRegistrationStatus.CANCELLED, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                if (registrations.isEmpty()) {
                    updateAdapterWithEntrants(new ArrayList<>());
                    return;
                }
                loadEntrantsFromRegistrations(registrations);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load cancelled entrants", Toast.LENGTH_SHORT).show();
                updateAdapterWithEntrants(new ArrayList<>());
            }
        });
    }

    /**
     * Loads entrant details from registration records.
     */
    private void loadEntrantsFromRegistrations(List<Registration> registrations) {
        List<Entrant> entrants = new ArrayList<>();
        final int[] processedCount = {0};

        for (Registration registration : registrations) {
            entrantRepository.findEntrantById(registration.getEntrantId(), new EntrantRepository.EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    if (entrant != null) {
                        entrants.add(entrant);
                    }
                    processedCount[0]++;

                    if (processedCount[0] == registrations.size()) {
                        updateAdapterWithEntrants(entrants);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("EntrantsFragment", "Failed to load entrant: " + registration.getEntrantId(), e);
                    processedCount[0]++;

                    if (processedCount[0] == registrations.size()) {
                        updateAdapterWithEntrants(entrants);
                    }
                }
            });
        }
    }

    /**
     * Updates the adapter with new entrant data.
     */
    private void updateAdapterWithEntrants(List<Entrant> entrants) {
        if (binding == null || getView() == null || !isAdded()) {
            return;
        }
        EntrantAdapter adapter = (EntrantAdapter) binding.lvEventEntrantList.getAdapter();
        if (adapter != null) {
            adapter.updateData(entrants);
        } else {
            setupListView(entrants);
        }
    }

    /**
     * Updates the count displays for each registration status.
     */
    public void updateEntrantsCount(int confirmed, int waiting, int selected, int cancelled) {
        if (binding == null || getView() == null || !isAdded()) {
            return;
        }

        binding.tvEventEntrantsConfirmedNumber.setText("(" + confirmed + ")");
        binding.tvEntrantsWaitingNumber.setText("(" + waiting + ")");
        binding.tvEntrantsSelectedNumber.setText("(" + selected + ")");
        binding.tvEntrantsCancelledNumber.setText("(" + cancelled + ")");
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