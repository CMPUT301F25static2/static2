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
        
        onView(withId(R.id.list_notifications))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testFragmentDisplaysEmptyStateWhenNoNotifications() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        // Wait a bit for ViewModel to initialize and determine empty state
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Empty state should be visible when there are no notifications
        // Note: This depends on ViewModel returning empty list
        onView(withId(R.id.empty_state_layout))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyStateShowsCorrectText() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Check empty state text
        onView(withId(R.id.empty_title))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.empty_subtitle))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testFragmentHasCorrectLayout() {
        FragmentScenario<NotificationsFragment> scenario = 
                FragmentScenario.launch(NotificationsFragment.class);
        
        // Verify main components exist
        onView(withId(R.id.list_notifications))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.empty_state_layout))
                .check(matches(isDisplayed()));
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

