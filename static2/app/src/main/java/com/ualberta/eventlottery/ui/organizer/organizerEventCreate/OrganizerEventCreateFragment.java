package com.ualberta.eventlottery.ui.organizer.organizerEventCreate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.databinding.FragmentOrganizerEventCreateBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment for creating new events by organizers.
 *
 * @author static2
 * @version 1.0
 */
public class OrganizerEventCreateFragment extends Fragment {
    private FragmentOrganizerEventCreateBinding binding;
    private EventRepository eventRepository;
    private String organizerId;

    private Calendar registrationStartCalendar;
    private Calendar registrationEndCalendar;
    private Calendar eventStartCalendar;
    private Calendar eventEndCalendar;
    private Calendar eventFromCalendar;
    private Calendar eventToCalendar;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateTimeFormat;

    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    /**
     * Creates the fragment's view hierarchy.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerEventCreateBinding.inflate(inflater, container, false);

        //initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // view image on imageView
                        binding.ivEventPoster.setImageURI(selectedImageUri);
                        binding.ivEventPoster.clearColorFilter();
                        binding.ivEventPoster.setImageTintList(null);
                        binding.ivEventPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        binding.ivEventPoster.setPadding(0, 0, 0, 0);
                        binding.ivEventPoster.setBackground(null);
                    }
                }
                });
        return binding.getRoot();
    }

    /**
     * Initializes the fragment after view creation.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        setUpListener();
        initDefaultDates();
    }

    /**
     * Initializes data repositories, formatters, and default calendar values.
     */
    private void initData() {
        eventRepository = EventRepository.getInstance();
        organizerId = UserManager.getCurrentUserId();

        // Initialize date formats
        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        dateTimeFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());

        // Initialize calendar objects
        registrationStartCalendar = Calendar.getInstance();
        registrationEndCalendar = Calendar.getInstance();
        eventStartCalendar = Calendar.getInstance();
        eventEndCalendar = Calendar.getInstance();
        eventFromCalendar = Calendar.getInstance();
        eventToCalendar = Calendar.getInstance();

