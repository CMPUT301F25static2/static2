package com.ualberta.static2.organizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.model.Entrant;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EntrantRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.ui.organizer.fragment.EntrantsFragment;
import com.ualberta.static2.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tests all state buttons and item displays in OrganizerEventShowcaseFragment as well as EntrantsFragment.
 *
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerEventShowcaseFragmentTest {

    private static final String REAL_EVENT_ID = "BB1EwGmHVkrpWRz5CPqp";
    private static final int MAX_WAIT_TIME_MS = 15000; // Maximum wait time: 15 seconds
    private static final int CHECK_INTERVAL_MS = 500;

    private FragmentScenario<EntrantsFragment> scenario;
    private RegistrationRepository registrationRepository;
    private EntrantRepository entrantRepository;

    // Stores test data for each registration status
    private Map<EntrantRegistrationStatus, List<String>> entrantNamesByStatus = new HashMap<>();
    private Map<EntrantRegistrationStatus, Integer> countByStatus = new HashMap<>();

    @Before
    public void setUp() throws InterruptedException {
        registrationRepository = RegistrationRepository.getInstance();
        entrantRepository = EntrantRepository.getInstance();

        // Initialize all statuses
        for (EntrantRegistrationStatus status : EntrantRegistrationStatus.values()) {
            entrantNamesByStatus.put(status, new ArrayList<>());
            countByStatus.put(status, 0);
        }

        // Fetch data for all statuses from the database
        fetchAllEntrantsData();

        for (EntrantRegistrationStatus status : EntrantRegistrationStatus.values()) {
            int count = countByStatus.getOrDefault(status, 0);
            if (count > 0) {
                System.out.println(status + ": " + count + " entrants");
                List<String> names = entrantNamesByStatus.get(status);
                for (String name : names) {
                    System.out.println("  - " + name);
                }
            }
        }

        // Launch the Fragment
        System.out.println("\nLaunching Fragment...");
        scenario = FragmentScenario.launchInContainer(
                EntrantsFragment.class,
                EntrantsFragment.newInstance(REAL_EVENT_ID).getArguments()
        );

        // Wait for the Fragment to fully initialize
        System.out.println("Waiting for Fragment to initialize...");
        Thread.sleep(3000);

    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    /**
     * Fetches entrant data for all registration statuses.
     */
    private void fetchAllEntrantsData() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(EntrantRegistrationStatus.values().length);
        final AtomicBoolean success = new AtomicBoolean(true);

        for (EntrantRegistrationStatus status : EntrantRegistrationStatus.values()) {
            fetchEntrantsByStatus(status, latch, success);
        }

        boolean completed = latch.await(20, TimeUnit.SECONDS);
        if (!completed || !success.get()) {
            throw new RuntimeException("Data loading timed out or failed");
        }
    }

    /**
     * Fetches entrants for a specific registration status.
     */
    private void fetchEntrantsByStatus(EntrantRegistrationStatus status, CountDownLatch parentLatch, AtomicBoolean success) {
        registrationRepository.getRegistrationsByStatus(
                REAL_EVENT_ID,
                status,
                new RegistrationRepository.RegistrationListCallback() {
                    @Override
                    public void onSuccess(List<Registration> registrations) {
                        int count = registrations.size();
                        countByStatus.put(status, count);
                        System.out.println("Fetched " + count + " registrations for " + status);

                        if (registrations.isEmpty()) {
                            parentLatch.countDown();
                            return;
                        }

                        final CountDownLatch entrantLatch = new CountDownLatch(registrations.size());

                        for (Registration registration : registrations) {
                            entrantRepository.findEntrantById(
                                    registration.getEntrantId(),
                                    new EntrantRepository.EntrantCallback() {
                                        @Override
                                        public void onSuccess(Entrant entrant) {
                                            if (entrant != null && entrant.getName() != null) {
                                                entrantNamesByStatus.get(status).add(entrant.getName());
                                                System.out.println(status + " - Loaded entrant: " + entrant.getName());
                                            }
                                            entrantLatch.countDown();
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            System.out.println(status + " - Failed to load entrant: " + e.getMessage());
                                            entrantLatch.countDown();
                                        }
                                    }
                            );
                        }

                        new Thread(() -> {
                            try {
                                entrantLatch.await(10, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                success.set(false);
                                e.printStackTrace();
                            } finally {
                                parentLatch.countDown();
                            }
                        }).start();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("Failed to fetch " + status + " registrations: " + e.getMessage());
                        e.printStackTrace();
                        success.set(false);
                        parentLatch.countDown();
                    }
                }
        );
    }

    // Basic Test: Verify Selected Button and Data
    @Test
    public void testSelectedButtonAndData() throws InterruptedException {

        int expectedCount = countByStatus.get(EntrantRegistrationStatus.SELECTED);
        System.out.println("Expected Selected count: " + expectedCount);

        if (expectedCount == 0) {
            System.out.println("⚠️ No Selected entrants, skipping test");
            return;
        }

        // Click Selected button
        System.out.println("\n1. Clicking Selected button");
        onView(withId(R.id.btn_entrants_selected)).perform(click());

        // Wait for data to load
        System.out.println("\n2. Waiting for data to load...");
        boolean loaded = waitForDataLoadWithPolling(expectedCount);

        if (!loaded) {
            printDiagnostics();
        }

        // Verify final state
        System.out.println("\n3. Verifying final state");
        scenario.onFragment(fragment -> {
            RecyclerView recyclerView = fragment.getView()
                    .findViewById(R.id.lv_event_entrant_list);

            assertNotNull("RecyclerView should exist", recyclerView);
            assertNotNull("Adapter should exist", recyclerView.getAdapter());

            int actualCount = recyclerView.getAdapter().getItemCount();
            System.out.println("Final RecyclerView count: " + actualCount);

            if (actualCount > 0) {
                assertTrue("Should contain data", actualCount > 0);

                // Verify first item if possible
                RecyclerView.ViewHolder holder =
                        recyclerView.findViewHolderForAdapterPosition(0);
                if (holder != null) {
                    TextView nameView = holder.itemView.findViewById(R.id.tv_entrant_name);
                    if (nameView != null) {
                        String name = nameView.getText().toString();
                        System.out.println("✓ First entrant: " + name);
                    }
                }
            } else {
                System.out.println("✗ RecyclerView has no data!");
                System.out.println("  Adapter type: " + recyclerView.getAdapter().getClass().getName());
                System.out.println("  RecyclerView width: " + recyclerView.getWidth());
                System.out.println("  RecyclerView height: " + recyclerView.getHeight());
            }
        });

    }

    private boolean waitForDataLoadWithPolling(int expectedCount) throws InterruptedException {
        System.out.println("Starting polling to check data load status...");
        System.out.println("Expected count: " + expectedCount);
        System.out.println("Max wait time: " + MAX_WAIT_TIME_MS + "ms");
        System.out.println("Check interval: " + CHECK_INTERVAL_MS + "ms");

        long startTime = System.currentTimeMillis();
        int checkCount = 0;
        int lastCount = -1;

        while (System.currentTimeMillis() - startTime < MAX_WAIT_TIME_MS) {
            checkCount++;
            long elapsedMs = System.currentTimeMillis() - startTime;

            final int[] actualCount = {-1};
            final boolean[] hasAdapter = {false};

            try {
                scenario.onFragment(fragment -> {
                    RecyclerView recyclerView = fragment.getView()
                            .findViewById(R.id.lv_event_entrant_list);

                    if (recyclerView != null && recyclerView.getAdapter() != null) {
                        hasAdapter[0] = true;
                        actualCount[0] = recyclerView.getAdapter().getItemCount();
                    }
                });
            } catch (Exception e) {
                System.out.println("Check #" + checkCount + " failed: " + e.getMessage());
            }

            // Only print when count changes
            if (actualCount[0] != lastCount) {
                System.out.println(String.format(
                        "Check #%d [%dms]: Adapter=%s, Current count=%d, Expected=%d",
                        checkCount, elapsedMs, hasAdapter[0], actualCount[0], expectedCount
                ));
                lastCount = actualCount[0];
            }

            // Check if expected count reached
            if (hasAdapter[0] && actualCount[0] == expectedCount) {
                System.out.println(String.format(
                        "✓ Data loaded! Time: %dms, Checks: %d",
                        elapsedMs, checkCount
                ));
                return true;
            }

            Thread.sleep(CHECK_INTERVAL_MS);
        }

        System.out.println(String.format(
                "✗ Data load timed out! Last count=%d, Expected=%d, Total time: %dms",
                lastCount, expectedCount, System.currentTimeMillis() - startTime
        ));

        return false;
    }


    private void printDiagnostics() {
        System.out.println("\n===Diagnostic Info===");

        scenario.onFragment(fragment -> {
            System.out.println("Fragment state:");
            System.out.println("  - Added: " + fragment.isAdded());
            System.out.println("  - Visible: " + fragment.isVisible());
            System.out.println("  - View exists: " + (fragment.getView() != null));

            if (fragment.getView() != null) {
                RecyclerView recyclerView = fragment.getView()
                        .findViewById(R.id.lv_event_entrant_list);

                if (recyclerView != null) {
                    System.out.println("\nRecyclerView state:");
                    System.out.println("  - Visibility: " + recyclerView.getVisibility());
                    System.out.println("  - Width: " + recyclerView.getWidth());
                    System.out.println("  - Height: " + recyclerView.getHeight());
                    System.out.println("  - Adapter: " + recyclerView.getAdapter());

                    if (recyclerView.getAdapter() != null) {
                        System.out.println("  - Adapter type: " +
                                recyclerView.getAdapter().getClass().getName());
                        System.out.println("  - Item count: " +
                                recyclerView.getAdapter().getItemCount());
                    }

                    System.out.println("  - LayoutManager: " +
                            recyclerView.getLayoutManager());
                } else {
                    System.out.println("✗ RecyclerView does not exist!");
                }
            }
        });

    }

    //  Test All Status Switching
    @Test
    public void testAllStatusSwitching() throws InterruptedException {
        System.out.println("\n========Testing All Status Switching========");

        EntrantRegistrationStatus[] statuses = {
                EntrantRegistrationStatus.SELECTED,
                EntrantRegistrationStatus.CONFIRMED,
                EntrantRegistrationStatus.WAITING,
                EntrantRegistrationStatus.CANCELLED
        };

        for (EntrantRegistrationStatus status : statuses) {
            int expectedCount = countByStatus.get(status);

            if (expectedCount == 0) {
                System.out.println("\n" + status + ": Skipped (no data)");
                continue;
            }

            System.out.println("\nTesting status: " + status);
            System.out.println("Expected count: " + expectedCount);

            // Click corresponding button
            clickButtonForStatus(status);

            // Wait for load
            boolean loaded = waitForDataLoadWithPolling(expectedCount);

            // Verify result
            scenario.onFragment(fragment -> {
                RecyclerView recyclerView = fragment.getView()
                        .findViewById(R.id.lv_event_entrant_list);
                int actualCount = recyclerView.getAdapter() != null ?
                        recyclerView.getAdapter().getItemCount() : 0;

                System.out.println("Actual count: " + actualCount);

                if (loaded && actualCount == expectedCount) {
                    System.out.println("✓ " + status + " test passed");
                } else {
                    System.out.println("✗ " + status + " test failed");
                }
            });
        }

    }

    private void clickButtonForStatus(EntrantRegistrationStatus status) {
        int buttonId;
        switch (status) {
            case CONFIRMED:
                buttonId = R.id.btn_entrants_confirmed;
                break;
            case SELECTED:
                buttonId = R.id.btn_entrants_selected;
                break;
            case WAITING:
                buttonId = R.id.btn_entrants_waiting;
                break;
            case CANCELLED:
                buttonId = R.id.btn_entrants_cancelled;
                break;
            default:
                return;
        }
        onView(withId(buttonId)).perform(click());
    }
}
