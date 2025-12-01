// C:/CMPUT-301/Project/static2/static2/app/src/androidTest/java/com/ualberta/static2/entrant/ProfileFragmentTest.java

package com.ualberta.static2.entrant;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.testutils.DatabaseCleaner;
import com.ualberta.static2.testutils.UserManagerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for Profile User Stories starting directly from the EntrantMainActivity.
 * Stories Tested:
 *  US-02.01.02: As a user, I want to update my profile information.
 *  US-02.01.04: As a user, I want to delete my profile.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileFragmentTest {

    private final CountingIdlingResource idlingResource =
            new CountingIdlingResource("ProfileFragmentTest");

    // This rule ensures UserManager is initialized and we have a userId before tests run.
    @Rule
    public UserManagerRule userManagerRule = new UserManagerRule();

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS
    );

    private String testUserId;

    @Before
    public void setup() throws Exception {
        IdlingRegistry.getInstance().register(idlingResource);
        testUserId = UserManager.getCurrentUserId();

        // --- PRE-TEST SETUP ---
        // Create a user profile in Firestore so that EntrantMainActivity can load it.
        // This replaces the need to manually click through the profile setup UI.
        User testUser = new User(testUserId, "Initial Name", "initial@test.com", "1234567890", null, "entrant", "", false);
        Tasks.await(FirebaseFirestore.getInstance().collection("users").document(testUserId).set(testUser));
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(idlingResource);
        // Clean up the user created in setup() to ensure test isolation.
        DatabaseCleaner.cleanUser(testUserId, 5000);
    }

    /**
     * Helper method to wait for long UI transitions (activity swap, dialog transitions, etc.)
     */
    private void performAndWaitForTransition(Runnable action) {
        idlingResource.increment();
        action.run();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        idlingResource.decrement();
    }

    /**
     * US-02.01.02
     * As a user, I want to update my profile.
     */
    @Test
    public void testUpdateProfile() {
        // 1. Launch directly into the Entrant's main activity.
        ActivityScenario.launch(EntrantMainActivity.class);

        // 2. Navigate to profile fragment using the bottom navigation bar.
        onView(withId(R.id.navigation_profile))
                .perform(click());

        // 3. Verify the initial data loaded from setup() is displayed.
        onView(withId(R.id.edit_name)).check(matches(withText("Initial Name")));

        // 4. Update fields
        String newName = "Test User Updated";
        String newPhone = "555-123-4567";
        onView(withId(R.id.edit_name)).perform(replaceText(newName), closeSoftKeyboard());
        onView(withId(R.id.edit_phone)).perform(replaceText(newPhone), closeSoftKeyboard());
        onView(withId(R.id.button_save)).perform(click());

        // 5. Verify updates
        onView(withId(R.id.edit_name)).check(matches(withText(newName)));
        onView(withId(R.id.edit_phone)).check(matches(withText(newPhone)));
    }

    /**
     * US-02.01.04
     * As a user, I want to delete my profile.
     */
    @Test
    public void testDeleteProfile() {
        // 1. Launch directly into the Entrant's main activity.
        ActivityScenario.launch(EntrantMainActivity.class);

        // 2. Navigate to profile fragment using the bottom navigation bar.
        onView(withId(R.id.navigation_profile))
                .perform(click());

        // 3. Click the main "Delete Profile" button on the fragment.
        onView(withId(R.id.button_delete))
                .perform(click());

        // 4. --- CORRECTED STEP ---
        // A confirmation dialog appears. Find the button with the text "Delete" and click it.
        // This action triggers the activity transition, so we wrap it.
        onView(withText("Delete")).perform(click());


        // 5. Verify the app has redirected to the profile setup screen.
        onView(withId(R.id.btn_save_profile))
                .check(matches(isDisplayed()));
    }
}
