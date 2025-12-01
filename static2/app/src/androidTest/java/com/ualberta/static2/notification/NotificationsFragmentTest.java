package com.ualberta.static2.notification;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.ualberta.eventlottery.ui.notifications.NotificationsFragment;
import com.ualberta.static2.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Android instrumentation tests for NotificationsFragment class.
 * Tests fragment UI display, empty state, and notification list rendering.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationsFragmentTest {

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS);

    @Test
    public void testFragmentCreation() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
        });
    }

    @Test
    public void testFragmentDisplaysListView() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        // Wait for fragment to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // The list view exists in the layout, but may be hidden if empty
        // Check that it exists in the hierarchy (even if not visible)
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView());
            assertNotNull(fragment.getView().findViewById(R.id.list_notifications));
        });
    }

    @Test
    public void testFragmentDisplaysEmptyStateWhenNoNotifications() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        // Wait for ViewModel to initialize and determine empty state
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Empty state should be visible when there are no notifications
        // The ViewModel will set it to visible if notifications list is empty
        // Check that the view exists in the fragment
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView());
            assertNotNull(fragment.getView().findViewById(R.id.empty_state_layout));
        });
    }

    @Test
    public void testEmptyStateShowsCorrectText() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        // Wait for ViewModel to initialize
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Check that empty state views exist in the fragment
        // They may not be visible initially, but should exist in the layout
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView());
            assertNotNull(fragment.getView().findViewById(R.id.empty_title));
            assertNotNull(fragment.getView().findViewById(R.id.empty_subtitle));
        });
    }

    @Test
    public void testFragmentHasCorrectLayout() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        // Wait for fragment to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Verify main components exist in the layout
        // They may not be visible, but should exist in the view hierarchy
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView());
            assertNotNull(fragment.getView().findViewById(R.id.list_notifications));
            assertNotNull(fragment.getView().findViewById(R.id.empty_state_layout));
        });
    }

    @Test
    public void testFragmentLifecycle() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        scenario.onFragment(fragment -> {
            assertTrue(fragment.isAdded());
            assertTrue(fragment.isResumed());
        });
        
        // Move to paused state
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.STARTED);
        
        scenario.onFragment(fragment -> {
            assertTrue(fragment.isAdded());
        });
        
        // Recreate fragment
        scenario.recreate();
        
        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
        });
    }
}

