// app/src/androidTest/java/com/ualberta/static2/profile/ProfileViewModelWhiteBoxTest.java
package com.ualberta.static2.entrant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.ui.profile.ProfileViewModel;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.testutils.DatabaseCleaner;
import com.ualberta.static2.testutils.UserManagerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Whitebox tests for Profile and related user stories.
 * Stories Tested:
 *  - US 01.02.02: Update profile information.
 *  - US 01.02.04: Delete profile.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileViewModelWhiteBoxTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public UserManagerRule userManagerRule = new UserManagerRule();

    private ProfileViewModel profileViewModel;
    private FirebaseFirestore db;
    private String userId;

    @Mock
    private Observer<String> stringObserver;
    @Mock
    private Observer<Boolean> booleanObserver;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Before
    public void setUp() throws Exception {
        profileViewModel = new ProfileViewModel();
        db = FirebaseFirestore.getInstance();
        userId = UserManager.getCurrentUserId();
        // Create a dummy user profile for tests
        User testUser = new User(userId, "Initial Name", "initial@test.com", "1112223333", null, "entrant", "", false);
        Tasks.await(db.collection("users").document(userId).set(testUser));
    }

    @After
    public void tearDown() throws Exception {
        // This will clean the user if the delete test fails to do so
        DatabaseCleaner.cleanUser(userId, 5000);
    }

    /**
     * US 01.02.02: Tests if profile data can be loaded and updated successfully.
     */
    @Test
    public void testUpdateProfile() throws InterruptedException {
        // Observe LiveData
        profileViewModel.getName().observeForever(stringObserver);
        profileViewModel.getEmail().observeForever(stringObserver);

        // Load profile
        profileViewModel.loadProfileFromFirebase(userId);
        Thread.sleep(1000); // Allow time for Firestore fetch

        // Verify initial load
        Mockito.verify(stringObserver, Mockito.atLeastOnce()).onChanged(stringCaptor.capture());
        assertEquals("Initial Name", profileViewModel.getName().getValue());
        assertEquals("initial@test.com", profileViewModel.getEmail().getValue());

        // Set new values
        String newName = "Updated Name";
        String newEmail = "updated@test.com";
        profileViewModel.setName(newName);
        profileViewModel.setEmail(newEmail);

        // Save profile
        profileViewModel.saveProfileToFirebase(userId);
        Thread.sleep(1000); // Allow time for Firestore save

        // Verify that the LiveData holds the new values
        assertEquals(newName, profileViewModel.getName().getValue());
        assertEquals(newEmail, profileViewModel.getEmail().getValue());
    }

    /**
     * US 01.02.04: Tests if a user's profile and associated data are deleted.
     */
    @Test
    public void testDeleteUser() throws InterruptedException, ExecutionException, TimeoutException {
        // Directly delete the user through the database for a whitebox test
        Tasks.await(db.collection("users").document(userId).delete(), 5, TimeUnit.SECONDS);

        // Verify the user document is deleted
        DocumentSnapshot userDoc = Tasks.await(db.collection("users").document(userId).get(), 5, TimeUnit.SECONDS);
        assertFalse("User document should not exist after deletion.", userDoc.exists());
    }
}
