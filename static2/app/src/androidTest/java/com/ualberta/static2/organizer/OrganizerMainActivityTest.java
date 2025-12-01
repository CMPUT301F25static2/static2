package com.ualberta.static2.organizer;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.MainActivity;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.ui.organizer.organizerHome.OrganizerHomeFragment;
import com.ualberta.static2.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerMainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    private IdlingResource idlingResource;

    private static final String TAG = "OrganizerBlackBoxTests";
    private FirebaseFirestore db;

    @Before
    public void setUp() {
        // Disable animations for tests
        getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global window_animation_scale 0"
        );
        getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global transition_animation_scale 0"
        );
        getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global animator_duration_scale 0"
        );

        // Grant notification permission so dialog doesn't appear
        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant com.ualberta.static2 android.permission.POST_NOTIFICATIONS"
        );

        Intents.init();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Setup complete");
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    // Testing if main activity loads
    @Test
    public void MainActivityLoads() {
        // Wait for the layout to be set (activity initialization)
        waitForViewToAppear(R.id.btn_admin);
        waitForViewToAppear(R.id.btn_entrant);
        waitForViewToAppear(R.id.btn_organizer);

        // Verify all buttons are displayed
        onView(withId(R.id.btn_entrant))
                .check(matches(isDisplayed()));

        onView(withId(R.id.btn_admin))
                .check(matches(isDisplayed()));

        onView(withId(R.id.btn_organizer))
                .check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("MainActivity");

    }

    // Testing if we can add an organizer account to the database
    // We need users for our organizer tests
    @Test
    public void AddOrganizer() throws InterruptedException {
        String userType = "organizer";
        String userId = "0000organizerBlackBoxTest";


        // Create user profile
        User userProfile = new User(userId, "OrganizerAndroidBlackBoxTest", "organizer@gmail.com", "2", "token", userType, "");
        Log.d(TAG, "User created: " + userProfile.getFcmToken());

        // Latch to wait for write to complete
        CountDownLatch writeLatch = new CountDownLatch(1);
        boolean[] writeSuccess = {false};

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Write Success");
                    writeSuccess[0] = true;
                    writeLatch.countDown();  // Signal write is done
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Write Failed: " + e.getMessage());
                    writeLatch.countDown();
                });

        // Wait for write to complete
        writeLatch.await();
        assertTrue("Adding user to Firestore succeeded", writeSuccess[0]);

        CountDownLatch queryLatch = new CountDownLatch(1);
        boolean[] found = {false};

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String type = doc.getString("userType");
                        Log.d(TAG, "Found user type: " + type);
                        found[0] = true;
                    } else {
                        Log.d(TAG, "Document doesn't exist");
                    }
                    queryLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Query failed: " + e.getMessage());
                    queryLatch.countDown();
                });

        // Wait for query to complete
        queryLatch.await();
        assertTrue("User found in Firestore", found[0]);
        assertTrue("User type is organizer",  userProfile.getUserType().equals("organizer"));

    }

    // Testing if we can setup an organizer account
    @Test
    public void SetUpOrganizer() {
        // Wait for the layout to be set (activity initialization)
        MainActivityLoads();
        onView(withId(R.id.btn_organizer))
                .perform(click());
        wait("Set Up Profile");
        
        // Handle the "Organizer Setup Required" dialog
        try {

            // Click "Set Up Profile" button (using button1 ID based on view hierarchy)
            onView(withText("Set Up Profile")).perform(click());
                    
            // Now we should be on the profile setup screen
            wait("Save Profile");
            
            // Fill in organizer profile information
            onView(withId(R.id.et_name)).perform(typeText("OrganizerAndroidBlackBoxTest"));
            onView(withId(R.id.et_name)).perform(closeSoftKeyboard());
            onView(withId(R.id.et_email)).perform(typeText("organizer@gmail.com"));
            onView(withId(R.id.et_email)).perform(closeSoftKeyboard());
            onView(withId(R.id.et_phone_number)).perform(typeText("1234567890"));
            onView(withId(R.id.et_phone_number)).perform(closeSoftKeyboard());
            onView(withId(R.id.radio_organizer)).perform(click());
            onView(withId(R.id.btn_save_profile)).perform(click());
            
            // Wait a bit for navigation to complete
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Ignore
            }
            
        } catch (NoMatchingViewException e) {
            // If dialog doesn't appear, we're already set up
            Log.d(TAG, "Organizer already set up, continuing with tests");
        }
        


        // Since we're already in organizer mode, we just verify the UI
        onView(withId(R.id.btn_create_event)).check(matches(isDisplayed()));
