package com.ualberta.static2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.RegistrationRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Black-box data test for registrations.
 * It verifies that registration data is correctly read from and written to the database
 * by interacting with the repository and then checking the database state directly.
 * NOTE: The Firebase Emulator Suite must be running.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationDataTest {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser testUser;
    private RegistrationRepository registrationRepository;
    private final String TEST_EVENT_ID = "test-event-456";

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator("10.0.2.2", 8080);
        auth = FirebaseAuth.getInstance();
        auth.useEmulator("10.0.2.2", 9099);
        Tasks.await(auth.signInAnonymously());
        testUser = auth.getCurrentUser();
        assertNotNull("Test user should be created", testUser);

        Event testEvent = new Event(TEST_EVENT_ID, "Data Test Event", "org-id", "2025-12-01");
        Tasks.await(firestore.collection("events").document(TEST_EVENT_ID).set(testEvent));

        registrationRepository = RegistrationRepository.getInstance();
    }

    /**
     * Test for writing data: Use the repository to register a user for an event,
     * then query the database to verify the registration document was created.
     */
    @Test
    public void testWriteRegistrationData() throws InterruptedException, ExecutionException, TimeoutException {
        final CountDownLatch latch = new CountDownLatch(1);
        registrationRepository.registerUser(TEST_EVENT_ID, testUser.getUid(), new RegistrationRepository.RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                assertNotNull("Callback should return a successful registration object", registration);
                latch.countDown();
            }
            @Override
            public void onFailure(Exception e) {
                fail("Registration should not fail: " + e.getMessage());
            }
        });

        assertTrue("Repository call timed out", latch.await(5, TimeUnit.SECONDS));

        QuerySnapshot query = Tasks.await(firestore.collection("registrations")
                .whereEqualTo("eventId", TEST_EVENT_ID)
                .whereEqualTo("entrantId", testUser.getUid())
                .limit(1).get(), 5, TimeUnit.SECONDS);

        assertEquals("One registration document should be found", 1, query.size());
        Registration savedReg = query.getDocuments().get(0).toObject(Registration.class);
        assertEquals(TEST_EVENT_ID, savedReg.getEventId());
        assertEquals(testUser.getUid(), savedReg.getEntrantId());
    }

    /**
     * Test for reading/deleting data: Create a registration, then use the repository
     * to unregister the user, and verify the document is gone.
     */
    @Test
    public void testReadAndDeleteRegistrationData() throws ExecutionException, InterruptedException, TimeoutException {
        Registration reg = new Registration("reg-to-delete", TEST_EVENT_ID, testUser.getUid());
        Tasks.await(firestore.collection("registrations").document(reg.getId()).set(reg), 5, TimeUnit.SECONDS);

        final CountDownLatch latch = new CountDownLatch(1);

        registrationRepository.unregisterUser(TEST_EVENT_ID, testUser.getUid(), new RegistrationRepository.RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                assertNull("Callback should return null on successful withdrawal", registration);
                latch.countDown();
            }
            @Override
            public void onFailure(Exception e) {
                fail("Unregister should not fail: " + e.getMessage());
            }
        });

        assertTrue("Repository call timed out", latch.await(5, TimeUnit.SECONDS));

        QuerySnapshot query = Tasks.await(firestore.collection("registrations")
                .whereEqualTo("eventId", TEST_EVENT_ID)
                .whereEqualTo("entrantId", testUser.getUid())
                .get(), 5, TimeUnit.SECONDS);

        assertTrue("Registration document should be deleted", query.isEmpty());
    }

    @After
    public void tearDown() throws ExecutionException, InterruptedException {
        if (testUser != null) {
            QuerySnapshot docs = Tasks.await(firestore.collection("registrations").whereEqualTo("entrantId", testUser.getUid()).get());
            for (DocumentSnapshot doc : docs.getDocuments()) {
                doc.getReference().delete();
            }
            testUser.delete();
        }
        Tasks.await(firestore.collection("events").document(TEST_EVENT_ID).delete());
        firestore.clearPersistence();
    }
}
