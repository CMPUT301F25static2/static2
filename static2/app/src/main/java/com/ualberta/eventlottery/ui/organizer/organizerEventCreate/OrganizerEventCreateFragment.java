package com.ualberta.eventlottery.ui.organizer.organizerEventCreate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.databinding.FragmentOrganizerEventCreateBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerEventCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        setUpListener();
        initDefaultDates();
    }

    private void initData() {
        eventRepository = EventRepository.getInstance();
        organizerId = UserManager.getCurrentUserId();

        // initialize date formats
        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        dateTimeFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());

        // initialize calendar objects
        registrationStartCalendar = Calendar.getInstance();
        registrationEndCalendar = Calendar.getInstance();
        eventStartCalendar = Calendar.getInstance();
        eventEndCalendar = Calendar.getInstance();
        eventFromCalendar = Calendar.getInstance();
        eventToCalendar = Calendar.getInstance();

        // set default times (registration ends 7 days from now, event starts 14 days from now)
        registrationEndCalendar.add(Calendar.DAY_OF_YEAR, 7);
        eventStartCalendar.add(Calendar.DAY_OF_YEAR, 14);
        eventEndCalendar.add(Calendar.DAY_OF_YEAR, 14);
        eventToCalendar.add(Calendar.HOUR_OF_DAY, 2); // Default event duration: 2 hours
    }

    private void initDefaultDates() {
        // set default date displays
        updateRegistrationStartDisplay();
        updateRegistrationEndDisplay();
        updateEventStartDisplay();
        updateEventEndDisplay();
        updateEventTimeDisplay();
    }

    private void setUpListener() {
        // back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // registration start date
        binding.tvRegistrationStartDate.setOnClickListener(v -> {
            showDatePickerDialog(registrationStartCalendar, this::updateRegistrationStartDisplay);
        });

        // registration start time
        binding.tvRegistrationStartTime.setOnClickListener(v -> {
            showTimePickerDialog(registrationStartCalendar, this::updateRegistrationStartDisplay);
        });

        // registration end date
        binding.tvRegistrationEndDate.setOnClickListener(v -> {
            showDatePickerDialog(registrationEndCalendar, this::updateRegistrationEndDisplay);
        });

        // registration end time
        binding.tvRegistrationEndTime.setOnClickListener(v -> {
            showTimePickerDialog(registrationEndCalendar, this::updateRegistrationEndDisplay);
        });

        // event start date
        binding.tvEventStartDate.setOnClickListener(v -> {
            showDatePickerDialog(eventStartCalendar, this::updateEventStartDisplay);
        });

        // event start time
        binding.tvEventStartTime.setOnClickListener(v -> {
            showTimePickerDialog(eventStartCalendar, this::updateEventStartDisplay);
        });

        // event end date
        binding.tvEventEndDate.setOnClickListener(v -> {
            showDatePickerDialog(eventEndCalendar, this::updateEventEndDisplay);
        });

        // event end time
        binding.tvEventEndTime.setOnClickListener(v -> {
            showTimePickerDialog(eventEndCalendar, this::updateEventEndDisplay);
        });

        // event start time (From)
        binding.tvEventFrom.setOnClickListener(v -> {
            showTimePickerDialog(eventFromCalendar, this::updateEventTimeDisplay);
        });

        // event end time (To)
        binding.tvEventTo.setOnClickListener(v -> {
            showTimePickerDialog(eventToCalendar, this::updateEventTimeDisplay);
        });

        // add tags button
        binding.btnAddTags.setOnClickListener(v -> {
            showAddTagDialog();
        });

        // create event button
        binding.btnCreateEvent.setOnClickListener(v -> {
            createEvent();
        });

        // upload image
        binding.createEventUploadImg.setOnClickListener(v -> {
            // TODO: Implement image upload logic
            Toast.makeText(requireContext(), "Image upload feature to be implemented", Toast.LENGTH_SHORT).show();
        });
    }

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

    private void updateRegistrationStartDisplay() {
        binding.tvRegistrationStartDate.setText(dateFormat.format(registrationStartCalendar.getTime()));
        binding.tvRegistrationStartTime.setText(timeFormat.format(registrationStartCalendar.getTime()));
    }

    private void updateRegistrationEndDisplay() {
        binding.tvRegistrationEndDate.setText(dateFormat.format(registrationEndCalendar.getTime()));
        binding.tvRegistrationEndTime.setText(timeFormat.format(registrationEndCalendar.getTime()));
    }

    private void updateEventStartDisplay() {
        binding.tvEventStartDate.setText(dateFormat.format(eventStartCalendar.getTime()));
        binding.tvEventStartTime.setText(timeFormat.format(eventStartCalendar.getTime()));
    }

    private void updateEventEndDisplay() {
        binding.tvEventEndDate.setText(dateFormat.format(eventEndCalendar.getTime()));
        binding.tvEventEndTime.setText(timeFormat.format(eventEndCalendar.getTime()));
    }

    private void updateEventTimeDisplay() {
        binding.tvEventFrom.setText(timeFormat.format(eventFromCalendar.getTime()));
        binding.tvEventTo.setText(timeFormat.format(eventToCalendar.getTime()));
    }

    private void showAddTagDialog() {
        // TODO: implement add tag dialog
        Toast.makeText(requireContext(), "Add tag feature to be implemented", Toast.LENGTH_SHORT).show();
    }

    private void createEvent() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            // create new event
            Event newEvent = new Event();

            newEvent.setOrganizerId(organizerId); // Use current user's organizer ID
            newEvent.setTitle(binding.etCreateEventTitle.getText().toString().trim());
            newEvent.setDescription(binding.etCreateEventDescription.getText().toString().trim());
            newEvent.setLocation(binding.etCreateEventLocation.getText().toString().trim());

            // set capacity
            String capacityText = binding.etCreateEventCapacity.getText().toString().trim();
            if (!TextUtils.isEmpty(capacityText)) {
                newEvent.setMaxAttendees(Integer.parseInt(capacityText));
            }

            // set times
            newEvent.setRegistrationStart(registrationStartCalendar.getTime());
            newEvent.setRegistrationEnd(registrationEndCalendar.getTime());
            newEvent.setStartTime(eventStartCalendar.getTime());
            newEvent.setEndTime(eventEndCalendar.getTime());

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

            // TODO: Get from tags
            newEvent.setCategory("General");

            newEvent.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
            newEvent.setCurrentAttendees(0); // Initialize with 0 attendees

            // Save to Firestore using EventManger with callback
            eventRepository.addEvent(newEvent, new EventRepository.OperationCallback() {
                @Override
                public void onSuccess() {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                        // Go back to previous page
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

    private boolean validateInput() {
        // validate title
        if (TextUtils.isEmpty(binding.etCreateEventTitle.getText().toString().trim())) {
            Toast.makeText(requireContext(), "Please enter event title", Toast.LENGTH_SHORT).show();
            binding.etCreateEventTitle.requestFocus();
            return false;
        }

        // validate location
        if (TextUtils.isEmpty(binding.etCreateEventLocation.getText().toString().trim())) {
            Toast.makeText(requireContext(), "Please enter event location", Toast.LENGTH_SHORT).show();
            binding.etCreateEventLocation.requestFocus();
            return false;
        }

        // validate capacity
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

        // validate time logic
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

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

