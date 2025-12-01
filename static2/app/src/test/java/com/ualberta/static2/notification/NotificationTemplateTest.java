package com.ualberta.static2.notification;

import static org.junit.Assert.*;

import com.ualberta.eventlottery.ui.notifications.NotificationTemplate;

import org.junit.Test;

/**
 * Unit tests for NotificationTemplate class.
 * Tests all template methods for generating notification titles and bodies.
 */
public class NotificationTemplateTest {

    @Test
    public void testGetAcceptedTitle() {
        String eventTitle = "Summer Festival";
        String result = NotificationTemplate.getAcceptedTitle(eventTitle);
        assertEquals("🎉 Event Summer Festival – You Have Been Selected!", result);
    }

    @Test
    public void testGetAcceptedTitle_EmptyString() {
        String result = NotificationTemplate.getAcceptedTitle("");
        assertEquals("🎉 Event  – You Have Been Selected!", result);
    }

    @Test
    public void testGetAcceptedTitle_Null() {
        String result = NotificationTemplate.getAcceptedTitle(null);
        assertEquals("🎉 Event null – You Have Been Selected!", result);
    }

    @Test
    public void testGetAcceptedBody() {
        String eventTitle = "Summer Festival";
        String result = NotificationTemplate.getAcceptedBody(eventTitle);
        assertTrue(result.contains("Congratulations!"));
        assertTrue(result.contains("Summer Festival"));
        assertTrue(result.contains("You have been selected"));
    }

    @Test
    public void testGetAcceptedBody_EmptyString() {
        String result = NotificationTemplate.getAcceptedBody("");
        assertTrue(result.contains("Congratulations!"));
        assertTrue(result.contains("\"\""));
    }

    @Test
    public void testGetAcceptedBody_Null() {
        String result = NotificationTemplate.getAcceptedBody(null);
        assertTrue(result.contains("Congratulations!"));
        assertTrue(result.contains("null"));
    }

    @Test
    public void testGetNotAcceptedTitle() {
        String eventTitle = "Winter Workshop";
        String result = NotificationTemplate.getNotAcceptedTitle(eventTitle);
        assertEquals("Event Winter Workshop – Waiting List Update", result);
    }

    @Test
    public void testGetNotAcceptedTitle_EmptyString() {
        String result = NotificationTemplate.getNotAcceptedTitle("");
        assertEquals("Event  – Waiting List Update", result);
    }

    @Test
    public void testGetNotAcceptedTitle_Null() {
        String result = NotificationTemplate.getNotAcceptedTitle(null);
        assertEquals("Event null – Waiting List Update", result);
    }

    @Test
    public void testGetNotAcceptedBody() {
        String eventTitle = "Winter Workshop";
        String result = NotificationTemplate.getNotAcceptedBody(eventTitle);
        assertTrue(result.contains("Unfortunately"));
        assertTrue(result.contains("Winter Workshop"));
        assertTrue(result.contains("waiting list"));
    }

    @Test
    public void testGetNotAcceptedBody_EmptyString() {
        String result = NotificationTemplate.getNotAcceptedBody("");
        assertTrue(result.contains("Unfortunately"));
        assertTrue(result.contains("\"\""));
    }

    @Test
    public void testGetNotAcceptedBody_Null() {
        String result = NotificationTemplate.getNotAcceptedBody(null);
        assertTrue(result.contains("Unfortunately"));
        assertTrue(result.contains("null"));
    }

    @Test
    public void testGetAcceptedOrganizer() {
        String name = "John Doe";
        String eventTitle = "Spring Conference";
        String result = NotificationTemplate.getAcceptedOrganizer(name, eventTitle);
        assertEquals("John Doe has accepted their spot in your event: Spring Conference", result);
    }

    @Test
    public void testGetAcceptedOrganizer_EmptyStrings() {
        String result = NotificationTemplate.getAcceptedOrganizer("", "");
        assertEquals(" has accepted their spot in your event: ", result);
    }

    @Test
    public void testGetAcceptedOrganizer_NullValues() {
        String result = NotificationTemplate.getAcceptedOrganizer(null, null);
        assertEquals("null has accepted their spot in your event: null", result);
    }

    @Test
    public void testGetNotAcceptedOrganizer() {
        String name = "Jane Smith";
        String eventTitle = "Fall Retreat";
        String result = NotificationTemplate.getNotAcceptedOrganizer(name, eventTitle);
        assertEquals("Jane Smith has chosen cancelled their spot in your event: Fall Retreat", result);
    }

    @Test
    public void testGetNotAcceptedOrganizer_EmptyStrings() {
        String result = NotificationTemplate.getNotAcceptedOrganizer("", "");
        assertEquals(" has chosen cancelled their spot in your event: ", result);
    }

    @Test
    public void testGetNotAcceptedOrganizer_NullValues() {
        String result = NotificationTemplate.getNotAcceptedOrganizer(null, null);
        assertEquals("null has chosen cancelled their spot in your event: null", result);
    }

