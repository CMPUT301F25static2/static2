package com.ualberta.static2.notification;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.notification.NotificationModel;
import com.ualberta.eventlottery.ui.notifications.NotificationsListAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Android instrumentation tests for NotificationsListAdapter class.
 * Tests adapter functionality including view creation, data binding, and click handling.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationsListAdapterTest {

    private Context context;
    private NotificationsListAdapter adapter;
    private List<NotificationModel> testNotifications;
    private TestClickListener clickListener;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        clickListener = new TestClickListener();
        adapter = new NotificationsListAdapter(context, clickListener);
        testNotifications = createTestNotifications();
    }

    @Test
    public void testAdapterCreation() {
        assertNotNull(adapter);
    }

    @Test
    public void testGetCount_EmptyList() {
        assertEquals(0, adapter.getCount());
    }

    @Test
    public void testGetCount_WithNotifications() {
        adapter.setNotifications(testNotifications);
        assertEquals(3, adapter.getCount());
    }

    @Test
    public void testGetItem() {
        adapter.setNotifications(testNotifications);
        NotificationModel item = adapter.getItem(0);
        assertNotNull(item);
        assertEquals("Test Title 1", item.getTitle());
    }

    @Test
    public void testGetItemId() {
        adapter.setNotifications(testNotifications);
        assertEquals(0, adapter.getItemId(0));
        assertEquals(1, adapter.getItemId(1));
        assertEquals(2, adapter.getItemId(2));
    }

    @Test
    public void testSetNotifications() {
        adapter.setNotifications(testNotifications);
        assertEquals(3, adapter.getCount());
    }

    @Test
    public void testSetNotifications_Null() {
        adapter.setNotifications(null);
        assertEquals(0, adapter.getCount());
    }

    @Test
    public void testSetNotifications_EmptyList() {
        adapter.setNotifications(new ArrayList<>());
        assertEquals(0, adapter.getCount());
    }

    @Test
    public void testSetNotifications_ReplacesExisting() {
        adapter.setNotifications(testNotifications);
        assertEquals(3, adapter.getCount());

        List<NotificationModel> newNotifications = new ArrayList<>();
        newNotifications.add(createNotification("New Title", "New Body", "event2"));
        adapter.setNotifications(newNotifications);
        assertEquals(1, adapter.getCount());
        assertEquals("New Title", adapter.getItem(0).getTitle());
    }

    @Test
    public void testGetView_CreatesView() {
        adapter.setNotifications(testNotifications);
        View view = adapter.getView(0, null, null);
        assertNotNull(view);
    }

    @Test
    public void testGetView_ReusesConvertView() {
        adapter.setNotifications(testNotifications);
        View firstView = adapter.getView(0, null, null);
        View secondView = adapter.getView(1, firstView, null);
        
        // View should be reused
        assertSame(firstView, secondView);
    }

    @Test
    public void testGetView_DisplaysTitle() {
        adapter.setNotifications(testNotifications);
        View view = adapter.getView(0, null, null);
        
        TextView titleView = view.findViewById(com.ualberta.static2.R.id.notification_title);
        assertNotNull(titleView);
        assertEquals("Test Title 1", titleView.getText().toString());
    }

    @Test
    public void testGetView_DisplaysBody() {
        adapter.setNotifications(testNotifications);
        View view = adapter.getView(0, null, null);
        
        TextView bodyView = view.findViewById(com.ualberta.static2.R.id.notification_body);
        assertNotNull(bodyView);
        assertTrue(bodyView.getText().toString().contains("Test Body 1"));
    }

    @Test
    public void testGetView_TruncatesLongBody() {
        String longBody = "This is a very long body text that should be truncated " +
                "when it exceeds 100 characters in length. It should show only the first " +
                "100 characters followed by ellipsis.";
        
        List<NotificationModel> longBodyNotifications = new ArrayList<>();
        longBodyNotifications.add(createNotification("Title", longBody, "event1"));
        adapter.setNotifications(longBodyNotifications);
        
        View view = adapter.getView(0, null, null);
        TextView bodyView = view.findViewById(com.ualberta.static2.R.id.notification_body);
        
        String displayedText = bodyView.getText().toString();
        assertTrue(displayedText.length() <= 103); // 100 chars + "..."
        assertTrue(displayedText.endsWith("..."));
    }

    @Test
    public void testGetView_ShowsReadState() {
        NotificationModel readNotification = createNotification("Read", "Body", "event1");
        readNotification.setIsRead(true);
        
        List<NotificationModel> notifications = new ArrayList<>();
        notifications.add(readNotification);
        adapter.setNotifications(notifications);
        
        View view = adapter.getView(0, null, null);
        // Read notifications should have reduced alpha
        assertEquals(0.7f, view.getAlpha(), 0.01f);
    }

    @Test
    public void testGetView_ShowsUnreadState() {
        NotificationModel unreadNotification = createNotification("Unread", "Body", "event1");
        unreadNotification.setIsRead(false);
        
        List<NotificationModel> notifications = new ArrayList<>();
        notifications.add(unreadNotification);
        adapter.setNotifications(notifications);
        
        View view = adapter.getView(0, null, null);
        // Unread notifications should have full alpha
        assertEquals(1.0f, view.getAlpha(), 0.01f);
    }

    @Test
    public void testCardClick_TriggersListener() {
        adapter.setNotifications(testNotifications);
        View view = adapter.getView(0, null, null);
        
        View card = view.findViewById(com.ualberta.static2.R.id.notification_card);
        assertNotNull(card);
        card.performClick();
        
        // Verify click listener was called
        assertTrue(clickListener.wasClickCalled());
        assertEquals("Test Title 1", clickListener.getLastClickedNotification().getTitle());
    }

    @Test
    public void testCloseButtonClick_TriggersListener() {
        adapter.setNotifications(testNotifications);
        View view = adapter.getView(0, null, null);
        
        View closeButton = view.findViewById(com.ualberta.static2.R.id.close_button);
        assertNotNull(closeButton);
        closeButton.performClick();
        
        // Wait for animation to complete (simplified - in real test might need idling resource)
        try {
            Thread.sleep(300); // Wait for animation
        } catch (InterruptedException e) {
            // Ignore
        }
        
        // Verify close listener was called
        assertTrue(clickListener.wasCloseCalled());
        assertEquals("Test Title 1", clickListener.getLastClosedNotification().getTitle());
    }

    /**
     * Helper method to create test notifications.
     */
    private List<NotificationModel> createTestNotifications() {
        List<NotificationModel> notifications = new ArrayList<>();
        notifications.add(createNotification("Test Title 1", "Test Body 1", "event1"));
        notifications.add(createNotification("Test Title 2", "Test Body 2", "event2"));
        notifications.add(createNotification("Test Title 3", "Test Body 3", "event3"));
        return notifications;
    }

    /**
     * Helper method to create a single notification.
     */
    private NotificationModel createNotification(String title, String body, String eventId) {
        List<String> recipients = new ArrayList<>();
        recipients.add("user1");
        return new NotificationModel(title, body, eventId, recipients, "action");
    }

    /**
     * Test implementation of OnNotificationClickListener for testing.
     */
    private static class TestClickListener implements NotificationsListAdapter.OnNotificationClickListener {
        private boolean clickCalled = false;
        private boolean closeCalled = false;
        private NotificationModel lastClickedNotification;
        private NotificationModel lastClosedNotification;

        @Override
        public void onNotificationClick(NotificationModel notification) {
            clickCalled = true;
            lastClickedNotification = notification;
        }

        @Override
        public void onNotificationClose(NotificationModel notification) {
            closeCalled = true;
            lastClosedNotification = notification;
        }

        public boolean wasClickCalled() {
            return clickCalled;
        }

        public boolean wasCloseCalled() {
            return closeCalled;
        }

        public NotificationModel getLastClickedNotification() {
            return lastClickedNotification;
        }

        public NotificationModel getLastClosedNotification() {
            return lastClosedNotification;
        }

        public void reset() {
            clickCalled = false;
            closeCalled = false;
            lastClickedNotification = null;
            lastClosedNotification = null;
        }
    }
}



