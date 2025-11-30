package com.ualberta.static2.organizer;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.os.Build;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.static2.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mockito.Mockito;

/**
 * Specifically focuses on viewing event waiting lists (US 02.01.01).
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerRegistrationWhiteboxTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    // Mock repositories
    private EventRepository mockEventRepository;
    private RegistrationRepository mockRegistrationRepository;

    // Mocked data
    private String testEventId = "mock_event_id";
    private String organizerId = "testOrganizerId_reg_" + UUID.randomUUID().toString();
    private List<String> testEntrantIds = new ArrayList<>();
    private List<String> testRegistrationIds = new ArrayList<>();
    private List<String> testEntrantUsernames = new ArrayList<>();

    private static final int NUM_WAITING_ENTRANTS = 3; // Number of test entrants to create

    // Mocked event and user data
    private Event mockEvent;
    private List<User> mockUsers;
    private List<Registration> mockRegistrations;

    // Mocked data for testing
    private Map<String, Object> mockEventMap;
    private Map<String, Object> mockUserMap;
    private Map<String, Object> mockRegistrationMap;

    /**
     * Sets up the test environment before each test method.
     * This involves creating mock data for events, users, and registrations
     */
    @Before
    public void setUp() throws InterruptedException {
        // Initialize mocks
        mockEventRepository = Mockito.mock(EventRepository.class);
        mockRegistrationRepository = Mockito.mock(RegistrationRepository.class);

        // Create mock event data
        mockEvent = new Event();
        mockEvent.setId(testEventId);
        mockEvent.setTitle("Test Event for Waitlist");
        mockEvent.setDescription("Description for waitlist test event");
        mockEvent.setMaxAttendees(10);
        mockEvent.setCategory("Test");
        mockEvent.setOrganizerId(organizerId);
        mockEvent.setPrice(0.0);
        mockEvent.setStartTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        mockEvent.setEndTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 2));
        mockEvent.setRegistrationStart(new Date(System.currentTimeMillis() - 1000 * 60 * 60));
        mockEvent.setRegistrationEnd(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 23));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mockEvent.setDailyStartTime(LocalTime.of(9, 0));
            mockEvent.setDailyEndTime(LocalTime.of(17, 0));
        }
        mockEvent.setConfirmedAttendees(0); // No confirmed attendees initially
        mockEvent.setPosterUrl("https://example.com/poster.jpg");
        mockEvent.setQrCodeUrl("https://example.com/qr.png");
        mockEvent.setLocation("Test Location");
        mockEvent.setLocationRequired(true);
        mockEvent.setLocationUrl("https://maps.google.com/?q=TestLocation");

        // Create mock user data
        mockUsers = new ArrayList<>();
        for (int i = 0; i < NUM_WAITING_ENTRANTS; i++) {
            String username = "testEntrant_" + i + "_" + UUID.randomUUID().toString().substring(0, 4);
            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setName(username);
            user.setEmail(username + "@example.com");
            user.setPhone("123-456-7890");
            mockUsers.add(user);
            testEntrantUsernames.add(username);
        }

        // Create mock registration data
        mockRegistrations = new ArrayList<>();
        for (int i = 0; i < NUM_WAITING_ENTRANTS; i++) {
            Registration registration = new Registration();
            registration.setId(UUID.randomUUID().toString());
            registration.setEventId(testEventId);
            registration.setEntrantId(mockUsers.get(i).getUserId());
            registration.setStatus(EntrantRegistrationStatus.WAITING);
            registration.setRegisteredAt(new Date());
            mockRegistrations.add(registration);
        }

        Mockito.when(mockEventRepository.eventToMap(Mockito.any())).thenReturn(mockEventMap);
        Mockito.when(mockRegistrationRepository.registrationToMap(Mockito.any())).thenReturn(mockRegistrationMap);

        // Verify that the OrganizerMainActivity's fragment container is displayed,
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }


    /**
     * US 02.01.01: Tests if an organizer can view the list of entrants who joined their event's waiting list.
     *
     */
    @Test
    public void testOrganizerCanViewWaitingEntrantsList() {
        // Verify that the list view is displayed
        onView(withId(R.id.lv_organzier_event_list)).check(matches(isDisplayed()));

        // 1. Click on the created test event in the main event list.
        // We find the event by matching its title, which is more robust than a fixed position.
        onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0)
                .perform(click());

        // Verify that the event details screen is displayed after clicking.
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.btn_event_showcase)).perform(click());

        // Verify that the EntrantsFragment (or the screen displaying entrants) is displayed.
        onView(ViewMatchers.withId(R.id.fragment_entrants_conatiner)).check(matches(isDisplayed()));

        // 2. Click on the button or tab that leads to the waiting entrants list.
        onView(ViewMatchers.withId(R.id.btn_entrants_waiting)).perform(click());

        // 3. Verify that the list view/recycler view of entrants is displayed within the fragment.
        onView(ViewMatchers.withId(R.id.lv_event_entrant_list)).check(matches(isDisplayed()));

        // 4. Verify that each expected waiting entrant's username is displayed in the list.
        for (String expectedUsername : testEntrantUsernames) {
            onView(allOf(withId(R.id.tv_entrant_name), withText(expectedUsername)))
                    .check(matches(isDisplayed()));
        }

    }
}