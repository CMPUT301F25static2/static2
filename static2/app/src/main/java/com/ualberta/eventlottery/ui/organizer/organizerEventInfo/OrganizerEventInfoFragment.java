package com.ualberta.eventlottery.ui.organizer.organizerEventInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.ui.organizer.fragment.DialogCustomContent;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.organizerEventQrcode.OrganizerEventQrcodeFragment;
import com.ualberta.eventlottery.ui.organizer.organizerEventShowcase.OrganizerEventShowcaseFragment;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.FragmentOrganizerEventInfoBinding;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrganizerEventInfoFragment extends Fragment {
    private static final String ARG_EVENT_ID = "event_id";

    private FragmentOrganizerEventInfoBinding binding;
    private String eventId;
    private Event currentEvent;
    private EventRepository eventRepo;
    private SimpleDateFormat dateFormat;

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
        binding.tvEventDescription.setText(currentEvent.getDescription());
        binding.tvEventUpdateTitle.setText(currentEvent.getTitle());
        binding.tvEventUpdateDescription.setText(currentEvent.getDescription());
        binding.tvEventLocation.setText(currentEvent.getLocation());

        if (currentEvent.getEndTime() != null) {
            String formattedTime = "End: " + dateFormat.format(currentEvent.getEndTime());
            binding.tvEventUpdateEndTime.setText(formattedTime);
        } else {
            binding.tvEventUpdateEndTime.setText("End: TBD");
        }

        if (currentEvent.getPosterUrl() != null && !currentEvent.getPosterUrl().isEmpty()) {
            // TODO: use the image loading library to load images from web urls
        } else {
            binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
        }

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
            String formattedTime = currentEvent.getEndTime() != null ?
                    dateFormat.format(currentEvent.getEndTime()) : "TBD";
            showEditDialog("endTime", formattedTime);
        });

        binding.btnEventUpdateLocation.setOnClickListener(v -> {
            showEditDialog("location", currentEvent.getLocation());
        });
    }

    private void showEditDialog(String fieldType, String currentValue) {
        DialogCustomContent dialog = DialogCustomContent.newInstance(fieldType, currentValue);
        dialog.setOnDialogConfirmListener((type, newValue) -> {
            handleFieldUpdate(type, newValue);
        });
        dialog.show(getChildFragmentManager(), "edit_dialog_" + fieldType);
    }

    private void handleFieldUpdate(String fieldType, String newValue) {
        switch (fieldType) {
            case "title":
                currentEvent.setTitle(newValue);
                break;
            case "description":
                currentEvent.setDescription(newValue);
                break;
            case "location":
                currentEvent.setLocation(newValue);
                break;
            case "endTime":

                break;
        }
        eventRepo.updateEvent(currentEvent);
        setUpView();
        Toast.makeText(requireContext(), fieldType + " updated successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
