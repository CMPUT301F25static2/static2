package com.ualberta.static2.notification;

import static org.junit.Assert.*;

import com.ualberta.eventlottery.notification.NotificationModel;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for NotificationModel class.
 * Tests model constructors, getters, setters, and business logic.
 */
public class NotificationModelTest {

    private NotificationModel notification;
    private List<String> recipientList;

    @Before
    public void setUp() {
        recipientList = new ArrayList<>();
        recipientList.add("user1");
        recipientList.add("user2");
        notification = new NotificationModel(
                "Test Title",
                "Test Body",
                "event123",
                recipientList,
                "action"
        );
    }

    @Test
    public void testEmptyConstructor() {
        NotificationModel emptyNotification = new NotificationModel();
        assertNull(emptyNotification.getTitle());
        assertNull(emptyNotification.getBody());
        assertNull(emptyNotification.getEventId());
        assertNull(emptyNotification.getRecipientIdList());
        assertNull(emptyNotification.getNotificationId());
        assertNull(emptyNotification.getSenderId());
        assertFalse(emptyNotification.getIsRead());
    }

    @Test
    public void testConstructorWithParameters() {
        assertEquals("Test Title", notification.getTitle());
        assertEquals("Test Body", notification.getBody());
        assertEquals("event123", notification.getEventId());
        assertEquals(recipientList, notification.getRecipientIdList());
        assertEquals("action", notification.getNotificationType());
        assertNotNull(notification.getCreatedAt());
        assertFalse(notification.getIsRead());
        assertNull(notification.getSenderId());
    }

    @Test
    public void testGettersAndSetters() {
        // Test setters
        notification.setNotificationId("notif123");
        notification.setTitle("New Title");
        notification.setBody("New Body");
        notification.setIsRead(true);
        notification.setNotificationType("info");

        // Test getters
        assertEquals("notif123", notification.getNotificationId());
        assertEquals("New Title", notification.getTitle());
        assertEquals("New Body", notification.getBody());
        assertTrue(notification.getIsRead());
        assertEquals("info", notification.getNotificationType());
    }

    @Test
    public void testCreatedAtIsSet() {
        Date beforeCreation = new Date();
        NotificationModel newNotification = new NotificationModel(
                "Title",
                "Body",
                "event1",
                recipientList,
                "action"
        );
        Date afterCreation = new Date();

        assertNotNull(newNotification.getCreatedAt());
        assertTrue(newNotification.getCreatedAt().getTime() >= beforeCreation.getTime());
        assertTrue(newNotification.getCreatedAt().getTime() <= afterCreation.getTime());
    }

    @Test
    public void testIsReadDefaultFalse() {
        assertFalse(notification.getIsRead());
    }

    @Test
    public void testRecipientListIsPreserved() {
        List<String> newRecipients = new ArrayList<>();
        newRecipients.add("user3");
        newRecipients.add("user4");

        // Note: NotificationModel doesn't have a setter for recipientIdList
        // This test verifies the list is correctly stored
        assertEquals(2, notification.getRecipientIdList().size());
        assertTrue(notification.getRecipientIdList().contains("user1"));
        assertTrue(notification.getRecipientIdList().contains("user2"));
    }

    @Test
    public void testNotificationType() {
        NotificationModel infoNotification = new NotificationModel(
                "Info",
                "Info body",
                "event1",
                recipientList,
                "info"
        );
        assertEquals("info", infoNotification.getNotificationType());

        NotificationModel actionNotification = new NotificationModel(
                "Action",
                "Action body",
                "event1",
                recipientList,
                "action"
        );
        assertEquals("action", actionNotification.getNotificationType());
    }

    @Test
    public void testNullValues() {
        NotificationModel nullNotification = new NotificationModel(
                null,
                null,
                null,
                null,
                null
        );
        assertNull(nullNotification.getTitle());
        assertNull(nullNotification.getBody());
        assertNull(nullNotification.getEventId());
        assertNull(nullNotification.getRecipientIdList());
        assertNull(nullNotification.getNotificationType());
    }

    @Test
    public void testEmptyRecipientList() {
        List<String> emptyList = new ArrayList<>();
        NotificationModel emptyRecipientNotification = new NotificationModel(
                "Title",
                "Body",
                "event1",
                emptyList,
                "action"
        );
        assertNotNull(emptyRecipientNotification.getRecipientIdList());
        assertEquals(0, emptyRecipientNotification.getRecipientIdList().size());
    }
}



