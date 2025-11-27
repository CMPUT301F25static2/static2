package com.ualberta.eventlottery.ui.organizer.organizerEventInfo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.RegistrationRepository;

import com.bumptech.glide.Glide;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.fragment.DialogCustomContent;
import com.ualberta.eventlottery.ui.organizer.fragment.DialogUpdateStatus;
import com.ualberta.eventlottery.ui.organizer.organizerEventQrcode.OrganizerEventQrcodeFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventShowcase.OrganizerEventShowcaseFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerEventInfoBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for displaying and editing event details.
 * Allows organizers to view and modify event information.
 *
 * @author static2
 * @version 1.0
 */
public class OrganizerEventInfoFragment extends Fragment {
    private static final String ARG_EVENT_ID = "event_id";

    public FragmentOrganizerEventInfoBinding binding;
    public String eventId;
    public Event currentEvent;

    public EventRepository eventRepository;
    public RegistrationRepository registrationRepository;
    public SimpleDateFormat dateFormat;

    // Map-related fields
    private GoogleMap googleMap;
    private boolean isMapReady = false;

    /**
     * Creates a new instance with the specified event ID.
     *
     * @param eventId the ID of the event to display
     * @return new OrganizerEventInfoFragment instance
     */
    public static OrganizerEventInfoFragment newInstance(String eventId) {
        OrganizerEventInfoFragment fragment = new OrganizerEventInfoFragment();
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
        binding = FragmentOrganizerEventInfoBinding.inflate(inflater, container, false);
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
        dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }

