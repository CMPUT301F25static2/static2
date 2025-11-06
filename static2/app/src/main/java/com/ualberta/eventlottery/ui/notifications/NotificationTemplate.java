package com.ualberta.eventlottery.ui.notifications;

/**
 * Provides reusable templates for notification titles and messages.
 * Used to format consistent notifications for entrants based on event outcomes.
 */
public class NotificationTemplate {

    /**
     * Returns the title for an accepted entrant notification.
     *
     * @param eventTitle the title of the event
     * @return formatted title text
     */
    public static String getAcceptedTitle(String eventTitle) {
        return "🎉 Event " + eventTitle + " – You Have Been Selected!";
    }

    /**
     * Returns the body text for an accepted entrant notification.
     *
     * @param eventTitle the title of the event
     * @return formatted message body
     */
    public static String getAcceptedBody(String eventTitle) {
        return "Congratulations! You have been selected to attend the event \""
                + eventTitle
                + "\". Please confirm your attendance as soon as possible.";
    }

    /**
     * Returns the title for a not-accepted (waiting list) notification.
     *
     * @param eventTitle the title of the event
     * @return formatted title text
     */
    public static String getNotAcceptedTitle(String eventTitle) {
        return "Event " + eventTitle + " – Waiting List Update";
    }

    /**
     * Returns the body text for a not-accepted (waiting list) notification.
     *
     * @param eventTitle the title of the event
     * @return formatted message body
     */
    public static String getNotAcceptedBody(String eventTitle) {
        return "Unfortunately, you were not selected for the event \""
                + eventTitle
                + "\" at this time. You remain on the waiting list and will be notified if a spot opens up.";
    }
}
