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

    /**
     * Whitebox test for US 01.04.01: Win notification model.
     * Verifies that NotificationModel can store win notification data correctly.
     */
    @Test
    public void testWinNotificationModel_US_01_04_01() {
        List<String> selectedUserIds = new ArrayList<>();
        selectedUserIds.add("winner1");
        selectedUserIds.add("winner2");

        NotificationModel winNotification = new NotificationModel(
                "🎉 Event Test Event – You Have Been Selected!",
                "Congratulations! You have been selected to attend the event \"Test Event\".",
                "event123",
                selectedUserIds,
                "action"
        );

        assertEquals("🎉 Event Test Event – You Have Been Selected!", winNotification.getTitle());
        assertTrue(winNotification.getBody().contains("Congratulations"));
        assertEquals("event123", winNotification.getEventId());
        assertEquals(2, winNotification.getRecipientIdList().size());
        assertEquals("action", winNotification.getNotificationType());
    }

    /**
     * Whitebox test for US 01.04.02: Lose notification model.
     * Verifies that NotificationModel can store lose notification data correctly.
     */
    @Test
    public void testLoseNotificationModel_US_01_04_02() {
        List<String> nonSelectedUserIds = new ArrayList<>();
        nonSelectedUserIds.add("loser1");
        nonSelectedUserIds.add("loser2");

        NotificationModel loseNotification = new NotificationModel(
                "Event Test Event – Waiting List Update",
                "Unfortunately, you were not selected for the event \"Test Event\" at this time.",
                "event123",
                nonSelectedUserIds,
                "action"
        );

        assertEquals("Event Test Event – Waiting List Update", loseNotification.getTitle());
        assertTrue(loseNotification.getBody().contains("Unfortunately"));
        assertEquals("event123", loseNotification.getEventId());
        assertEquals(2, loseNotification.getRecipientIdList().size());
        assertEquals("action", loseNotification.getNotificationType());
    }

    /**
     * Whitebox test for US 02.07.01: Waiting list notification model.
     * Verifies that NotificationModel can store waiting list notification data correctly.
     */
    @Test
    public void testWaitingListNotificationModel_US_02_07_01() {
        List<String> waitingListUserIds = new ArrayList<>();
        waitingListUserIds.add("waiting1");
        waitingListUserIds.add("waiting2");
        waitingListUserIds.add("waiting3");

        NotificationModel waitingListNotification = new NotificationModel(
                "Event Notification",
                "Custom message for waiting list entrants",
                "event123",
                waitingListUserIds,
                "general"
        );

        assertEquals("Event Notification", waitingListNotification.getTitle());
        assertEquals("Custom message for waiting list entrants", waitingListNotification.getBody());
        assertEquals("event123", waitingListNotification.getEventId());
        assertEquals(3, waitingListNotification.getRecipientIdList().size());
        assertEquals("general", waitingListNotification.getNotificationType());
    }

    /**
     * Whitebox test for US 02.07.02: Selected entrants notification model.
     * Verifies that NotificationModel can store selected entrants notification data correctly.
     */
    @Test
    public void testSelectedEntrantsNotificationModel_US_02_07_02() {
        List<String> selectedEntrantIds = new ArrayList<>();
        selectedEntrantIds.add("selected1");
        selectedEntrantIds.add("selected2");

        NotificationModel selectedNotification = new NotificationModel(
                "Event Notification",
                "Notification message for selected entrants",
                "event123",
                selectedEntrantIds,
                "general"
        );

        assertEquals("Event Notification", selectedNotification.getTitle());
        assertEquals("Notification message for selected entrants", selectedNotification.getBody());
        assertEquals("event123", selectedNotification.getEventId());
        assertEquals(2, selectedNotification.getRecipientIdList().size());
        assertEquals("general", selectedNotification.getNotificationType());
    }

    /**
     * Whitebox test for US 02.07.03: Cancelled entrants notification model.
     * Verifies that NotificationModel can store cancelled entrants notification data correctly.
     */
    @Test
    public void testCancelledEntrantsNotificationModel_US_02_07_03() {
        List<String> cancelledEntrantIds = new ArrayList<>();
        cancelledEntrantIds.add("cancelled1");
        cancelledEntrantIds.add("cancelled2");

        NotificationModel cancelledNotification = new NotificationModel(
                "Event Notification",
                "Notification message for cancelled entrants",
                "event123",
                cancelledEntrantIds,
                "general"
        );

        assertEquals("Event Notification", cancelledNotification.getTitle());
        assertEquals("Notification message for cancelled entrants", cancelledNotification.getBody());
        assertEquals("event123", cancelledNotification.getEventId());
        assertEquals(2, cancelledNotification.getRecipientIdList().size());
        assertEquals("general", cancelledNotification.getNotificationType());
    }

    /**
     * Whitebox test: Verifies that notification types are correctly differentiated.
     * Win/lose notifications use "action" type, while organizer notifications use "general".
     */
    @Test
    public void testNotificationTypeDifferentiation() {
        // Win notification uses "action" type
        NotificationModel winNotification = new NotificationModel(
                "Win Title", "Win Body", "event1", recipientList, "action"
        );
        assertEquals("action", winNotification.getNotificationType());

        // Organizer notifications use "general" type
        NotificationModel organizerNotification = new NotificationModel(
                "Organizer Title", "Organizer Body", "event1", recipientList, "general"
        );
        assertEquals("general", organizerNotification.getNotificationType());

        assertNotEquals(winNotification.getNotificationType(), organizerNotification.getNotificationType());
    }
}




