package com.ualberta.static2.entrant;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Custom Matchers for espresso testing
 * Source:
 * https://www.google.com/search?q=android+espresso+match+first+view+with+id&oq=android+espresso+match+first+view+with+id&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIHCAEQIRigATIHCAIQIRigATIHCAMQIRigATIHCAQQIRigATIHCAUQIRiPAtIBCTIzNzQ1ajBqNKgCALACAQ&sourceid=chrome&ie=UTF-8
 */
public class CustomMatchers {

    /**
     * Allows for finding the first instance of a view
     * @param matcher Matcher Object to find
     * @return Matcher</T>
     * @param <T>
     */
    public static <T> Matcher<T> first(final Matcher<T> matcher) {
        return new TypeSafeMatcher<T>() {
            boolean isFirst = true;

            @Override
            public void describeTo(Description description) {
                description.appendText("returns first matching view for: ");
                matcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(T item) {
                if (matcher.matches(item) && isFirst) {
                    isFirst = false;
                    return true;
                }
                return false;
            }
        };
    }
}





