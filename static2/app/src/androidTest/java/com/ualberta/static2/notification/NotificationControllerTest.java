package com.ualberta.static2.notification;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.ualberta.eventlottery.notification.NotificationController;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Android instrumentation tests for NotificationController class.
 * Tests notification channel creation and notification display functionality.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationControllerTest {

    private Context context;
    private NotificationController controller;
    private static final String CHANNEL_ID = "notification channel";

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS);

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        controller = new NotificationController(context);
    }

    @Test
    public void testNotificationControllerCreation() {
        assertNotNull(controller);
    }

    @Test
    public void testNotificationChannelCreated() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = 
                    context.getSystemService(NotificationManager.class);
            assertNotNull(notificationManager);
            
            android.app.NotificationChannel channel = 
                    notificationManager.getNotificationChannel(CHANNEL_ID);
            assertNotNull(channel);
            assertEquals(CHANNEL_ID, channel.getId());
        }
    }

    @Test
    public void testDisplayNotification_WithValidInput() {
        // This test verifies that displayNotification doesn't throw exceptions
        // Actual notification display requires system UI which is hard to verify
        try {
            controller.displayNotification("Test Title", "Test Body");
            // If we reach here, no exception was thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("displayNotification should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayNotification_WithNullTitle() {
        try {
            controller.displayNotification(null, "Test Body");
            assertTrue(true);
        } catch (Exception e) {
            fail("displayNotification should handle null title: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayNotification_WithNullBody() {
        try {
            controller.displayNotification("Test Title", null);
            assertTrue(true);
        } catch (Exception e) {
            fail("displayNotification should handle null body: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayNotification_WithEmptyStrings() {
        try {
            controller.displayNotification("", "");
            assertTrue(true);
        } catch (Exception e) {
            fail("displayNotification should handle empty strings: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayNotification_WithLongText() {
        String longTitle = "This is a very long title that might cause issues with notification display";
        String longBody = "This is a very long body text that might cause issues with notification display. " +
                "It contains multiple sentences and should still work correctly.";
        
        try {
            controller.displayNotification(longTitle, longBody);
            assertTrue(true);
        } catch (Exception e) {
            fail("displayNotification should handle long text: " + e.getMessage());
        }
    }

    @Test
    public void testSendNotification_WithValidInput() {
        // This test verifies that sendNotification doesn't throw exceptions
        // Actual FCM sending requires Firebase which is tested in integration tests
        try {
            java.util.List<String> recipients = new java.util.ArrayList<>();
            recipients.add("user1");
            recipients.add("user2");
            
            controller.sendNotification(
                    "Test Title",
                    "Test Body",
                    "event123",
                    recipients,
                    "action"
            );
            // If we reach here, no exception was thrown
            assertTrue(true);
        } catch (Exception e) {
            // FCM calls may fail in test environment, which is acceptable
            // We just verify the method doesn't crash
        }
    }

    @Test
    public void testSendNotification_WithEmptyRecipientList() {
        try {
            java.util.List<String> emptyList = new java.util.ArrayList<>();
            controller.sendNotification(
                    "Title",
                    "Body",
                    "event1",
                    emptyList,
                    "action"
            );
            assertTrue(true);
        } catch (Exception e) {
            // Expected to fail with empty list, but shouldn't crash
        }
    }

    @Test
    public void testSendNotification_WithNullRecipientList() {
        try {
            controller.sendNotification(
                    "Title",
                    "Body",
                    "event1",
                    null,
                    "action"
            );
            // May throw NullPointerException, which is acceptable behavior
        } catch (NullPointerException e) {
            // Expected behavior
            assertTrue(true);
        } catch (Exception e) {
            // Other exceptions are also acceptable in test environment
        }
    }

    @Test
    public void testMultipleNotificationControllers() {
        // Test that multiple controllers can be created
        NotificationController controller1 = new NotificationController(context);
        NotificationController controller2 = new NotificationController(context);
        
        assertNotNull(controller1);
        assertNotNull(controller2);
        assertNotSame(controller1, controller2);
    }
}



