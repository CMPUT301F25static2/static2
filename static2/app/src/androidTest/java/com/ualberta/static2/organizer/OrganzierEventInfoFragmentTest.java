package com.ualberta.static2.organizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.static2.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class OrganzierEventInfoFragmentTest {

    private static final String TEST_EVENT_ID = "62iaipMyuGtQInbGrXog";
    private FragmentScenario<OrganizerEventInfoFragment> scenario;
    private EventRepository eventRepository;

    @Before
    public void setUp() {
        eventRepository = EventRepository.getInstance();
        createTestEventInDatabase();

        scenario = FragmentScenario.launchInContainer(
                OrganizerEventInfoFragment.class,
                OrganizerEventInfoFragment.newInstance(TEST_EVENT_ID).getArguments()
        );

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createTestEventInDatabase() {
        Event testEvent = new Event();
        testEvent.setId(TEST_EVENT_ID);
        testEvent.setTitle("Testing");
        testEvent.setDescription("This entry is designed for testing");
        testEvent.setEventStatus(EventStatus.UPCOMING);
        testEvent.setEndTime(new Date(System.currentTimeMillis() + 86400000));
        testEvent.setRegistrationEnd(new Date(System.currentTimeMillis() + 43200000));

        final CountDownLatch latch = new CountDownLatch(1);

//        eventRepository.addEvent(testEvent, new EventRepository.OperationCallback() {
//            @Override
//            public void onSuccess() {
//                latch.countDown();
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                latch.countDown();
//            }
//        });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // test if title is correct
        onView(withId(R.id.tv_event_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("Testing")));
        onView(withId(R.id.tv_event_update_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("Testing")));

        // test if description is correct
        onView(withId(R.id.tv_event_description))
                .check(matches(isDisplayed()))
                .check(matches(withText("This entry is designed for testing")));
        onView(withId(R.id.tv_event_update_description))
                .check(matches(isDisplayed()))
                .check(matches(withText("This entry is designed for testing")));

        // test if event status is correct
        onView(withId(R.id.tv_event_update_status))
                .check(matches(isDisplayed()))
                .check(matches(withText("UPCOMING")));

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
        String expectedEndTime = dateFormat.format(new Date(System.currentTimeMillis() + 86400000));
        String expectedRegEndTime = dateFormat.format(new Date(System.currentTimeMillis() + 43200000));


        onView(withId(R.id.tv_event_update_endTime))
                .check(matches(isDisplayed()))
                .check(matches(withText(expectedEndTime)));

        onView(withId(R.id.tv_event_update_registry_endTime))
                .check(matches(isDisplayed()))
                .check(matches(withText(expectedRegEndTime)));

    }

    @Test
    public void testEditTitle() {
        onView(withId(R.id.btn_event_update_title))
                .perform(click());

        onView(withId(R.id.et_updated_content))
                .inRoot(isDialog())
                .perform(clearText(), typeText("Updated Title"), closeSoftKeyboard());

        onView(withId(R.id.btn_confirm))
                .inRoot(isDialog())
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.tv_event_title))
                .check(matches(withText("Updated Title")));
        onView(withId(R.id.tv_event_update_title))
                .check(matches(withText("Updated Title")));
    }

    @Test
    public void testEditDescription() {
        onView(withId(R.id.btn_event_update_description))
                .perform(click());

        onView(withId(R.id.et_updated_content))
                .inRoot(isDialog())
                .perform(clearText(), typeText("Updated description for testing"), closeSoftKeyboard());

        onView(withId(R.id.btn_confirm))
                .inRoot(isDialog())
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.tv_event_description))
                .check(matches(withText("Updated description for testing")));
        onView(withId(R.id.tv_event_update_description))
                .check(matches(withText("Updated description for testing")));
    }


    @Test
    public void testCancelEditDialog() {
        onView(withId(R.id.btn_event_update_title))
                .perform(click());

        onView(withId(R.id.et_updated_content))
                .inRoot(isDialog())
                .perform(typeText("This should not be saved"), closeSoftKeyboard());

        onView(withId(R.id.btn_cancel))
                .inRoot(isDialog())
                .perform(click());

        onView(withId(R.id.tv_event_title))
                .check(matches(withText("Testing")));
    }



    @Test
    public void testDialogTitlesAreCorrect() {
        onView(withId(R.id.btn_event_update_title))
                .perform(click());
        onView(withText("Title"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.btn_cancel))
                .inRoot(isDialog())
                .perform(click());

        onView(withId(R.id.btn_event_update_description))
                .perform(click());
        onView(withText("Description"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withId(R.id.btn_cancel))
                .inRoot(isDialog())
                .perform(click());

    }
}