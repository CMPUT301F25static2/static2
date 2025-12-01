package com.ualberta.static2;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

import android.widget.Toast;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ualberta.eventlottery.MainActivity;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.utils.UserManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminMainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    private IdlingResource idlingResource;

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
    }

    @After
    public void tearDown()
        {
        Intents.release();
    }


    @Test
    public void testMainActivityLoads() {
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
    }

    @Test
    public void testSetUpEntrant() {
        testMainActivityLoads();
        onView(withId(R.id.btn_entrant))
                .perform(click());
        wait("Set Up Profile");
        onView(withText("Set Up Profile")).perform(click());
        onView(withId(R.id.et_name)).perform(typeText("AndroidBlackBoxTestEntrant"));
        onView(withId(R.id.et_email)).perform(typeText("EntrantBlackBoxTest@gmail.com"));
        onView(withId(R.id.et_phone_number)).perform(typeText("1234567890"));
        onView(withId(R.id.radio_organizer)).perform(closeSoftKeyboard());
        onView(withId(R.id.radio_entrant)).perform(click());
        onView(withId(R.id.btn_save_profile)).perform(click());
        waitForViewToAppear(R.id.myEventsButton);
    }

    @Test
    public void testSetUpOrganizer() {
        testMainActivityLoads();
        onView(withId(R.id.btn_entrant))
                .perform(click());
        wait("Set Up Profile");
        onView(withText("Set Up Profile")).perform(click());
        onView(withId(R.id.et_name)).perform(typeText("AndroidBlackBoxTestOrganizer"));
        onView(withId(R.id.et_email)).perform(typeText("OrganizerBlackBoxTest@gmail.com"));
        onView(withId(R.id.et_phone_number)).perform(typeText("1234567890"));
        onView(withId(R.id.radio_organizer)).perform(closeSoftKeyboard());
        onView(withId(R.id.radio_organizer)).perform(click());
        onView(withId(R.id.btn_save_profile)).perform(click());
    }

    @Test
    public void testSetUpAdmin() {
        testMainActivityLoads();
        onView(withId(R.id.btn_entrant))
                .perform(click());
        wait("Set Up Profile");
        onView(withText("Set Up Profile")).perform(click());
        onView(withId(R.id.et_name)).perform(typeText("AndroidBlackBoxTestAdmin"));
        onView(withId(R.id.et_email)).perform(typeText("AdminBlackBoxTest@gmail.com"));
        onView(withId(R.id.et_phone_number)).perform(typeText("1234567890"));
        onView(withId(R.id.radio_organizer)).perform(closeSoftKeyboard());
        onView(withId(R.id.radio_admin)).perform(click());
        onView(withId(R.id.btn_save_profile)).perform(click());
        waitForViewToAppear(R.id.admin_browse);
    }
    @Test
    public void testAdminButtonClick() {
        // Wait for the layout to be set
        testMainActivityLoads();

        // Click admin button
        onView(withId(R.id.btn_admin))
                .perform(click());

        // Verify AdminMainActivity was launched
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
    }

    @Test
    public void testAdminPortalOpens() {
        // Wait for the layout to be set
        waitForViewToAppear(R.id.btn_admin);
        onView(withId(R.id.btn_admin))
                .perform(click());

        // Verify AdminMainActivity was launched
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminMainActivity");
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
        onView(withText("Admin Portal")).check(matches(isDisplayed()));
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));
    }

    @Test
    public void testAdminPortalBrowseEvents() {
        waitForViewToAppear(R.id.btn_admin);
        onView(withId(R.id.btn_admin))
                .perform(click());

        // Verify AdminMainActivity was launched
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
        onView(withText("Admin Portal")).check(matches(isDisplayed()));
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));
        onView(withText("Browse Events")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(0).perform(click());
        onView(withId(R.id.adminSearchEvents)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminEventFragment");
        wait("");
        System.out.println("Test completed - inspect the app now");
    }

    @Test
    public void testAdminPortalBrowseUsers() {
        waitForViewToAppear(R.id.btn_admin);
        onView(withId(R.id.btn_admin))
                .perform(click());

        // Verify AdminMainActivity was launched
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
        onView(withText("Admin Portal")).check(matches(isDisplayed()));
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));
        onView(withText("Browse Users")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(1).perform(click());
        onView(withId(R.id.searchUsers)).check(matches(isDisplayed()));
        this.getClass().getSimpleName().matches("AdminUsersFragment");

    }

    @Test
    public void testAdminBrowseUsersEntrants() {
        testAdminPortalBrowseUsers();
        waitForViewToAppear(R.id.sortButtonUsersEntrants);
        onView(withId(R.id.sortButtonUsersEntrants)).perform(click());
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        wait("");
        System.out.println("Test completed - inspect the app now");

    }

    @Test
    public void testAdminBrowseUsersOrganizers() {
        testAdminPortalBrowseUsers();
        onView(withId(R.id.sortButtonUsersOrganizers)).perform(click());
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        wait("");
        System.out.println("Test completed - inspect the app now");

    }

    @Test
    public void testAdminBrowseUsersAdmins() {
        testAdminPortalBrowseUsers();
        onView(withId(R.id.sortButtonUsersAdmins)).perform(click());
        onView(withId(R.id.userListView)).check(matches(isDisplayed()));
        wait("");
        System.out.println("Test completed - inspect the app now");

    }

    @Test
    public void testAdminOpenUserProfile() {
        testSetUpEntrant();
        pressBack();
        testAdminPortalBrowseUsers();
        onData(anything()).inAdapterView(withId(R.id.userListView)).atPosition(0).perform(click());
        wait("");
        wait("");
        System.out.println("Test completed - inspect the app now");
    }

    @Test
    public void testAdminOpenUserProfileDelete() {
        testAdminPortalBrowseUsers();
        onData(anything()).inAdapterView(withId(R.id.userListView)).atPosition(0).perform(click());
        onView(withId(R.id.button_delete)).perform(click());
        //wait("");
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete")).perform(click());
        wait("");

        System.out.println("Test completed - inspect the app now");

    }


    @Test
    public void testAdminPortalBrowseImagess() {
        waitForViewToAppear(R.id.btn_admin);
        onView(withId(R.id.btn_admin))
                .perform(click());

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
    public void testAdminPortalBrowseLogs() {
        waitForViewToAppear(R.id.btn_admin);
        onView(withId(R.id.btn_admin))
                .perform(click());

        // Verify AdminMainActivity was launched
        Intents.intended(IntentMatchers.hasComponent(AdminMainActivity.class.getName()));
        onView(withText("Admin Portal")).check(matches(isDisplayed()));
        onView(withId(R.id.admin_browse)).check(matches(isDisplayed()));
        onView(withText("Browse Logs")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.admin_browse)).atPosition(3).perform(click());
        onView(withId(R.id.systemLogList)).check(matches(isDisplayed()));
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