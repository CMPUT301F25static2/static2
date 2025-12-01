package com.ualberta.static2.entrant;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ualberta.static2.MainActivity;
import com.ualberta.static2.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Blackbox tests for the complete user profile lifecycle, styled after EntrantMainActivityTest.
 * Stories Tested:
 *  - US 02.01.01: Set up a user profile.
 *  - US 01.02.02: Update profile information.
 *  - US 01.02.04: Delete profile.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragmentTest {

    // Using an IdlingResource is a more reliable way to handle waits than Thread.sleep()
    private final CountingIdlingResource idlingResource = new CountingIdlingResource("ProfileFragmentTest");

    @Before
    public void setUp() {
        // Register the idling resource before each test
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void tearDown() {
        // Unregister the idling resource after each test
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    /**
     * A helper method to perform a delayed action using an IdlingResource.
     * This signals to Espresso that a background task is running.
     */
    private void performDelayedAction(Runnable action) {
        idlingResource.increment(); // Start of background task
        new Thread(() -> {
            try {
                // The delay allows UI transitions to complete
                Thread.sleep(1500);
                action.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                idlingResource.decrement(); // End of background task
            }
        }).start();
    }


    /**
     * US 01.02.02: Tests if a user can set up a profile, navigate to it, and update it.
     */
    @Test
    public void testUpdateProfile() {
        // 1. Launch the app fresh
        ActivityScenario.launch(MainActivity.class);

        // 2. Start the Entrant flow
        onView(withId(R.id.btn_entrant)).perform(click());

        // 3. Set up the initial profile
        onView(withId(R.id.et_name)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.et_email)).perform(typeText("test.user@example.com"), closeSoftKeyboard());
        onView(withId(R.id.et_phone_number)).perform(typeText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.btn_save_profile)).perform(click());

        // 4. Navigate to the Profile Fragment using the bottom navigation bar
        // Espresso will wait until the main screen is idle before this action
        onView(withId(R.id.navigation_profile)).perform(click());

        // 5. Update the user's name and phone number
        String newName = "Test User Updated";
        String newPhone = "555-123-4567";
        onView(withId(R.id.edit_name)).perform(replaceText(newName), closeSoftKeyboard());
        onView(withId(R.id.edit_phone)).perform(replaceText(newPhone), closeSoftKeyboard());

        // 6. Save the changes
        onView(withId(R.id.button_save)).perform(click());

        // 7. Verify the information was updated correctly
        onView(withId(R.id.edit_name)).check(matches(withText(newName)));
        onView(withId(R.id.edit_phone)).check(matches(withText(newPhone)));
    }

    /**
     * US 01.02.04: Tests if a user can set up a profile and then delete it,
     * verifying redirection to the profile setup screen.
     */
    @Test
    public void testDeleteProfile() {
        // 1. Launch the app fresh
        ActivityScenario.launch(MainActivity.class);

        // 2. Start the Entrant flow
        onView(withId(R.id.btn_entrant)).perform(click());

        // 3. Set up a basic profile to get to the main app screen.
        onView(withId(R.id.et_name)).perform(typeText("Delete Me"), closeSoftKeyboard());
        onView(withId(R.id.et_email)).perform(typeText("delete.me@example.com"), closeSoftKeyboard());
        onView(withId(R.id.et_phone_number)).perform(typeText("0987654321"), closeSoftKeyboard());
        onView(withId(R.id.btn_save_profile)).perform(click());

        // 4. Navigate to the Profile Fragment
        onView(withId(R.id.navigation_profile)).perform(click());

        // 5. Scroll to the delete button and click it
        onView(withId(R.id.button_delete)).perform(scrollTo(), click());

        // 6. A confirmation dialog appears. Click the "Delete" button.
        onView(withText("Delete")).perform(click());

        // 7. Verify that the app redirects back to the ProfileSetupActivity.
        // We check for the "Save Profile" button. Espresso, combined with the idling resource,
        // will wait until the new activity is ready, preventing the race condition.
        onView(withId(R.id.btn_save_profile)).check(matches(isDisplayed()));
    }
}
