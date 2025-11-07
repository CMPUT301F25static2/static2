package com.ualberta.static2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.ui.profile.ProfileViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileDataTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser testUser;
    private ProfileViewModel profileViewModel;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        firestore = FirebaseFirestore.getInstance();
        firestore.useEmulator("10.0.2.2", 8080);
        auth = FirebaseAuth.getInstance();
        auth.useEmulator("10.0.2.2", 9099);

        Tasks.await(auth.signInAnonymously());
        testUser = auth.getCurrentUser();
        assertNotNull("Test user should be created", testUser);

        profileViewModel = new ProfileViewModel();
    }

    @Test
    public void testWriteProfileData() throws InterruptedException, ExecutionException, TimeoutException {
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<String> observer = value -> {
            if ("Harvey Specter".equals(value)) {
                latch.countDown();
            }
        };
        profileViewModel.getName().observeForever(observer);

        profileViewModel.setName("Harvey Specter");
        profileViewModel.setEmail("harvey@pearsonhardman.com");
        profileViewModel.setPhone("555-0100");
        profileViewModel.setFavoriteRecCenter("Law Library");

        profileViewModel.saveProfileToFirebase(testUser.getUid());

        assertTrue("Timed out waiting for LiveData to update after save", latch.await(5, TimeUnit.SECONDS));

        assertEquals("Harvey Specter", profileViewModel.getName().getValue());
        assertEquals("harvey@pearsonhardman.com", profileViewModel.getEmail().getValue());
        assertEquals("555-0100", profileViewModel.getPhone().getValue());
        assertEquals("Law Library", profileViewModel.getFavoriteRecCenter().getValue());

        DocumentSnapshot document = Tasks.await(firestore.collection("users").document(testUser.getUid()).get());
        User savedUser = document.toObject(User.class);
        assertEquals("Harvey Specter", savedUser.getName());

        profileViewModel.getName().removeObserver(observer);
    }

    @Test
    public void testReadProfileData() throws ExecutionException, InterruptedException, TimeoutException {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Donna Paulsen");
        userData.put("email", "donna@pearsonhardman.com");
        userData.put("phone", "555-0101");
        userData.put("favRecCenter", "Front Desk");
        Tasks.await(firestore.collection("users").document(testUser.getUid()).set(userData), 5, TimeUnit.SECONDS);

        final CountDownLatch latch = new CountDownLatch(1);
        Observer<String> observer = value -> {
            if ("Donna Paulsen".equals(value)) {
                latch.countDown();
            }
        };
        profileViewModel.getName().observeForever(observer);

        profileViewModel.loadProfileFromFirebase(testUser.getUid());

        assertTrue("Timed out waiting for LiveData to load", latch.await(5, TimeUnit.SECONDS));

        assertEquals("Donna Paulsen", profileViewModel.getName().getValue());
        assertEquals("donna@pearsonhardman.com", profileViewModel.getEmail().getValue());
        assertEquals("555-0101", profileViewModel.getPhone().getValue());
        assertEquals("Front Desk", profileViewModel.getFavoriteRecCenter().getValue());

        profileViewModel.getName().removeObserver(observer);
    }

    @After
    public void tearDown() throws ExecutionException, InterruptedException {
        if (testUser != null) {
            Tasks.await(firestore.collection("users").document(testUser.getUid()).delete());
            testUser.delete();
        }
        firestore.clearPersistence();
    }
}