    @Test
    public void testTemplateConsistency() {
        String eventTitle = "Test Event";
        
        // All templates should include the event title
        assertTrue(NotificationTemplate.getAcceptedTitle(eventTitle).contains(eventTitle));
        assertTrue(NotificationTemplate.getAcceptedBody(eventTitle).contains(eventTitle));
        assertTrue(NotificationTemplate.getNotAcceptedTitle(eventTitle).contains(eventTitle));
        assertTrue(NotificationTemplate.getNotAcceptedBody(eventTitle).contains(eventTitle));
    }

    @Test
    public void testLongEventTitle() {
        String longTitle = "This is a very long event title that might cause formatting issues";
        String acceptedTitle = NotificationTemplate.getAcceptedTitle(longTitle);
        String notAcceptedTitle = NotificationTemplate.getNotAcceptedTitle(longTitle);
        
        assertTrue(acceptedTitle.contains(longTitle));
        assertTrue(notAcceptedTitle.contains(longTitle));
    }

    /**
     * Whitebox test for US 01.04.01: Win notification template.
     * Verifies that getAcceptedTitle and getAcceptedBody generate correct win notification content.
     */
    @Test
    public void testWinNotificationTemplate_US_01_04_01() {
        String eventTitle = "Summer Festival";
        
        // Test win notification title
        String winTitle = NotificationTemplate.getAcceptedTitle(eventTitle);
        assertEquals("🎉 Event Summer Festival – You Have Been Selected!", winTitle);
        assertTrue(winTitle.contains("You Have Been Selected"));
        assertTrue(winTitle.contains(eventTitle));
        
        // Test win notification body
        String winBody = NotificationTemplate.getAcceptedBody(eventTitle);
        assertTrue(winBody.contains("Congratulations"));
        assertTrue(winBody.contains("You have been selected"));
        assertTrue(winBody.contains(eventTitle));
    }

    /**
     * Whitebox test for US 01.04.02: Lose notification template.
     * Verifies that getNotAcceptedTitle and getNotAcceptedBody generate correct lose notification content.
     */
    @Test
    public void testLoseNotificationTemplate_US_01_04_02() {
        String eventTitle = "Winter Workshop";
        
        // Test lose notification title
        String loseTitle = NotificationTemplate.getNotAcceptedTitle(eventTitle);
        assertEquals("Event Winter Workshop – Waiting List Update", loseTitle);
        assertTrue(loseTitle.contains("Waiting List Update"));
        assertTrue(loseTitle.contains(eventTitle));
        
        // Test lose notification body
        String loseBody = NotificationTemplate.getNotAcceptedBody(eventTitle);
        assertTrue(loseBody.contains("Unfortunately"));
        assertTrue(loseBody.contains("not selected"));
        assertTrue(loseBody.contains("waiting list"));
        assertTrue(loseBody.contains(eventTitle));
    }

    /**
     * Whitebox test for US 01.04.01: Verifies win notification template format consistency.
     */
    @Test
    public void testWinNotificationFormatConsistency_US_01_04_01() {
        String[] eventTitles = {"Event A", "Event B", "Event C"};
        
        for (String eventTitle : eventTitles) {
            String title = NotificationTemplate.getAcceptedTitle(eventTitle);
            String body = NotificationTemplate.getAcceptedBody(eventTitle);
            
            // Both should contain the event title
            assertTrue(title.contains(eventTitle));
            assertTrue(body.contains(eventTitle));
            
            // Title should have the emoji and "Selected" text
            assertTrue(title.contains("🎉"));
            assertTrue(title.contains("Selected"));
            
            // Body should have congratulatory message
            assertTrue(body.contains("Congratulations"));
        }
    }

    /**
     * Whitebox test for US 01.04.02: Verifies lose notification template format consistency.
     */
    @Test
    public void testLoseNotificationFormatConsistency_US_01_04_02() {
        String[] eventTitles = {"Event X", "Event Y", "Event Z"};
        
        for (String eventTitle : eventTitles) {
            String title = NotificationTemplate.getNotAcceptedTitle(eventTitle);
            String body = NotificationTemplate.getNotAcceptedBody(eventTitle);
            
            // Both should contain the event title
            assertTrue(title.contains(eventTitle));
            assertTrue(body.contains(eventTitle));
            
            // Title should have "Waiting List Update"
            assertTrue(title.contains("Waiting List Update"));
            
            // Body should have "Unfortunately" and "waiting list"
            assertTrue(body.contains("Unfortunately"));
            assertTrue(body.contains("waiting list"));
        }
    }

    /**
     * Whitebox test: Verifies win and lose notifications are distinct.
     */
    @Test
    public void testWinLoseNotificationDistinction() {
        String eventTitle = "Test Event";
        
        String winTitle = NotificationTemplate.getAcceptedTitle(eventTitle);
        String loseTitle = NotificationTemplate.getNotAcceptedTitle(eventTitle);
        
        String winBody = NotificationTemplate.getAcceptedBody(eventTitle);
        String loseBody = NotificationTemplate.getNotAcceptedBody(eventTitle);
        
        // Titles should be different
        assertNotEquals(winTitle, loseTitle);
        
        // Bodies should be different
        assertNotEquals(winBody, loseBody);
        
        // Win notification should be positive
        assertTrue(winTitle.contains("Selected") || winBody.contains("Congratulations"));
        
        // Lose notification should indicate waiting list
        assertTrue(loseTitle.contains("Waiting List") || loseBody.contains("Unfortunately"));
    }
}