        // Set default times
        registrationEndCalendar.add(Calendar.DAY_OF_YEAR, 7);
        eventStartCalendar.add(Calendar.DAY_OF_YEAR, 14);
        eventEndCalendar.add(Calendar.DAY_OF_YEAR, 14);
        eventToCalendar.add(Calendar.HOUR_OF_DAY, 2); // Default event duration: 2 hours
    }

    /**
     * Sets up default date displays for all time fields.
     */
    private void initDefaultDates() {
        updateRegistrationStartDisplay();
        updateRegistrationEndDisplay();
        updateEventStartDisplay();
        updateEventEndDisplay();
        updateEventTimeDisplay();
    }

    /**
     * Sets up click listeners for all interactive elements.
     */
    private void setUpListener() {

        binding.btnCreateEvent.setOnClickListener(v -> {
            createEvent();
        });

        // Back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Registration start date and time
        binding.tvRegistrationStartDate.setOnClickListener(v -> {
            showDatePickerDialog(registrationStartCalendar, this::updateRegistrationStartDisplay);
        });
        binding.tvRegistrationStartTime.setOnClickListener(v -> {
            showTimePickerDialog(registrationStartCalendar, this::updateRegistrationStartDisplay);
        });

        // Registration end date and time
        binding.tvRegistrationEndDate.setOnClickListener(v -> {
            showDatePickerDialog(registrationEndCalendar, this::updateRegistrationEndDisplay);
        });
        binding.tvRegistrationEndTime.setOnClickListener(v -> {
            showTimePickerDialog(registrationEndCalendar, this::updateRegistrationEndDisplay);
        });

        // Event start date and time
        binding.tvEventStartDate.setOnClickListener(v -> {
            showDatePickerDialog(eventStartCalendar, this::updateEventStartDisplay);
        });
        binding.tvEventStartTime.setOnClickListener(v -> {
            showTimePickerDialog(eventStartCalendar, this::updateEventStartDisplay);
        });

        // Event end date and time
        binding.tvEventEndDate.setOnClickListener(v -> {
            showDatePickerDialog(eventEndCalendar, this::updateEventEndDisplay);
        });
        binding.tvEventEndTime.setOnClickListener(v -> {
            showTimePickerDialog(eventEndCalendar, this::updateEventEndDisplay);
        });

        // Event daily time range
        binding.tvEventFrom.setOnClickListener(v -> {
            showTimePickerDialog(eventFromCalendar, this::updateEventTimeDisplay);
        });
        binding.tvEventTo.setOnClickListener(v -> {
            showTimePickerDialog(eventToCalendar, this::updateEventTimeDisplay);
        });

        // Add tags button
        binding.btnAddTags.setOnClickListener(v -> {
            showAddTagDialog();
        });

        // Create event button
        binding.btnCreateEvent.setOnClickListener(v -> {
            createEvent();
        });

        // Upload image
//        binding.createEventUploadImg.setOnClickListener(v -> {
//            Toast.makeText(requireContext(), "Image upload feature to be implemented", Toast.LENGTH_SHORT).show();
//        });

        binding.ivEventPoster.setOnClickListener(v -> {
            openImageChooser();
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Shows date picker dialog for calendar selection.
     */
    private void showDatePickerDialog(Calendar calendar, Runnable updateCallback) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateCallback.run();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    /**
     * Shows time picker dialog for time selection.
     */
    private void showTimePickerDialog(Calendar calendar, Runnable updateCallback) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateCallback.run();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    /**
     * Updates registration start time display.
     */
    private void updateRegistrationStartDisplay() {
        binding.tvRegistrationStartDate.setText(dateFormat.format(registrationStartCalendar.getTime()));
        binding.tvRegistrationStartTime.setText(timeFormat.format(registrationStartCalendar.getTime()));
    }

    /**
     * Updates registration end time display.
     */
    private void updateRegistrationEndDisplay() {
        binding.tvRegistrationEndDate.setText(dateFormat.format(registrationEndCalendar.getTime()));
        binding.tvRegistrationEndTime.setText(timeFormat.format(registrationEndCalendar.getTime()));
    }

    /**
     * Updates event start time display.
     */
    private void updateEventStartDisplay() {
        binding.tvEventStartDate.setText(dateFormat.format(eventStartCalendar.getTime()));
        binding.tvEventStartTime.setText(timeFormat.format(eventStartCalendar.getTime()));
    }

    /**
     * Updates event end time display.
     */
    private void updateEventEndDisplay() {
        binding.tvEventEndDate.setText(dateFormat.format(eventEndCalendar.getTime()));
        binding.tvEventEndTime.setText(timeFormat.format(eventEndCalendar.getTime()));
    }

    /**
     * Updates event daily time range display.
     */
    private void updateEventTimeDisplay() {
        binding.tvEventFrom.setText(timeFormat.format(eventFromCalendar.getTime()));
        binding.tvEventTo.setText(timeFormat.format(eventToCalendar.getTime()));
    }

    /**
     * Shows dialog for adding event tags.
     */
    private void showAddTagDialog() {
        Toast.makeText(requireContext(), "Add tag feature to be implemented", Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates new event with validated input data.
     */
    private void createEvent() {
        Log.d("Auth", "Current UID: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (!validateInput()) {
            return;
        }

        // check if image is selected
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an event poster", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Event newEvent = new Event();
            newEvent.setOrganizerId(organizerId);
            newEvent.setTitle(binding.etCreateEventTitle.getText().toString().trim());
            newEvent.setDescription(binding.etCreateEventDescription.getText().toString().trim());
            newEvent.setLocation(binding.etCreateEventLocation.getText().toString().trim());

            // Set capacity
            String capacityText = binding.etCreateEventCapacity.getText().toString().trim();
            if (!TextUtils.isEmpty(capacityText)) {
                newEvent.setMaxAttendees(Integer.parseInt(capacityText));
            }

            // Set times
            newEvent.setRegistrationStart(registrationStartCalendar.getTime());
            newEvent.setRegistrationEnd(registrationEndCalendar.getTime());
            newEvent.setStartTime(eventStartCalendar.getTime());
            newEvent.setEndTime(eventEndCalendar.getTime());

            // Set daily time range for API 26+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                newEvent.setDailyStartTime(
                        eventFromCalendar.get(Calendar.HOUR_OF_DAY),
                        eventFromCalendar.get(Calendar.MINUTE)
                );
                newEvent.setDailyEndTime(
                        eventToCalendar.get(Calendar.HOUR_OF_DAY),
                        eventToCalendar.get(Calendar.MINUTE)
                );
            }

            newEvent.setCategory("General");
            newEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
            newEvent.setEventStatus(EventStatus.UPCOMING);
            newEvent.setCurrentAttendees(0);

            eventRepository.addEventWithPoster(newEvent, selectedImageUri, new EventRepository.OperationCallback() {
                @Override
                public void onSuccess() {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });


        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Capacity must be a valid number", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validates all input fields for event creation.
     *
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInput() {
        // Validate title
        if (TextUtils.isEmpty(binding.etCreateEventTitle.getText().toString().trim())) {
            Toast.makeText(requireContext(), "Please enter event title", Toast.LENGTH_SHORT).show();
            binding.etCreateEventTitle.requestFocus();
            return false;
        }

        // Validate location
        if (TextUtils.isEmpty(binding.etCreateEventLocation.getText().toString().trim())) {
            Toast.makeText(requireContext(), "Please enter event location", Toast.LENGTH_SHORT).show();
            binding.etCreateEventLocation.requestFocus();
            return false;
        }

        // Validate capacity
        String capacityText = binding.etCreateEventCapacity.getText().toString().trim();
        if (TextUtils.isEmpty(capacityText)) {
            Toast.makeText(requireContext(), "Please enter capacity limit", Toast.LENGTH_SHORT).show();
            binding.etCreateEventCapacity.requestFocus();
            return false;
        }

        try {
            int capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                Toast.makeText(requireContext(), "Capacity must be greater than 0", Toast.LENGTH_SHORT).show();
                binding.etCreateEventCapacity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Capacity must be a valid number", Toast.LENGTH_SHORT).show();
            binding.etCreateEventCapacity.requestFocus();
            return false;
        }

        // Validate time logic
        if (registrationEndCalendar.before(registrationStartCalendar)) {
            Toast.makeText(requireContext(), "Registration end time cannot be before start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventEndCalendar.before(eventStartCalendar)) {
            Toast.makeText(requireContext(), "Event end time cannot be before start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eventStartCalendar.before(registrationEndCalendar)) {
            Toast.makeText(requireContext(), "Event start time should be after registration end time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(binding.etCreateEventTitle.getText().toString().trim())) {
            Toast.makeText(getContext(), "Event title cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an event poster.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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