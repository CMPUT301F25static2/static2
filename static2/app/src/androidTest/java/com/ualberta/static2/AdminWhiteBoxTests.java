package com.ualberta.static2;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.test.InstrumentationRegistry.getContext;

import android.os.Build;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.repository.EntrantRepository;
import com.ualberta.eventlottery.ui.adminUsers.UserAdapter;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.eventlottery.ui.profile.ProfileFragment;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.sql.ConnectionEventListener;

@RunWith(AndroidJUnit4.class)
public class AdminWhiteBoxTests {

    private static final String TAG = "AdminWhiteBoxTests";
    private FirebaseFirestore db;

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Setup complete");
    }

    /*  Adds a dummy user to the database .
     *   This test checks to make sure that the user is added to the database.
     */
    @Test
    public void testAddUser() throws InterruptedException {
        String userType = "organizer";
        String userId = "0000adminWhiteBoxTest";


        // Create user profile
        User userProfile = new User(userId, "adminWhiteBoxTest", "aw@gmail.com", "2", "token", userType, "");
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

    /*  Deletes a dummy user from the database.
     *   This test checks to make sure that the user is deleted from the database.
     */
    @Test
    public void testDeleteUser() throws InterruptedException {
        testAddUser();
        String userId = "0000adminWhiteBoxTest";
        CountDownLatch deleteLatch = new CountDownLatch(1);
        boolean[] deleteSuccess = {false};
        db.collection("users")
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Delete Success");
                    deleteSuccess[0] = true;
                    deleteLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Delete Failed: " + e.getMessage());
                    deleteLatch.countDown();
                });
        deleteLatch.await();

        CountDownLatch queryLatch = new CountDownLatch(1);
        boolean[] found = {false};

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Log.d(TAG, "Document still exists");
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

        queryLatch.await();
        assertTrue("Deleted user from Firestore", deleteSuccess[0]);
        assertFalse("User still in Firestore", found[0]);

    }

    /*  Browsing users requires a list and adapter that update properly,
    *   This test checks to make sure that such a list and adapter can be created,
    *   and that the size of the list and adapter is correct after adding and deleting users
    *   from the database.
     */
    @Test
    public void testBrowseUsers() throws InterruptedException {
        final int[] size = {0};
        testDeleteUser();

        UserAdapter adapter;
        ArrayList<User> masterList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), masterList);
        Log.d(TAG, "adapter size: " + adapter.getCount());
        Log.d(TAG, "masterList size: " + masterList.size());
        Log.d(TAG, "size: " + size[0]);

        CountDownLatch queryLatch = new CountDownLatch(1);
        updateUserList(masterList, size, queryLatch);
        queryLatch.await();

        assertEquals(adapter.getCount(), size[0]);
        assertEquals(masterList.size(), size[0]);
        Log.d(TAG, "adapter size: " + adapter.getCount());
        Log.d(TAG, "masterList size: " + masterList.size());
        Log.d(TAG, "size: " + size[0]);

        testAddUser();
        size[0] = 0;
        queryLatch = new CountDownLatch(1);
        updateUserList(masterList, size, queryLatch);
        queryLatch.await();

        assertEquals(adapter.getCount(), size[0]);
        assertEquals(masterList.size(), size[0]);
        assertTrue(masterList.get(0).getUserId().equals(db.collection("users").document("0000adminWhiteBoxTest").getId()));
        Log.d(TAG, "adapter size: " + adapter.getCount());
        Log.d(TAG, "masterList size: " + masterList.size());
        Log.d(TAG, "size: " + size[0]);

        testDeleteUser();
        size[0] = 0;
        queryLatch = new CountDownLatch(1);
        updateUserList(masterList, size, queryLatch);
        queryLatch.await();

        assertEquals(adapter.getCount(), size[0]);
        assertEquals(masterList.size(), size[0]);
        assertFalse(masterList.get(0).getUserId().equals(db.collection("users").document("0000adminWhiteBoxTest").getId()));

    }

    /*  Adds a dummy event to the database .
    *   This test checks to make sure that the event is added to the database.
     */
    @Test
    public void testAddEvent() throws InterruptedException {
        String eventId = "0000adminWhiteBoxTestEvent";

        Event event = new Event();
        event.setId(eventId);
        event.setOrganizerId("0000adminWhiteBoxTest");
        event.setTitle("adminWhiteBoxEvent");
        event.setDescription("adminWhiteBoxEvent");
        event.setCategory("adminWhiteBoxEvent");

        event.setPrice(0.0);
        event.setMaxAttendees(50);  
        event.setMaxWaitListSize(20);  
        event.setLocation("adminWhiteBoxEvent");
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

    /*  Deletes a dummy event from the database .
     *   This test checks to make sure that the event is deleted from the database.
     */
    @Test
    public void testDeleteEvent() throws InterruptedException {
        testAddEvent();
        String eventId = "0000adminWhiteBoxTestEvent";
        CountDownLatch deleteLatch = new CountDownLatch(1);
        boolean[] deleteSuccess = {false};
        db.collection("events")
                .document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Delete Success");
                    deleteSuccess[0] = true;
                    deleteLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Delete Failed: " + e.getMessage());
                    deleteLatch.countDown();
                });
        deleteLatch.await();

        CountDownLatch queryLatch = new CountDownLatch(1);
        boolean[] found = {false};

        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Log.d(TAG, "Document still exists");
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

        queryLatch.await();
        assertTrue("Deleted event from Firestore", deleteSuccess[0]);
        assertFalse("Event still in Firestore", found[0]);

    }

    /*  Browsing events also requires a list and adapter that update properly,
     *   This test checks to make sure that such a list and adapter can be created,
     *   and that the size of the list and adapter is correct after adding and deleting events
     *   from the database.
     */
    @Test
    public void testBrowseEvents() throws InterruptedException {
        final int[] size = {0};
        testDeleteEvent();

        EventAdapter adapter;
        ArrayList<Event> masterList = new ArrayList<>();
        adapter = new EventAdapter(masterList, null);

        Log.d(TAG, "adapter size: " + adapter.getItemCount());
        Log.d(TAG, "masterList size: " + masterList.size());
        Log.d(TAG, "size: " + size[0]);

        CountDownLatch queryLatch = new CountDownLatch(1);
        updateEventList(masterList, size, queryLatch);
        queryLatch.await();

        assertEquals(adapter.getItemCount(), size[0]);
        assertEquals(masterList.size(), size[0]);
        Log.d(TAG, "adapter size: " + adapter.getItemCount());
        Log.d(TAG, "masterList size: " + masterList.size());
        Log.d(TAG, "size: " + size[0]);

        testAddEvent();
        size[0] = 0;
        queryLatch = new CountDownLatch(1);
        updateEventList(masterList, size, queryLatch);
        queryLatch.await();

        assertEquals(adapter.getItemCount(), size[0]);
        assertEquals(masterList.size(), size[0]);
        assertTrue(masterList.get(0).getId().equals(db.collection("events").document("0000adminWhiteBoxTestEvent").getId()));
        Log.d(TAG, "adapter size: " + adapter.getItemCount());
        Log.d(TAG, "masterList size: " + masterList.size());
        Log.d(TAG, "size: " + size[0]);

        testDeleteEvent();
        size[0] = 0;
        queryLatch = new CountDownLatch(1);
        updateEventList(masterList, size, queryLatch);
        queryLatch.await();

        assertEquals(adapter.getItemCount(), size[0]);
        assertEquals(masterList.size(), size[0]);
        assertFalse(masterList.get(0).getId().equals(db.collection("events").document("0000adminWhiteBoxTestEvent").getId()));

    }

    /*  Deleting an organizer should delete their events as well 
    *   This test checks to make sure that the cascading delete works.
     */
    @Test
    public void testDeleteOrganizer() throws InterruptedException {
        testAddUser();
        testAddEvent();
        String userId = "0000adminWhiteBoxTest";
        String eventId = "0000adminWhiteBoxTestEvent";
        CountDownLatch deleteLatch = new CountDownLatch(1);
        boolean[] deleteSuccess = {false};
        CountDownLatch finalDeleteLatch = deleteLatch;
        CountDownLatch finalDeleteLatch1 = deleteLatch;
        db.collection("users")
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Delete Success");
                    deleteSuccess[0] = true;
                    finalDeleteLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Delete Failed: " + e.getMessage());
                    finalDeleteLatch1.countDown();
                });
        deleteLatch.await();

        CountDownLatch queryLatch = new CountDownLatch(1);
        boolean[] found = {false};

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Log.d(TAG, "Document still exists");
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

        queryLatch.await();
        assertTrue("Deleted user from Firestore", deleteSuccess[0]);
        assertFalse("User still in Firestore", found[0]);

        deleteLatch = new CountDownLatch(1);
        deleteSuccess[0] = false;
        CountDownLatch finalDeleteLatch2 = deleteLatch;
        db.collection("events").get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                if (doc != null && doc.getString("organizerId") != null && doc.getString("organizerId").trim().equals(userId)) {
                    db.collection("events").document(doc.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Deleted event from Firestore");
                                deleteSuccess[0] = true;
                                        finalDeleteLatch2.countDown();

                                    }
                                    )
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to delete event from Firestore: " + e.getMessage());
                                deleteSuccess[0] = false;
                                finalDeleteLatch2.countDown();
                            });
                }

            }

        });
        deleteLatch.await();
        assertTrue("Deleted event from Firestore", deleteSuccess[0]);
    }


    public void updateUserList(ArrayList<User> masterList, final int[] size, CountDownLatch queryLatch) {
        db.collection("users").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
            }
            if (value != null && !value.isEmpty()) {
                masterList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    String userId = snapshot.getId();
                    String name = snapshot.getString("name");
                    String email = snapshot.getString("email");
                    String phone = snapshot.getString("phone");
                    String favRecCenter = snapshot.getString("favRecCenter");
                    String userType = snapshot.getString("userType");

                    masterList.add(new User(userId, name, email, phone, "token", userType, favRecCenter));
                    size[0]++;
                }
                Log.d(TAG, "Master list size: " + masterList.size());
                Log.d(TAG, "Value list size: " + value.size());
                queryLatch.countDown();
            }
        });
    }

    public void updateEventList(ArrayList<Event> masterList, final int[] size, CountDownLatch queryLatch) {
        db.collection("events").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (value != null && !value.isEmpty()) {
                masterList.clear();
                size[0] = 0;
                for (QueryDocumentSnapshot snapshot : value) {
                    Event event = new Event();
                    event.setId(snapshot.getId()); // Set the document ID
                    masterList.add(event);
                    size[0]++;
                }
                Log.d(TAG, "Master list size: " + masterList.size());
                queryLatch.countDown();
            }
        });
    }
}