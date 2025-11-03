package com.ualberta.eventlottery.organzier;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.organzier.adapter.OrganizerEventAdapter;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityOrganizerEventInfoBinding;
import com.ualberta.static2.databinding.ActivityOrganizerMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrganizerMainActivity extends AppCompatActivity {

    private OrganizerEventAdapter adapter;
    private EventRepository eventRepo;
    private ActivityOrganizerMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        initData();
        setupAdapter();
        setupListener();
        
    }

    private void initData() {
        eventRepo = EventRepository.getInstance();

    }

    private void initViews() {
        binding.lvOrganzierEventList.setDivider(new ColorDrawable(Color.TRANSPARENT));
        binding.lvOrganzierEventList.setDividerHeight(18);

    }

    private void setupAdapter() {
        List<Event> eventList = eventRepo.getAllEvents();
        adapter = new OrganizerEventAdapter(this, eventList);
        binding.lvOrganzierEventList.setAdapter(adapter);


        binding.lvOrganzierEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventList.get(position);

            Intent intent = new Intent(this, OrganizerEventInfoActivity.class);
            intent.putExtra("event_id", selectedEvent.getId());
            startActivity(intent);
        });
    }

    private void setupListener() {
        binding.btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerEventCreateActivity.class);
            startActivity(intent);
        });

    }

}