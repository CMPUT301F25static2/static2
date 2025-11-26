package com.ualberta.static2.entrant;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.eventlottery.ui.home.entrant.HomeFragment;
import com.ualberta.static2.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EntrantMainActivityTest {
    private CountingIdlingResource idlingResource = new CountingIdlingResource("EntrantMainActivityTest");

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS);

    @Before
    public void setup() {
        IdlingRegistry idlingRegistry = IdlingRegistry.getInstance();
        idlingRegistry.register(idlingResource);
        idlingRegistry.register(EventAdapter.idlingResource);
    }

    @After
    public void tearDown() {
        IdlingRegistry idlingRegistry = IdlingRegistry.getInstance();
        idlingRegistry.unregister(HomeFragment.idlingResource);
        idlingRegistry.register(EventAdapter.idlingResource);
    }

    @Test
    public void testActivityLaunchesSuccessfully() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);


        onView(withId(R.id.myEventsButton))
                .check(matches(isDisplayed()));
    }

    // US 01.01.03 As an entrant, I want to be able to see a list of events that I can join the waiting list for.
    @Test
    public void testEntrantIsAbleToSeeEventsTheyCanRegisterFor() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .check(matches(isDisplayed()));
    }

    //US 01.01.01 As an entrant, I want to join the waiting list for a specific event
    @Test
    public void testJoinWaitlist() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);

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

    // US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
    @Test
    public void testLeaveWaitlist() {
        // test if the activity launches successfully
        ActivityScenario.launch(EntrantMainActivity.class);

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

    //US 01.01.04 As an entrant I want to filter my interests and availability
    @Test
    public void testOpenFilterOptions() {
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.filterGroup))
                .perform(click());

        try {
            onView(allOf(CustomMatchers.first(withText("Filter")), isDisplayed()))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException nmve) {
            // Check if there's a Filter button we can click to make this test pass
            onView(allOf(CustomMatchers.first(withText("Filter")), isDisplayed()))
                    .perform(click());
        }

        onView(CustomMatchers.first(withText("Filter")))
                .perform(click());

        try{
            Thread.sleep(2000);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFilterByCategorySport(){
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        onView(withId(R.id.filterGroup))
                .perform(click());

        try {
            onView(allOf(CustomMatchers.first(withText("Sports")), isDisplayed()))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException nmve) {
            // Check if there's a Filter button we can click to make this test pass
            onView(allOf(CustomMatchers.first(withText("Filter")), isDisplayed()))
                    .perform(click());
        }

        onView(CustomMatchers.first(withText("Sports")))
                .perform(click());

        onView(CustomMatchers.first(withText("Apply")))
                .perform(click());
        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFilterByMultipleCategories(){
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        onView(withId(R.id.filterGroup))
                .perform(click());

        try {
            onView(allOf(CustomMatchers.first(withText("Sports")), isDisplayed()))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException nmve) {
            // Check if there's a Filter button we can click to make this test pass
            onView(allOf(CustomMatchers.first(withText("Filter")), isDisplayed()))
                    .perform(click());
        }

        onView(CustomMatchers.first(withText("Sports")))
                .perform(click());

        onView(CustomMatchers.first(withText("Music")))
                .perform(click());

        onView(CustomMatchers.first(withText("Apply")))
                .perform(click());


        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testFilterStaysDuringNav() {
        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        onView(withId(R.id.filterGroup))
                .perform(click());

        onView(CustomMatchers.first(withText("Sports")))
                .perform(click());

        onView(CustomMatchers.first(withText("Music")))
                .perform(click());


        //Navigate away from available event screen
        onView(withId(R.id.myEventsButton))
                .perform(click());

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testCancelFilter(){

        ActivityScenario.launch(EntrantMainActivity.class);

        onView(withId(R.id.availableEventsButton))
                .perform(click());

        onView(withId(R.id.filterGroup))
                .perform(click());

        onView(CustomMatchers.first(withText("Sports")))
                .perform(click());

        onView(CustomMatchers.first(withText("Cancel")))
                .perform(click());

        onView(withId(R.id.availableEventsButton))
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(2000);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

}