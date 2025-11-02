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
import com.ualberta.static2.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrganizerMainActivity extends AppCompatActivity {


    ListView lv_organzier_event_list;
    private OrganizerEventAdapter adapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initViews();
        initData();
        setupAdapter();
        
    }

    private void initData() {
        eventList = new ArrayList<>();

        Event event1 = new Event();
        event1.setId("1");
        event1.setTitle("Morning Yoga Session");
        event1.setDescription("Relaxing morning yoga for all levels");
        event1.setMaxAttendees(50);
        event1.setEndTime(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L));
        eventList.add(event1);

        Event event2 = new Event();
        event2.setId("2");
        event2.setTitle("Tech Conference 2024");
        event2.setDescription("Annual technology conference");
        event2.setMaxAttendees(200);
        event2.setEndTime(new Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L));
        eventList.add(event2);

        Event event3 = new Event();
        event3.setId("3");
        event3.setTitle("Charity Run");
        event3.setDescription("5K run for charity");
        event3.setMaxAttendees(100);
        event3.setEndTime(new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L));
        eventList.add(event3);
    }

    private void initViews() {
        lv_organzier_event_list = findViewById(R.id.lv_organzier_event_list);
        lv_organzier_event_list.setDivider(new ColorDrawable(Color.TRANSPARENT));
        lv_organzier_event_list.setDividerHeight(16);
    }

    private void setupAdapter() {

        adapter = new OrganizerEventAdapter(this, eventList);
        lv_organzier_event_list.setAdapter(adapter);


        lv_organzier_event_list.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventList.get(position);

             Intent intent = new Intent(this, OrganizerEventInfoActivity.class);
             intent.putExtra("event_id", selectedEvent.getId());
             startActivity(intent);
        });
    }
}