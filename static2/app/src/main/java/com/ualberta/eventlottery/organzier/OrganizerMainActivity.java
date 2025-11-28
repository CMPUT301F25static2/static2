package com.ualberta.eventlottery.organzier;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ualberta.eventlottery.ui.notifications.NotificationsFragment;
import com.ualberta.eventlottery.ui.organizer.organizerHome.OrganizerHomeFragment;
import com.ualberta.static2.R;

public class OrganizerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Default fragment on load
        if (savedInstanceState == null) {
            loadFragment(new OrganizerHomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new OrganizerHomeFragment();
            } else if (item.getItemId() == R.id.navigation_notifications) {
                selectedFragment = new NotificationsFragment();
            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_organizer, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
