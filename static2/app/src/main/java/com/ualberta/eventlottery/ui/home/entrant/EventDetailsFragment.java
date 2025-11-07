package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentEventDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventDetailsFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private FragmentEventDetailsBinding binding;

    private String mEventId;
    private EventDetailsViewModel eventDetailsViewModel;
    private RegistrationRepository registrationRepository;


    private Registration currentUserRegistration = null;

    public EventDetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_EVENT_ID);
        }
        registrationRepository = RegistrationRepository.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backButton.setOnClickListener(v -> NavHostFragment.findNavController(EventDetailsFragment.this).popBackStack());

        binding.registerButton.setOnClickListener(v -> {

            if (currentUserRegistration != null) {
                withdrawFromEvent();
            } else {
                registerForEvent();
            }
        });

        if (mEventId == null) {
            Toast.makeText(getContext(), "Error: Event ID not found", Toast.LENGTH_LONG).show();
            binding.eventDetailsTitle.setText("Event Not Found");
            binding.registerButton.setEnabled(false);
            return;
        }

        checkUserRegistrationStatus();

        com.ualberta.eventlottery.ui.home.entrant.EventDetailsViewModelFactory factory = new com.ualberta.eventlottery.ui.home.entrant.EventDetailsViewModelFactory(mEventId);
        eventDetailsViewModel = new ViewModelProvider(this, factory).get(EventDetailsViewModel.class);
        eventDetailsViewModel.getEventLiveData().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                populateUi(event);
            } else {
                binding.eventDetailsTitle.setText("Event data could not be loaded.");
                binding.registerButton.setEnabled(false);
            }
        });
    }

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
                binding.registerButton.setEnabled(true);
            }
        });
    }

    private void updateButtonUi() {

        if (currentUserRegistration != null) {
            binding.registerButton.setText("Withdraw");
            binding.registerButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            binding.registerButton.setText("Register");
            binding.registerButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_500));
        }
    }

    private void registerForEvent() {
        String currentUserId = UserManager.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(getContext(), "You must be signed in to register", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEventId == null) return;

        binding.registerButton.setEnabled(false);
        binding.registerButton.setText("Registering...");

        // Use the registerUser method you already have
        registrationRepository.registerUser(mEventId, currentUserId, new RegistrationRepository.RegistrationCallback() {
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

    private void withdrawFromEvent() {

        if (currentUserRegistration == null || currentUserRegistration.getId() == null) {
            Toast.makeText(getContext(), "Cannot withdraw: Registration not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.registerButton.setEnabled(false);
        binding.registerButton.setText("Withdrawing...");

        // --- Use deleteRegistration directly with the ID ---
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

    private void populateUi(Event event) {
        // ... (this method is unchanged)
        binding.eventDetailsTitle.setText(event.getTitle());
        binding.eventDetailsDescription.setText(event.getDescription());
        SimpleDateFormat dateSdf = new SimpleDateFormat("MMM dd, yyyy", Locale.CANADA);
        String fromToText = "Dates TBD";
        if (event.getStartTime() != null && event.getEndTime() != null) {
            fromToText = dateSdf.format(event.getStartTime()) + " - " + dateSdf.format(event.getEndTime());
        }
        binding.eventDetailsDate.setText(fromToText);
        String timeText = "Time TBD";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (event.getDailyStartTime() != null) {
                timeText = "Starts at " + event.getDailyStartTime().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
            }
        }
        binding.eventDetailsTime.setText(timeText);
        String location = event.getLocation();
        if (location != null && !location.isEmpty()) {
            binding.eventDetailsLocation.setText(location);
        } else {
            binding.eventDetailsLocation.setText("Location TBD");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
