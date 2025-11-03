package com.ualberta.eventlottery.organzier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
    }

}