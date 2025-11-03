package com.ualberta.eventlottery.organzier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.organzier.fragment.DialogCustomContent;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityOrganizerEventInfoBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrganizerEventInfoActivity extends AppCompatActivity {

    private ActivityOrganizerEventInfoBinding binding;

    private String eventId;
    private Event currentEvent;
    private EventRepository eventRepo;
    private SimpleDateFormat dateFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerEventInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        receiveIntentData();
        initData();
        setUpView();
        setUpListener();


    }

    private void initData() {
        eventRepo = EventRepository.getInstance();
        dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }


    private void receiveIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("event_id")) {
            eventId = intent.getStringExtra("event_id");
            Toast.makeText(this, "Received Event ID: " + eventId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No event ID received", Toast.LENGTH_SHORT).show();
            finish(); // if no eventId, cloase current activity
        }
    }


    private void setUpView() {
        //TODO: find event from database based on eventId
        currentEvent = eventRepo.findEventById(eventId);

        if (currentEvent == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.tvEventTitle.setText(currentEvent.getTitle());

        binding.tvEventDescription.setText(currentEvent.getDescription());

        if (currentEvent.getPosterUrl() != null && !currentEvent.getPosterUrl().isEmpty()) {
            // TODO: use the image loading library to load images from web urls
        } else {
            // if no post img, use placeholder img
            binding.ivEventPosterImg.setImageResource(R.drawable.placeholder_background);
        }

        binding.tvEventUpdateTitle.setText(currentEvent.getTitle());

        binding.tvEventUpdateDescription.setText(currentEvent.getDescription());

        if (currentEvent.getEndTime() != null) {
            String formattedTime = "End: " + dateFormat.format(currentEvent.getEndTime());
            binding.tvEventUpdateEndTime.setText(formattedTime);
        } else {
            binding.tvEventUpdateEndTime.setText("End: TBD");
        }

        // TODO: get notifictaions of the current event(from database)

        if ( currentEvent.getLocationUrl() != null && !currentEvent.getLocationUrl().isEmpty()) {

            // TODO: use the image loading library to load images from web urls
        } else {
            // if no post img, use placeholder img
            binding.ivEventLocationImg.setImageResource(R.drawable.placeholder_background);
        }
        binding.tvEventLocation.setText(currentEvent.getLocation());
    }

    private void setUpListener() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnEventShowcase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OrganizerEventShowcaseActivity.class);
                intent.putExtra("event_id", eventId);
                startActivity(intent);
            }
        });

        // click to show dialog
        binding.btnEventUpdateTitle.setOnClickListener(v -> {
            showEditDialog("title", currentEvent.getTitle());
        });

        binding.btnEventUpdateDescription.setOnClickListener(v -> {
            showEditDialog("description", currentEvent.getDescription());
        });

        binding.btnEventUpdateEndTime.setOnClickListener(v -> {
            String formattedTime;
            if (currentEvent.getEndTime() != null) {
                formattedTime = "End: " + dateFormat.format(currentEvent.getEndTime());
            } else {
                formattedTime = "TBD";
            }
            showEditDialog("endTime", formattedTime);
        });

        binding.btnEventUpdateLocation.setOnClickListener(v -> {
            showEditDialog("location", currentEvent.getLocation());
        });

    }

    private void showEditDialog(String fieldType, String currentValue) {
        DialogCustomContent dialog = DialogCustomContent.newInstance(fieldType, currentValue);
        dialog.setOnDialogConfirmListener((type, newValue) -> {
            // update field based on fieldType and newValue
            handleFieldUpdate(type, newValue);
        });
        dialog.show(getSupportFragmentManager(), "edit_dialog_" + fieldType);
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
//                currentEvent.setEndTime(newValue);
                break;
        }
        eventRepo.updateEvent(currentEvent);
        setUpView();
        Toast.makeText(this, fieldType + " updated successfully", Toast.LENGTH_SHORT).show();
    }


}
