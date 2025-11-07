package com.ualberta.eventlottery.ui.organizer.organizerEventInfo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import java.util.Locale;

public class OrganizerEventInfoFragment extends Fragment {
    private static final String ARG_EVENT_ID = "event_id";

    public FragmentOrganizerEventInfoBinding binding;
    public String eventId;
    public Event currentEvent;

    public EventRepository eventRepository;
    public SimpleDateFormat dateFormat;

    public static OrganizerEventInfoFragment newInstance(String eventId) {
        OrganizerEventInfoFragment fragment = new OrganizerEventInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerEventInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiveArguments();
        initData();
        loadEventData();
    }

    private void receiveArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_EVENT_ID)) {
            eventId = args.getString(ARG_EVENT_ID);
        } else {
            Toast.makeText(requireContext(), "No event ID received", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
    }

    private void initData() {
        eventRepository = EventRepository.getInstance();
        dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }

    private void loadEventData() {
        binding.scrollView.setVisibility(View.GONE);

        eventRepository.findEventById(eventId, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {

                binding.scrollView.setVisibility(View.VISIBLE);

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

    private void setUpView() {
        if (currentEvent == null) {
            Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        binding.tvEventTitle.setText(currentEvent.getTitle());
        binding.tvEventDescription.setText(currentEvent.getDescription());
        binding.tvEventUpdateTitle.setText(currentEvent.getTitle());
        binding.tvEventUpdateDescription.setText(currentEvent.getDescription());
        binding.tvEventLocation.setText(currentEvent.getLocation());

        //status
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

        if (currentEvent.getEndTime() != null) {
            String formattedTime = dateFormat.format(currentEvent.getEndTime());
            binding.tvEventUpdateEndTime.setText(formattedTime);
        } else {
            binding.tvEventUpdateEndTime.setText("TBD");
        }


        //registration end time
        if (currentEvent.getRegistrationEnd() != null) {
            String formattedTime = dateFormat.format(currentEvent.getRegistrationEnd());
            binding.tvEventUpdateRegistryEndTime.setText(formattedTime);
        } else {
            binding.tvEventUpdateRegistryEndTime.setText("TBD");
        }

        //poster
        if (currentEvent.getPosterUrl() != null && !currentEvent.getPosterUrl().isEmpty()) {
            // TODO: use the image loading library to load images from web urls
        } else {
            binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
        }

        //location
        if (currentEvent.getLocationUrl() != null && !currentEvent.getLocationUrl().isEmpty()) {
            // TODO: use the image loading library to load images from web urls
        } else {
            binding.ivEventLocationImg.setImageResource(R.drawable.placeholder_background);
        }
    }

    private void setUpListener() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
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

        binding.btnEventUpdateLocation.setOnClickListener(v -> {
            showEditDialog("location", currentEvent.getLocation());
        });

        binding.btnEventUpdateStatus.setOnClickListener(v -> {
            String currentStatus = currentEvent.getEventStatus().toString();
            showStatusDialog(currentStatus);
        });


    }


    private void showStatusDialog(String currentStatus) {
        DialogUpdateStatus dialog = DialogUpdateStatus.newInstance(currentStatus);
        dialog.setOnStatusChangeListener(newStatus -> {
            handleFieldUpdate("status", newStatus);
        });
        dialog.show(getChildFragmentManager(), "status_dialog");
    }



    private void showEditDialog(String fieldType, String currentValue) {
        DialogCustomContent dialog = DialogCustomContent.newInstance(fieldType, currentValue);
        dialog.setOnDialogConfirmListener((type, newValue) -> {
            handleFieldUpdate(type, newValue);
        });
        dialog.show(getChildFragmentManager(), "edit_dialog_" + fieldType);
    }

    private void showDateTimePicker(String fieldType, Date currentDate) {
        // initialize the calendar with the current date
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}