    /**
     * Loads event data from repository.
     */
    private void loadEventData() {
        binding.scrollView.setVisibility(View.GONE);

        eventRepository.findEventById(eventId, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                binding.scrollView.setVisibility(View.VISIBLE);
                currentEvent = event;
                setUpView();
                setUpListener();
                initializeMap();
                loadEntrantLocations();
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded() || getContext() == null) {
                    return;
                }
            }
        });
    }

    /**
     * Sets up the view with event data.
     */
    private void setUpView() {
        if (currentEvent == null) {
            Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        // Update statuses based on current time and deadlines
        currentEvent.updateRegistrationStatusBasedOnDeadline();
        if (currentEvent.getEventStatus() == EventStatus.UPCOMING && currentEvent.getStartTime() != null) {
            java.util.Date now = new java.util.Date();
            if (now.after(currentEvent.getStartTime()) && now.before(currentEvent.getEndTime())) {
                currentEvent.setEventStatus(EventStatus.ONGOING);
            } else if (now.after(currentEvent.getEndTime())) {
                currentEvent.setEventStatus(EventStatus.CLOSED);
            }
        }

        binding.tvEventTitle.setText(currentEvent.getTitle());
        binding.tvEventDescription.setText(currentEvent.getDescription());
        binding.tvEventUpdateTitle.setText(currentEvent.getTitle());
        binding.tvEventUpdateDescription.setText(currentEvent.getDescription());
//        binding.tvEventLocation.setText(currentEvent.getLocation());

        // Set geolocation requirement toggle
        binding.switchEventGeolocationRequired.setChecked(currentEvent.isLocationRequired());

        // set up image
        if (currentEvent.getPosterUrl() != null && !currentEvent.getPosterUrl().isEmpty()) {
            // use glide to load image
            Glide.with(requireContext())
                    .load(currentEvent.getPosterUrl())
                    .placeholder(R.drawable.placeholder_background)
                    .error(R.drawable.placeholder_background)
                    .into(binding.ivEventPosterImg);

        } else {
            // if no imge, using placeholder
            binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
        }

        // Set event status with color coding
        if (currentEvent.getEventStatus() != null) {
            String status = currentEvent.getEventStatus().toString();
            binding.tvEventUpdateStatus.setText(status);

            switch (status.toLowerCase()) {
                case "ongoing":
                    binding.tvEventUpdateStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_deep));
                    break;
                case "closed":
                    binding.tvEventUpdateStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_deep));
                    break;
                case "upcoming":
                    binding.tvEventUpdateStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                    break;
                default:
                    binding.tvEventUpdateStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
                    break;
            }
        } else {
            binding.tvEventUpdateStatus.setText("Not set");
        }

        // Set event end time
        if (currentEvent.getEndTime() != null) {
            String formattedTime = dateFormat.format(currentEvent.getEndTime());
            binding.tvEventUpdateEndTime.setText(formattedTime);
        } else {
            binding.tvEventUpdateEndTime.setText("TBD");
        }

        // Set registration end time
        if (currentEvent.getRegistrationEnd() != null) {
            String formattedTime = dateFormat.format(currentEvent.getRegistrationEnd());
            binding.tvEventUpdateRegistryEndTime.setText(formattedTime);
        } else {
            binding.tvEventUpdateRegistryEndTime.setText("TBD");
        }


    }

    /**
     * Sets up click listeners for navigation and editing.
     */
    private void setUpListener() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.chipGroupEntrantStatus.setOnCheckedChangeListener((group, checkedId) -> {
            loadEntrantLocations();
        });

        binding.btnDeleteEvent.setOnClickListener(v -> {
            // Show a confirmation dialog before deleting
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to permanently delete this event? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        eventRepository.deleteEvent(eventId, new EventRepository.BooleanCallback() {
                            @Override
                            public void onSuccess(boolean result) {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                                    // Go back to the previous screen (OrganizerHomeFragment)
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "Failed to delete event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.btnEventShowcase.setOnClickListener(v -> {
            OrganizerEventShowcaseFragment fragment = OrganizerEventShowcaseFragment.newInstance(eventId);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnEventShowQrcode.setOnClickListener(v -> {
            OrganizerEventQrcodeFragment fragment = OrganizerEventQrcodeFragment.newInstance(eventId);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Edit field listeners
        binding.btnEventUpdateTitle.setOnClickListener(v -> {
            showEditDialog("title", currentEvent.getTitle());
        });

        binding.btnEventUpdateDescription.setOnClickListener(v -> {
            showEditDialog("description", currentEvent.getDescription());
        });

        binding.btnEventUpdateEndTime.setOnClickListener(v -> {
            showDateTimePicker("eventEndTime", currentEvent.getEndTime());
        });

        binding.btnEventUpdateRegistryEndTime.setOnClickListener(v -> {
            showDateTimePicker("registryEndTime", currentEvent.getRegistrationEnd());
        });

//        binding.btnEventUpdateLocation.setOnClickListener(v -> {
//            showEditDialog("location", currentEvent.getLocation());
//        });

        binding.btnEventUpdateGeolocation.setOnClickListener(v -> {
            // Toggle the geolocation requirement
            boolean newValue = !binding.switchEventGeolocationRequired.isChecked();
            binding.switchEventGeolocationRequired.setChecked(newValue);
            handleFieldUpdate("locationRequired", newValue);
        });

        binding.btnEventUpdateStatus.setOnClickListener(v -> {
            String currentStatus = currentEvent.getEventStatus().toString();
            showStatusDialog(currentStatus);
        });
    }



    /**
     * Shows dialog for updating event status.
     */
    private void showStatusDialog(String currentStatus) {
        DialogUpdateStatus dialog = DialogUpdateStatus.newInstance(currentStatus);
        dialog.setOnStatusChangeListener(newStatus -> {
            handleFieldUpdate("status", newStatus);
        });
        dialog.show(getChildFragmentManager(), "status_dialog");
    }

    /**
     * Shows dialog for editing text fields.
     */
    private void showEditDialog(String fieldType, String currentValue) {
        DialogCustomContent dialog = DialogCustomContent.newInstance(fieldType, currentValue);
        dialog.setOnDialogConfirmListener((type, newValue) -> {
            handleFieldUpdate(type, newValue);
        });
        dialog.show(getChildFragmentManager(), "edit_dialog_" + fieldType);
    }

    /**
     * Shows time picker dialog for time fields.
     */
    private void showDateTimePicker(String fieldType, Date currentDate) {
        final Calendar calendar = Calendar.getInstance();
        if (currentDate != null) {
            calendar.setTime(currentDate);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    showDatePicker(fieldType, calendar.getTime());
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    /**
     * Shows date picker dialog for date fields.
     */
    private void showDatePicker(String fieldType, Date selectedTime) {
        final Calendar calendar = Calendar.getInstance();
        if (selectedTime != null) {
            calendar.setTime(selectedTime);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    handleFieldUpdate(fieldType, calendar.getTime());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    /**
     * Handles field updates and saves to repository.
     */
    private void handleFieldUpdate(String fieldType, Object newValue) {
        switch (fieldType) {
            case "title":
                currentEvent.setTitle((String) newValue);
                break;
            case "description":
                currentEvent.setDescription((String) newValue);
                break;
            case "location":
                currentEvent.setLocation((String) newValue);
                break;
            case "locationRequired":
                currentEvent.setLocationRequired((Boolean) newValue);
                break;
            case "eventEndTime":
                if (newValue instanceof Date) {
                    currentEvent.setEndTime((Date) newValue);
                }
                break;
            case "registryEndTime":
                if (newValue instanceof Date) {
                    currentEvent.setRegistrationEnd((Date) newValue);
                }
                break;
            case "status":
                if (newValue instanceof String) {
                    currentEvent.setEventStatus(EventStatus.valueOf(((String) newValue).toUpperCase()));
                }
                break;
        }

        eventRepository.updateEvent(currentEvent, new EventRepository.BooleanCallback() {
            @Override
            public void onSuccess(boolean result) {
                if (result) {
                    setUpView();
                    Toast.makeText(requireContext(), fieldType + " updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to update " + fieldType, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Error updating " + fieldType + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initializes the map for displaying entrant locations.
     */
    private void initializeMap() {
        // Only initialize map if geolocation is required for this event
        if (currentEvent != null && currentEvent.isLocationRequired()) {
            // Check if there's a map fragment in the layout
            SupportMapFragment mapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.map);

            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        googleMap = map;
                        isMapReady = true;

                        // Set up basic map settings
                        map.getUiSettings().setZoomControlsEnabled(true);
                        map.getUiSettings().setScrollGesturesEnabled(true);
                        map.getUiSettings().setRotateGesturesEnabled(false);

                        // Load markers once map is ready
                        loadEntrantLocations();
                    }
                });
            }
        }
    }

    /**
     * Loads and displays entrant locations on the map based on chip selection.
     */
    private void loadEntrantLocations() {
        if (registrationRepository == null || eventId == null) {
            return;
        }

        int checkedChipId = binding.chipGroupEntrantStatus.getCheckedChipId();

        if (checkedChipId == R.id.chip_all_entrants) {
            loadAllEntrantLocations();
        } else if (checkedChipId == R.id.chip_waiting_list) {
            loadWaitingListLocations();
        }
    }

    /**
     * Loads all entrant locations.
     */
    private void loadAllEntrantLocations() {
        registrationRepository.getRegistrationsByEvent(eventId, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                displayEntrantLocationsOnMap(registrations);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load entrant locations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads waiting list entrant locations.
     */
    private void loadWaitingListLocations() {
        registrationRepository.getRegistrationsByStatus(eventId, EntrantRegistrationStatus.WAITING, new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
                displayEntrantLocationsOnMap(registrations);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load waiting list locations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays entrant locations as markers on the map.
     */
    private void displayEntrantLocationsOnMap(java.util.List<Registration> registrations) {
        if (googleMap == null || registrations == null) {
            return;
        }

        // Clear existing markers
        googleMap.clear();

        if (registrations.isEmpty()) {
            Toast.makeText(getContext(), "No location data available for the selected filter", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int validLocations = 0;

        for (Registration registration : registrations) {
            if (registration.getLatitude() != null && registration.getLongitude() != null) {
                LatLng location = new LatLng(registration.getLatitude(), registration.getLongitude());

                // Create marker with registration info
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .title("Entrant Location")
                        .snippet(registration.getLocationAddress() != null ?
                                registration.getLocationAddress() :
                                "Location not available")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                Marker marker = googleMap.addMarker(markerOptions);
                boundsBuilder.include(location);
                validLocations++;
            }
        }

        if (validLocations > 0) {
            // Adjust camera to show all markers
            try {
                LatLngBounds bounds = boundsBuilder.build();
                int padding = 100; // offset from edges of the map in pixels
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            } catch (Exception e) {
                // If bounds creation fails, that's okay - map will just not zoom to bounds
            }
        } else {
            Toast.makeText(getContext(), "No location data available for entrants", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Cleans up resources when view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        googleMap = null;
        isMapReady = false;
        binding = null;
    }
}
