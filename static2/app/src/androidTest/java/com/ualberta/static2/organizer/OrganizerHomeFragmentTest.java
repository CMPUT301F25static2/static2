package com.ualberta.static2.organizer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.ui.organizer.organizerHome.OrganizerHomeFragment;
import com.ualberta.static2.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerHomeFragmentTest {

    @Test
    public void testEventListShowsAfterDataLoad() {
        FragmentScenario<OrganizerHomeFragment> scenario = FragmentScenario.launchInContainer(
                OrganizerHomeFragment.class,
                null,
                R.style.TestTheme
        );

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scenario.onFragment(fragment -> {

            assertTrue("List should have adapter",
                    fragment.binding.lvOrganzierEventList.getAdapter() != null);

            int itemCount = fragment.binding.lvOrganzierEventList.getAdapter().getCount();


            assertTrue("Adapter item count should be >= 0", itemCount >= 0);


            int visibility = fragment.binding.lvOrganzierEventList.getVisibility();
            assertTrue("List should be VISIBLE or at least not GONE", visibility == android.view.View.VISIBLE);


            assertTrue("List should be enabled", fragment.binding.lvOrganzierEventList.isEnabled());

        });
    }
}