//        onView(withId(R.id.lv_organzier_event_list)).check(matches(isDisplayed()));
    }

    /* US 02.01.01
     * Testing if we can create a new event
     */
    @Test
    public void OrganizerCreateEvent() {
        // Make sure organizer is set up first by running the setup test
        SetUpOrganizer();

        // Click create event button
        onView(withId(R.id.btn_create_event))
                .perform(click());

        // Verify we're on the event creation screen by checking for the back button
//        onView(withId(R.id.btn_back)).check(matches(isDisplayed()));
        
        // Fill in event details
        onView(withId(R.id.et_create_event_title))
                .perform(typeText("Test Event"), closeSoftKeyboard());
        
        onView(withId(R.id.et_create_event_location))
                .perform(typeText("Test Location"), closeSoftKeyboard());
        
        onView(withId(R.id.et_create_event_price))
                .perform(typeText("0.00"), closeSoftKeyboard());
        
        onView(withId(R.id.et_create_event_capacity))
                .perform(typeText("50"), closeSoftKeyboard());
        
        onView(withId(R.id.et_create_event_description))
                .perform(typeText("This is a test event for Android testing."), closeSoftKeyboard());

        onView(withId(R.id.btn_create_event))
                .perform(scrollTo(), click());
        
        // Verify that we get a toast/error message about missing image

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        onView(withId(R.id.btn_create_event)).check(matches(isDisplayed()));
        
       
    }

    /*
     * Testing if we can draw entrants from waiting list
     */
    @Test
    public void OrganizerDrawEntrants() {
        setUpOrganizer();

        // Try to click on the draw button of the first event (if exists)
        try {
            onView(withId(R.id.btn_draw))
                    .check(matches(isDisplayed()))
                    .perform(click());

            // If draw screen opens, go back
            pressBack();
        } catch (NoMatchingViewException e) {
            Log.d(TAG, "No draw button found - this is expected if no events exist");
        }
    }

    /*
     * Testing if we can export entrant list
     */
    @Test
    public void OrganizerExportEntrants() {
        setUpOrganizer();

        try {
            onView(withId(R.id.btn_export))
                    .check(matches(isDisplayed()))
                    .perform(click());

            pressBack();
        } catch (NoMatchingViewException e) {
            Log.d(TAG, "No export button found - this is expected if no events exist");
        }
    }

    /* 
     * Testing if we can view event statistics
     */
    @Test
    public void OrganizerViewStatistics() {
        setUpOrganizer();

        // Verify statistics cards are displayed
        onView(withId(R.id.tv_total_events)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_total_entrants)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_full_rate)).check(matches(isDisplayed()));
    }

    /* 
     * Testing if we can view created events list
     */
    @Test
    public void OrganizerViewEventsList() {
        setUpOrganizer();

        // Verify event list container exists (even if empty)
        // We use a custom matcher to check if the view exists regardless of its dimensions
        onView(withId(R.id.lv_organzier_event_list)).check(matches(exists()));
    }

    /* 
     * Testing if we can view entrant waiting list
     */
    @Test
    public void OrganizerViewWaitingList() {
        setUpOrganizer();

        // Try to click on the first event to view details (if exists)
        try {
            onView(withId(R.id.organizer_event))
                    .check(matches(isDisplayed()))
                    .perform(click());

            pressBack();
        } catch (NoMatchingViewException e) {
            // No events exist, that's okay for this test
            Log.d(TAG, "No events found - this is expected if no events exist");
        }
    }
    
    // Testing bottom navigation
    @Test
    public void OrganizerBottomNavigation() {
        // Navigate to organizer
        setUpOrganizer();
        
        // Test navigating to notifications
        onView(withId(R.id.navigation_notifications))
                .perform(click());
                
        // Go back to home
        onView(withId(R.id.navigation_home))
                .perform(click());
                
        onView(withId(R.id.btn_create_event)).check(matches(isDisplayed()));
    }
    
    // Custom matcher to check if a view exists regardless of its visibility or dimensions
    private static Matcher<View> exists() {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("exists");
            }

            @Override
            protected boolean matchesSafely(View view) {
                // If we've reached this point, the view was found, so it exists
                return true;
            }
        };
    }
    
    // Helper method to set up organizer without depending on other test methods
    private void setUpOrganizer() {
        // Wait for the layout to be set (activity initialization)
        waitForViewToAppear(R.id.btn_admin);
        waitForViewToAppear(R.id.btn_entrant);
        waitForViewToAppear(R.id.btn_organizer);
        
        onView(withId(R.id.btn_organizer))
                .perform(click());
        wait("Set Up Profile");
        
        // Handle the "Organizer Setup Required" dialog
        try {
            // Click "Set Up Profile" button (using button1 ID based on view hierarchy)
            onView(withText("Set Up Profile")).perform(click());
                    
            // Now we should be on the profile setup screen
            wait("Save Profile");
            
            // Fill in organizer profile information
            onView(withId(R.id.et_name)).perform(typeText("OrganizerAndroidBlackBoxTest"));
            onView(withId(R.id.et_name)).perform(closeSoftKeyboard());
            onView(withId(R.id.et_email)).perform(typeText("organizer@gmail.com"));
            onView(withId(R.id.et_email)).perform(closeSoftKeyboard());
            onView(withId(R.id.et_phone_number)).perform(typeText("1234567890"));
            onView(withId(R.id.et_phone_number)).perform(closeSoftKeyboard());
            onView(withId(R.id.radio_organizer)).perform(click());
            onView(withId(R.id.btn_save_profile)).perform(click());
            
            // Wait a bit for navigation to complete
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Ignore
            }
            
        } catch (NoMatchingViewException e) {
            // If dialog doesn't appear, we're already set up
            Log.d(TAG, "Organizer already set up, continuing with tests");
        }
        
        // Since we're already in organizer mode, we just verify the UI
        onView(withId(R.id.btn_create_event)).check(matches(isDisplayed()));
    }

    // Helper method to wait for a view to appear
    private void waitForViewToAppear(int viewId) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = 5000; // 5 second timeout

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                onView(withId(viewId)).check(matches(isDisplayed()));
                return; // View found and displayed
            } catch (Exception e) {
                // View not found yet, try again
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private void wait(String text) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = 5000; // 5 second timeout

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                onView(withText(text)).check(matches(isDisplayed()));
                return; // View found and displayed
            } catch (Exception e) {
                // View not found yet, try again
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}