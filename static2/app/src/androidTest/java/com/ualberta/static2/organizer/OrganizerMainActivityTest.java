package com.ualberta.static2.organizer;

import static org.junit.Assert.assertTrue;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.ui.organizer.organizerHome.OrganizerHomeFragment;
import com.ualberta.static2.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerMainActivityTest {

    @Test
    public void testActivityLaunchesSuccessfully() {
        // test if the activity launches successfully
        ActivityScenario.launch(OrganizerMainActivity.class);
    }

    @Test
    public void testInitialFragmentLoaded() {
        // test if the initial fragment loaded is the home fragment
        ActivityScenario<OrganizerMainActivity> scenario = ActivityScenario.launch(OrganizerMainActivity.class);

        scenario.onActivity(activity -> {
            Fragment fragment = activity.getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container_organizer);
            assertTrue(fragment instanceof OrganizerHomeFragment);
        });
    }
}
