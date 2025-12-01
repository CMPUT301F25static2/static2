package com.ualberta.static2.notification;

import static org.junit.Assert.*;

import com.ualberta.eventlottery.model.User;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for User model notification opt-out functionality.
 * Tests US 01.04.03: As an entrant I want to opt out of receiving notifications 
 * from organizers and admins.
 * 
 * These are whitebox tests that verify the internal implementation of the 
 * notificationsEnabled field in the User model.
 */
public class UserNotificationOptOutTest {

    private User user;

    @Before
    public void setUp() {
        user = new User("userId123", "Test User", "test@example.com", "1234567890", 
                       "fcmToken123", "entrant", "Rec Center", true);
    }

    /**
     * Whitebox test for US 01.04.03: Verifies that notificationsEnabled field 
     * can be set and retrieved correctly.
     */
    @Test
    public void testNotificationsEnabledGetterSetter_US_01_04_03() {
        // Test initial state (set to true in constructor)
        assertTrue(user.getNotificationsEnabled());

        // Test setting to false (opt-out)
        user.setNotificationsEnabled(false);
        assertFalse(user.getNotificationsEnabled());

        // Test setting back to true (opt-in)
        user.setNotificationsEnabled(true);
        assertTrue(user.getNotificationsEnabled());
    }

    /**
     * Whitebox test for US 01.04.03: Verifies that user can opt out by setting 
     * notificationsEnabled to false.
     */
    @Test
    public void testOptOut_US_01_04_03() {
        // Initially enabled
        assertTrue(user.getNotificationsEnabled());

        // Opt out
        user.setNotificationsEnabled(false);
        assertFalse(user.getNotificationsEnabled());
    }

    /**
     * Whitebox test for US 01.04.03: Verifies that user can opt back in by setting 
     * notificationsEnabled to true.
     */
    @Test
    public void testOptIn_US_01_04_03() {
        // Opt out first
        user.setNotificationsEnabled(false);
        assertFalse(user.getNotificationsEnabled());

        // Opt back in
        user.setNotificationsEnabled(true);
        assertTrue(user.getNotificationsEnabled());
    }

    /**
     * Whitebox test for US 01.04.03: Verifies default value when notificationsEnabled 
     * is not explicitly set in constructor.
     */
    @Test
    public void testDefaultNotificationsEnabled_US_01_04_03() {
        // Create user without notificationsEnabled parameter
        User defaultUser = new User("user1", "Name", "email@test.com", "phone", "token");
        
        // Default should be null (not set)
        assertNull(defaultUser.getNotificationsEnabled());
    }

    /**
     * Whitebox test for US 01.04.03: Verifies that notificationsEnabled can be set 
     * to null (which should be treated as opt-out in the NotificationController).
     */
    @Test
    public void testNotificationsEnabledNull_US_01_04_03() {
        user.setNotificationsEnabled(null);
        assertNull(user.getNotificationsEnabled());
    }

    /**
     * Whitebox test for US 01.04.03: Verifies constructor with notificationsEnabled parameter.
     */
    @Test
    public void testConstructorWithNotificationsEnabled_US_01_04_03() {
        // Test with notifications enabled
        User enabledUser = new User("user1", "Name", "email@test.com", "phone", 
                                     "token", "entrant", "Rec Center", true);
        assertTrue(enabledUser.getNotificationsEnabled());

        // Test with notifications disabled
        User disabledUser = new User("user2", "Name", "email@test.com", "phone", 
                                     "token", "entrant", "Rec Center", false);
        assertFalse(disabledUser.getNotificationsEnabled());
    }

    /**
     * Whitebox test for US 01.04.03: Verifies that multiple users can have 
     * different notification preferences.
     */
    @Test
    public void testMultipleUsersDifferentPreferences_US_01_04_03() {
        User user1 = new User("user1", "User 1", "user1@test.com", "phone", 
                            "token", "entrant", "Rec Center", true);
        User user2 = new User("user2", "User 2", "user2@test.com", "phone", 
                            "token", "entrant", "Rec Center", false);

        assertTrue(user1.getNotificationsEnabled());
        assertFalse(user2.getNotificationsEnabled());

        // User 1 opts out
        user1.setNotificationsEnabled(false);
        assertFalse(user1.getNotificationsEnabled());
        assertFalse(user2.getNotificationsEnabled());

        // User 2 opts in
        user2.setNotificationsEnabled(true);
        assertFalse(user1.getNotificationsEnabled());
        assertTrue(user2.getNotificationsEnabled());
    }

    /**
     * Whitebox test for US 01.04.03: Verifies that the notificationsEnabled field 
     * persists through object state changes.
     */
    @Test
    public void testNotificationsEnabledPersistence_US_01_04_03() {
        // Set to false
        user.setNotificationsEnabled(false);
        assertFalse(user.getNotificationsEnabled());

        // Modify other fields
        user.setName("New Name");
        user.setEmail("newemail@test.com");

        // notificationsEnabled should still be false
        assertFalse(user.getNotificationsEnabled());
    }
}

