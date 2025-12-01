package com.ualberta.static2;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.not;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.MainActivity;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.ui.profile.ProfileFragment;
import com.ualberta.eventlottery.utils.UserManager;

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
public class AdminMainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    private IdlingResource idlingResource;

    private static final String TAG = "AdminBlackBoxTests";
    private FirebaseFirestore db;

    @Before
    public void setUp() {
        // Disable animations for tests
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global window_animation_scale 0"
        );
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global transition_animation_scale 0"
        );
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "settings put global animator_duration_scale 0"
        );

        // Grant notification permission so dialog doesn't appear
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant com.ualberta.static2 android.permission.POST_NOTIFICATIONS"
        );

        Intents.init();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Setup complete");
    }

    @After
    public void tearDown()
        {
        Intents.release();
    }

    // Testing if main activity loads
    @Test
    public void testA_MainActivityLoads() {
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

    // Testing if we can add an entrant account to the database
    // We need users for our admin tests
    @Test
    public void testB_AddEntrant() throws InterruptedException {
        String userType = "entrant";
        String userId = "00000adminBlackBoxTest";


        // Create user profile
        User userProfile = new User(userId, "AdminAndroidBlackBoxTestEntrant", "aw@gmail.com", "2", "token", userType, "");
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
        assertTrue("User type is admin",  userProfile.getUserType().equals("entrant"));

    }

    // Testing if we can add an organizer account to the database
    // We need users for our admin tests
    @Test
    public void testC_AddOrganizer() throws InterruptedException {
        String userType = "organizer";
        String userId = "0000adminBlackBoxTest";


        // Create user profile
        User userProfile = new User(userId, "AdminAndroidBlackBoxTestOrganizer", "aw@gmail.com", "2", "token", userType, "");
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
        assertTrue("User type is admin",  userProfile.getUserType().equals("organizer"));

    }

    // Testing if we can setup an admin account
    // We need users for our admin tests
    @Test
    public void testD_SetUpAdmin() {
        // Wait for the layout to be set (activity initialization)
        testA_MainActivityLoads();
        onView(withId(R.id.btn_admin))
                .perform(click());
        wait("Set Up Profile");

        // Set up admin account
        onView(withText("Set Up Profile")).perform(click());
        onView(withId(R.id.et_name)).perform(typeText("AdminAndroidBlackBoxTestAdmin"));
        onView(withId(R.id.et_email)).perform(typeText("AdminBlackBoxTest@gmail.com"));
        onView(withId(R.id.et_phone_number)).perform(typeText("1234567890"));
        onView(withId(R.id.radio_organizer)).perform(closeSoftKeyboard());
        onView(withId(R.id.radio_admin)).perform(click());
        onView(withId(R.id.btn_save_profile)).perform(click());

        // Verify AdminMainActivity was launched
        waitForViewToAppear(R.id.admin_browse);
        this.getClass().getSimpleName().matches("AdminMainActivity");
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
        onView(withText("Admin Portal")).check(matches(isDisplayed()));
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));

    }

    /* US 03.05.01
    * Testing if we can browse users and open their profiles
     */
    @Test
    public void testE_AdminPortalBrowseUsers() {
        // Set up admin account and launch admin portal
        try {
            testD_SetUpAdmin();
        } catch (NoMatchingViewException e) {
            // We're already set up, just continue
        }

        onView(withText("Browse Users")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(1).perform(click());
        onView(withId(R.id.searchUsers)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminUsersFragment");
        waitForViewToAppear(R.id.sortButtonUsersEntrants);
        waitForViewToAppear(R.id.sortButtonUsersOrganizers);
        waitForViewToAppear(R.id.sortButtonUsersAdmins);

        // Sort by organizer
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        onView(withId(R.id.searchUsers)).perform(click())
                .perform(typeText("AdminAndroidBlackBoxTestOrg"))
                .perform(closeSoftKeyboard());
        // Check if the user AdminAndroidBlackBoxTestOrganizer is displayed
        try {
            onView(withText("AdminAndroidBlackBoxTestOrganizer"))
                    .check(matches(isDisplayed()));
            // Success - at least one exists
        } catch (AmbiguousViewMatcherException e) {
            // Multiple found - that's fine! At least one exists
            assertTrue(true);
        }

        // Sort by admin
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        onView(withId(R.id.searchUsers)).perform(click())
                .perform(replaceText(""))
                .perform(typeText("AdminAndroidBlackBoxTestAdmin"))
                .perform(closeSoftKeyboard());
        // Check if the user AdminAndroidBlackBoxTestAdmin is displayed
        try {
            onView(withText("AdminAndroidBlackBoxTestAdmin"))
                    .check(matches(isDisplayed()));
            // Success - at least one exists
        } catch (AmbiguousViewMatcherException e) {
            // Multiple found - that's fine! At least one exists
            assertTrue(true);
        }

        // Sort by entrant
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        onView(withId(R.id.searchUsers)).perform(click())
                .perform(replaceText(""))
                .perform(typeText("AdminAndroidBlackBoxTestEntrant"))
                .perform(closeSoftKeyboard());
        // Check if the user AdminAndroidBlackBoxTestEntrant is displayed
        try {
            onView(withText("AdminAndroidBlackBoxTestEntrant"))
                    .check(matches(isDisplayed()));
            // Success - at least one exists
        } catch (AmbiguousViewMatcherException e) {
            // Multiple found - that's fine! At least one exists
            assertTrue(true);
        }

        // Done
    }

    /* US 03.02.01
    * Testing if we can remove user profiles
    */
    @Test
    public void testF_AdminPortalRemoveProfile() {
    // Set up admin account and launch admin portal
        try {
            testD_SetUpAdmin();
        } catch (NoMatchingViewException e) {
            // We're already set up, just continue
        }

        onView(withText("Browse Users")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(1).perform(click());
        onView(withId(R.id.searchUsers)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminUsersFragment");
        waitForViewToAppear(R.id.sortButtonUsersEntrants);
        waitForViewToAppear(R.id.sortButtonUsersOrganizers);
        waitForViewToAppear(R.id.sortButtonUsersAdmins);

        // Sort by organizer
        onView(withId(R.id.sortButtonUsersEntrants)).perform(click());
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        onView(withId(R.id.searchUsers)).perform(click())
                .perform(replaceText("AdminAndroidBlackBoxTestEnt"))
                .perform(closeSoftKeyboard());
        // Check if the user AdminAndroidBlackBoxTestEntrant is displayed
        try {
            onView(withText("AdminAndroidBlackBoxTestEntrant"))
                    .check(matches(isDisplayed()));
            // Success - at least one exists
        } catch (AmbiguousViewMatcherException e) {
            // Multiple found - that's fine! At least one exists
            assertTrue(true);
        }


        // We will now open a user profile
        // Click on the user AdminAndroidBlackBoxTestEntrant
        onData(anything()).inAdapterView(withId(R.id.userListView)).atPosition(0).perform(click());
        waitForViewToAppear(R.id.image_profile);;

        // Verify ProfileFragment was launched
        this.getClass().getSimpleName().matches("ProfileFragment");
        onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save)).check(matches(isDisplayed()));

        // Delete the user
        onView(withId(R.id.button_delete)).perform(click());
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete")).perform(click());

        // Since we deleted a user that wasn't ourselves, we go back browsing users
        waitForViewToAppear(R.id.userListView);
        this.getClass().getSimpleName().matches("AdminUsersFragment");

        // Done

    }

    /* US 03.07.01
    * Testing if we can remove organizer profiles
    */
    @Test
    public void testG_AdminPortalRemoveOrganizerProfile() {
// Set up admin account and launch admin portal
        try {
            testD_SetUpAdmin();
        } catch (NoMatchingViewException e) {
            // We're already set up, just continue
        }

        onView(withText("Browse Users")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(1).perform(click());
        onView(withId(R.id.searchUsers)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminUsersFragment");
        waitForViewToAppear(R.id.sortButtonUsersEntrants);
        waitForViewToAppear(R.id.sortButtonUsersOrganizers);
        waitForViewToAppear(R.id.sortButtonUsersAdmins);

        // Sort by organizer
        onView(withId(R.id.sortButtonUsersOrganizers)).perform(click());
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        onView(withId(R.id.searchUsers)).perform(click())
                .perform(replaceText("AdminAndroidBlackBoxTestOrg"))
                .perform(closeSoftKeyboard());
        // Check if the user AdminAndroidBlackBoxTestOrganizer is displayed
        try {
            onView(withText("AdminAndroidBlackBoxTestOrganizer"))
                    .check(matches(isDisplayed()));
            // Success - at least one exists
        } catch (AmbiguousViewMatcherException e) {
            // Multiple found - that's fine! At least one exists
            assertTrue(true);
        }

        // We will now open a user profile
        // Click on the user AdminAndroidBlackBoxTestOrganizer
        onData(anything()).inAdapterView(withId(R.id.userListView)).atPosition(0).perform(click());
        waitForViewToAppear(R.id.image_profile);;

        // Verify ProfileFragment was launched
        this.getClass().getSimpleName().matches("ProfileFragment");
        onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save)).check(matches(isDisplayed()));

        // Delete the user
        onView(withId(R.id.button_delete)).perform(click());
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete")).perform(click());

        // Since we deleted a user that wasn't ourselves, we go back browsing users
        waitForViewToAppear(R.id.userListView);
        this.getClass().getSimpleName().matches("AdminUsersFragment");

        // Done
    }

    /*  Adds a dummy event to the database .
     *  This test checks to make sure that the event is added to the database.
     */
    @Test
    public void testH_AddEvent() throws InterruptedException {
        String eventId = "0000adminBlackBoxTestEvent";

        Event event = new Event();
        event.setId(eventId);
        event.setOrganizerId("0000adminBlackBoxTest");
        event.setTitle("adminBlackBoxEvent");
        event.setDescription("adminBlackBoxEvent");
        event.setCategory("adminBlackBoxEvent");

        event.setPrice(0.0);
        event.setMaxAttendees(50);
        event.setMaxWaitListSize(20);
        event.setLocation("adminBlackBoxEvent");
        event.setLocationRequired(false);
        event.setSessionDuration(120);

        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        Date nextWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);

        event.setRegistrationStart(now);
        event.setRegistrationEnd(tomorrow);
        event.setStartTime(tomorrow);
        event.setEndTime(nextWeek);

        event.setEventStatus(null);
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            event.setDailyStartTime(9, 0);  // 9:00 AM
            event.setDailyEndTime(17, 0);   // 5:00 PM
        }

        event.setLocationUrl("");
        event.setPosterUrl("");
        event.setQrCodeUrl("");


        Log.d(TAG, "Event created: " + event.getTitle());

        CountDownLatch writeLatch = new CountDownLatch(1);
        boolean[] writeSuccess = {false};

        db.collection("events")
                .document(eventId)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Write Success");
                    writeSuccess[0] = true;
                    writeLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Write Failed: " + e.getMessage());
                    writeLatch.countDown();
                });

        writeLatch.await();
        assertTrue("Adding event to Firestore succeeded", writeSuccess[0]);
    }

    /* US 03.04.01
    * Testing if we can browse events
     */
    @Test
    public void testI_AdminPortalBrowseEvents() throws InterruptedException {
        // Add a dummy event
        testH_AddEvent();

        // Set up admin account and launch admin portal
        try {
            testD_SetUpAdmin();
        } catch (NoMatchingViewException e) {
            // We're already set up, just continue
        }

        onView(withText("Browse Events")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(0).perform(click());
        onView(withId(R.id.adminSearchEvents)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminEventFragment");

        onView(withId(R.id.adminSearchEvents)).perform(click())
                .perform(replaceText(""))
                .perform(typeText("adminBlackBoxEvent"))
                .perform(closeSoftKeyboard());

        // Check if the event adminBlackBoxEvent is displayed
        try {
            onView(withText("adminBlackBoxEvent"))
                    .check(matches(isDisplayed()));
        } catch (AmbiguousViewMatcherException e) {
            // Multiple found - that's fine! At least one exists
            assertTrue(true);
        }
    }

    /* US 03.01.01
     * Testing if we can remove events
     */
    @Test
    public void testJ_AdminPortalRemoveEvents() throws InterruptedException {
        boolean eventRemoved = false;

        // Add a dummy event
        testH_AddEvent();

        // Set up admin account and launch admin portal
        try {
            testD_SetUpAdmin();
        } catch (NoMatchingViewException e) {
            // We're already set up, just continue
        }

        onView(withText("Browse Events")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(0).perform(click());
        onView(withId(R.id.adminEventsRecyclerView)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminEventFragment");

        onView(withId(R.id.adminSearchEvents)).perform(click())
                .perform(replaceText(""))
                .perform(typeText("adminBlackBoxEven"))
                .perform(closeSoftKeyboard());

        // Check if the event adminBlackBoxEvent is displayed
        try {
            onView(withText("adminBlackBoxEvent"))
                    .check(matches(isDisplayed()));
        } catch (AmbiguousViewMatcherException e) {
            // Multiple found - that's fine! At least one exists
            assertTrue(true);
        }

        // Delete the event
        onView(withId(R.id.deleteButtonEvents)).perform(click());
        onView(withText("adminBlackBoxEvent")).check(matches(isDisplayed()));
        onView(withText("adminBlackBoxEvent")).perform(click());
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete")).perform(click());

        // Check if the event adminBlackBoxEvent is no longer displayed
        onView(withId(R.id.adminSearchEvents)).perform(click())
                .perform(replaceText("adminBlackBoxEven"))
                .perform(closeSoftKeyboard());
        onView(withText("adminBlackBoxEvent")).check(doesNotExist());



    }

    @Test
    public void testK_AdminPortalBrowseImagess() {
        try {
            testD_SetUpAdmin();
        } catch (NoMatchingViewException e) {
            // We're already set up, just continue
        }

        // Verify AdminMainActivity was launched
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
        onView(withText("Admin Portal")).check(matches(isDisplayed()));
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));
        onView(withText("Browse Images")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(2).perform(click());
        //onView(withId(R.id.systemImageList)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminImagesFragment");
    }

    @Test
    public void testL_AdminPortalBrowseLogs() {
        try {
            testD_SetUpAdmin();
        } catch (NoMatchingViewException e) {
            // We're already set up, just continue
        }

        // Verify AdminMainActivity was launched
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
        onView(withText("Admin Portal")).check(matches(isDisplayed()));
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));
        onView(withText("Browse Logs")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(3).perform(click());
        onView(withId(R.id.rvNotificationLogs)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminLogFragment");
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