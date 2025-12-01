package com.ualberta.static2.entrant;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.eventlottery.ui.home.entrant.EventDetailsFragment;
import com.ualberta.eventlottery.ui.home.entrant.HomeFragment;
import com.ualberta.static2.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests for Entrant User Stories
 * Stories Tested:
 * US-01.01.01, 02, 03, 04
 * US-01.06.02
 */
@RunWith(AndroidJUnit4.class)
public class EntrantMainActivityTest {
    private CountingIdlingResource idlingResource = new CountingIdlingResource("EntrantMainActivityTest");

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION);

    @Before
    public void setup() {
        IdlingRegistry idlingRegistry = IdlingRegistry.getInstance();
        idlingRegistry.register(idlingResource);
        idlingRegistry.register(EventAdapter.idlingResource);
        idlingRegistry.register(EventDetailsFragment.idlingResource);
    }

    @After
    public void tearDown() {
        IdlingRegistry idlingRegistry = IdlingRegistry.getInstance();
        idlingRegistry.unregister(HomeFragment.idlingResource);
        idlingRegistry.unregister(EventAdapter.idlingResource);
        idlingRegistry.unregister(EventDetailsFragment.idlingResource);
    }

    @Test
    public void testActivityLaunchesSuccessfully() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);


        onView(withId(R.id.myEventsButton))
                .check(matches(isDisplayed()));
    }

    /**
     * US 01.01.01 As an entrant, I want to join the waiting list for a specific event
     */
    @Test
    public void testJoinWaitlist() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(allOf(withId(R.id.availableEventsButton), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        try {
            onView(allOf(CustomMatchers.first(withText("Register")), isDisplayed()))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException nmve) {
            // Check if there's a withdraw button we can click to make this test pass
            onView(allOf(CustomMatchers.first(withText("Withdraw")), isDisplayed()))
                    .perform(click());
        }

        onView(CustomMatchers.first(withText("Register")))
                .perform(click());

        // Delay the exit for easy visual inspection of the test.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
     */
    @Test
    public void testLeaveWaitlist() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(allOf(withId(R.id.availableEventsButton), isDisplayed()))
               .check(matches(isDisplayed()));

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        try {
            onView(allOf(CustomMatchers.first(withText("Withdraw")), isDisplayed()))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException nmve) {
            // Check if there's a register button we can click to make this test pass
            onView(allOf(CustomMatchers.first(withText("Register")), isDisplayed()))
                    .perform(click());
        }

        onView(CustomMatchers.first(withText("Withdraw")))
                .perform(click());


        // Delay the exit for easy visual inspection of the test.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * US 01.01.03 As an entrant, I want to be able to see a list of events that I can join the waiting list for.
     */
    @Test
    public void testEntrantIsAbleToSeeEventsTheyCanRegisterFor() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .check(matches(isDisplayed()))
                .perform(click());

        // Delay the exit for easy visual inspection of the test.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * US 01.01.04 As an entrant I want to filter my interests and availability
     */

    /**
     * Tests Opening Filtering options
     */
    @Test
    public void testOpenFilterOptions() {
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(allOf(withText("Category: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Filters"))
                .perform(click());

        onView(allOf(withText("Days: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Selections"))
                .perform(click());

        onView(allOf(withText("Start Time: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Selections"))
                .perform(click());

        try{
            Thread.sleep(2000);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests Apply the Sport Filter
     */
    @Test
    public void testFilterByCategorySport(){
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        onView(allOf(withText("Category: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("sports"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(withText("sports"))
                .perform(click());

        onView(withText("Apply Filters"))
                .perform(click());

        onView(allOf(withText("Category: (1)"), isDisplayed()))
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests applying multiple days of the week
     */
    @Test
    public void testFilterByMultipleDaysOfWeek(){
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        onView(allOf(withText("Days: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("Mon"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("Wed"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("Fri"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Selections"))
                .perform(click());

        onView(allOf(withText("Days: M W F"), isDisplayed()))
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Tests that filters are persistent between My Events and Available Events
     */
    @Test
    public void testFiltersApplyToMyEventsAndAvailableEvents() {

        //Lauching Activity
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());


        //Applying Filters to Available Events
        onView(allOf(withText("Category: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("sports"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Filters"))
                .perform(click());

        //Checks that filters are properly displayed
        onView(allOf(withText("Category: (1)"), isDisplayed()))
                .check(matches(isDisplayed()));

        //Tests Applying Days of the week filter
        onView(allOf(withText("Days: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("Sat"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("Sun"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Selections"))
                .perform(click());

        //Checks that all texts match with expected behavior
        onView(allOf(withText("Days: S S"), isDisplayed()))
                .check(matches(isDisplayed()));

        //Applies start time filter
        onView(allOf(withText("Start Time: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withText("Morning (6am - 12pm)"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Selections"))
                .perform(click());

        onView(allOf(withText("Start Time: Morning"), isDisplayed()))
                .check(matches(isDisplayed()));

        //Navigate away from available event screen
        onView(withId(R.id.myEventsButton))
                .perform(click());

        onView(allOf(withText("Category: (1)"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Days: S S"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withText("Start Time: Morning"), isDisplayed()))
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests ability to stop a Filter Selection
     */
    @Test
    public void testCancelFilter(){

        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        onView(allOf(withText("Category: Any"), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Apply Filters"))
                .perform(click());

        onView(allOf(withText("Category: Any"), isDisplayed()))
                .check(matches(isDisplayed()));


        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * US 01.06.02 As an entrant, I want to be able to sign up for an event from the event details
     */
    @Test
    public void testEntrantSignUpForEventFromEventDetails() throws InterruptedException {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .check(matches(isDisplayed()))
                .perform(click());

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(2000, TimeUnit.MILLISECONDS);

        try {
            onView(allOf(CustomMatchers.first(allOf(withText("Entrants:"), withParent(hasSibling(hasDescendant(withText("Register")))))), isDisplayed()))
                    .check(matches(isDisplayed()));
        } catch(NoMatchingViewException e) {
            // Check if there's a withdraw button we can click to make this test pass
            onView(allOf(CustomMatchers.first(withText("Withdraw")), isDisplayed()))
                    .perform(click());
        }

        onView(CustomMatchers.first(allOf(withText("Entrants:"), withParent(hasSibling(hasDescendant(withText("Register")))))))
                .perform(click());

        latch.await(2000, TimeUnit.MILLISECONDS);

        onView(allOf(withText("Register"), isDisplayed()))
                .check(matches(isDisplayed()));

        onView(withText("Register"))
                .perform(click());

        latch.await(1000, TimeUnit.MILLISECONDS);
    }
}
