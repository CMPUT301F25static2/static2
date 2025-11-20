package com.ualberta.eventlottery.organzier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ualberta.eventlottery.ui.organizer.organizerEventShowcase.OrganizerEventShowcaseFragment;
import com.ualberta.eventlottery.ui.organizer.organizerHome.OrganizerHomeFragment;
import com.ualberta.static2.R;

public class OrganizerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, new OrganizerHomeFragment())
                    .commit();
        }

        // If there is intent, get the event ID and replace the fragment to event Showcaase
        String eventId = getIntent().getStringExtra("id");
        if (eventId != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_organizer, OrganizerEventShowcaseFragment.newInstance(eventId))
                    .commit();
        }
    }

}