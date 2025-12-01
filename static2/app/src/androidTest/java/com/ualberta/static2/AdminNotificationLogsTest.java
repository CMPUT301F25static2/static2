package com.ualberta.static2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.platform.app.InstrumentationRegistry;

import com.ualberta.eventlottery.model.NotificationLog;
import com.ualberta.eventlottery.notification.NotificationModel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Test class for US 03.08.01: As an administrator, I want to review logs of all notifications sent to entrants by organizers.
 * Tests the admin notification logs functionality.
 */
public class AdminNotificationLogsTest {

    /**
     * Test creating a NotificationLog from NotificationModel
     */


    @Test
    public void testNotificationLogCreation() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create NotificationModel
        List<String> recipients = Arrays.asList("user1", "user2", "user3");
        NotificationModel notification = new NotificationModel(
                "Test Notification",
                "This is a test message",
                "event-123",
                recipients
        );
        notification.setNotificationId("notif-1");

        // Create NotificationLog from NotificationModel
        NotificationLog log = new NotificationLog(notification);

        assertNotNull("Log should be created", log);
        assertEquals("Title should match", "Test Notification", log.getTitle());
        assertEquals("Body should match", "This is a test message", log.getBody());
        assertEquals("Notification ID should match", "notif-1", log.getNotificationId());
        assertEquals("Recipient count should be 3", 3, log.getRecipientCount());
    }

    /**
     * Test setting organizer name in notification log
     */
    @Test
    public void testSetOrganizerName() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        NotificationLog log = new NotificationLog();
        log.setOrganizerName("John Doe");

        assertEquals("Organizer name should be set", "John Doe", log.getOrganizerName());
    }

    /**
     * Test setting event title in notification log
     */
    @Test
    public void testSetEventTitle() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        NotificationLog log = new NotificationLog();
        log.setEventTitle("Sample Event");

        assertEquals("Event title should be set", "Sample Event", log.getEventTitle());
    }

    /**
     * Test setting recipient count
     */
    @Test
    public void testSetRecipientCount() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        NotificationLog log = new NotificationLog();
        log.setRecipientCount(50);

        assertEquals("Recipient count should be 50", 50, log.getRecipientCount());
    }

    /**
     * Test notification log with no recipients
     */

    @Test
    public void testLogWithNoRecipients() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<String> recipients = new ArrayList<>();
        NotificationModel notification = new NotificationModel(
                "Empty Notification",
                "No recipients",
                "event-456",
                recipients
        );

        NotificationLog log = new NotificationLog(notification);

        assertEquals("Recipient count should be 0", 0, log.getRecipientCount());
    }

    /**
     * Test filtering logs by title
     */
    @Test
    public void testFilterLogsByTitle() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> allLogs = createSampleLogs();
        List<NotificationLog> filtered = filterLogs(allLogs, "selected");

        assertEquals("Should find 1 log with 'selected' in title", 1, filtered.size());
        assertTrue("Filtered log should contain 'selected'",
                filtered.get(0).getTitle().toLowerCase().contains("selected"));
    }

    /**
     * Test filtering logs by event title
     */
    @Test
    public void testFilterLogsByEventTitle() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> allLogs = createSampleLogs();
        List<NotificationLog> filtered = filterLogs(allLogs, "concert");

        assertEquals("Should find 1 log with 'concert' event", 1, filtered.size());
        assertTrue("Filtered log should be for concert event",
                filtered.get(0).getEventTitle().toLowerCase().contains("concert"));
    }

    /**
     * Test filtering logs by organizer name
     */
    @Test
    public void testFilterLogsByOrganizerName() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> allLogs = createSampleLogs();
        List<NotificationLog> filtered = filterLogs(allLogs, "alice");

        assertEquals("Should find logs from Alice", 1, filtered.size());
        assertTrue("Filtered log should be from Alice",
                filtered.get(0).getOrganizerName().toLowerCase().contains("alice"));
    }

    /**
     * Test filtering logs with empty query returns all logs
     */
    @Test
    public void testFilterWithEmptyQuery() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> allLogs = createSampleLogs();
        List<NotificationLog> filtered = filterLogs(allLogs, "");

        assertEquals("Empty query should return all logs", allLogs.size(), filtered.size());
    }

    /**
     * Test filtering logs with no matches
     */
    @Test
    public void testFilterWithNoMatches() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> allLogs = createSampleLogs();
        List<NotificationLog> filtered = filterLogs(allLogs, "nonexistent");

        assertEquals("Should return 0 results for non-matching query", 0, filtered.size());
    }

    /**
     * Test sorting logs by date (newest first)
     */
    @Test
    public void testLogsSortedByDate() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> logs = createSampleLogs();

        // Verify logs are in descending order by date
        for (int i = 0; i < logs.size() - 1; i++) {
            Date current = logs.get(i).getCreatedAt();
            Date next = logs.get(i + 1).getCreatedAt();

            if (current != null && next != null) {
                assertTrue("Logs should be sorted newest first",
                        current.getTime() >= next.getTime());
            }
        }
    }

    /**
     * Test notification log with null values
     */
    @Test
    public void testLogWithNullValues() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        NotificationLog log = new NotificationLog();

        assertNull("Title should be null", log.getTitle());
        assertNull("Body should be null", log.getBody());
        assertNull("Event title should be null", log.getEventTitle());
        assertNull("Organizer name should be null", log.getOrganizerName());
        assertEquals("Recipient count should be 0", 0, log.getRecipientCount());
    }

    /**
     * Test multiple logs from same organizer
     */
    @Test
    public void testMultipleLogsFromSameOrganizer() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> allLogs = createSampleLogs();
        List<NotificationLog> bobLogs = filterLogs(allLogs, "bob");

        assertEquals("Bob should have sent 2 notifications", 2, bobLogs.size());
    }

    /**
     * Test total recipient count across all logs
     */
    @Test
    public void testTotalRecipientCount() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<NotificationLog> logs = createSampleLogs();
        int totalRecipients = 0;

        for (NotificationLog log : logs) {
            totalRecipients += log.getRecipientCount();
        }

        assertTrue("Total recipients should be greater than 0", totalRecipients > 0);
    }

    // Helper methods

    private List<NotificationLog> createSampleLogs() {
        List<NotificationLog> logs = new ArrayList<>();

        // Log 1: Selected notification
        NotificationLog log1 = new NotificationLog();
        log1.setNotificationId("log-1");
        log1.setTitle("You've been selected!");
        log1.setBody("Congratulations, you won the lottery");
        log1.setEventTitle("Summer Concert");
        log1.setOrganizerName("Alice Smith");
        log1.setRecipientCount(5);
        logs.add(log1);

        // Log 2: Registration open
        NotificationLog log2 = new NotificationLog();
        log2.setNotificationId("log-2");
        log2.setTitle("Registration Now Open");
        log2.setBody("Registration is now open for the event");
        log2.setEventTitle("Sports Tournament");
        log2.setOrganizerName("Bob Johnson");
        log2.setRecipientCount(100);
        logs.add(log2);

        // Log 3: Event cancelled
        NotificationLog log3 = new NotificationLog();
        log3.setNotificationId("log-3");
        log3.setTitle("Event Cancelled");
        log3.setBody("Unfortunately, the event has been cancelled");
        log3.setEventTitle("Art Exhibition");
        log3.setOrganizerName("Bob Johnson");
        log3.setRecipientCount(25);
        logs.add(log3);

        return logs;
    }

    private List<NotificationLog> filterLogs(List<NotificationLog> logs, String query) {
        if (query == null || query.trim().isEmpty()) {
            return logs;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<NotificationLog> filtered = new ArrayList<>();

        for (NotificationLog log : logs) {
            boolean matches = false;

            if (log.getTitle() != null && log.getTitle().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            if (log.getBody() != null && log.getBody().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            if (log.getEventTitle() != null && log.getEventTitle().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            if (log.getOrganizerName() != null && log.getOrganizerName().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }

            if (matches) {
                filtered.add(log);
            }
        }

        return filtered;
    }
}

