package com.ualberta.static2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.platform.app.InstrumentationRegistry;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.ImageItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for US 03.03.01: As an administrator, I want to be able to remove images.
 * Tests the admin image removal functionality.
 */
public class AdminImageRemovalTest {

    /**
     * Test that an ImageItem can be created with event poster data
     */
    @Test
    public void testImageItemCreation() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        String imageUrl = "https://firebasestorage.googleapis.com/test/poster.jpg";
        String imageType = "EVENT_POSTER";
        String eventId = "event-123";
        String title = "Test Event";
        String storagePath = "posters/poster-uuid.jpg";

        ImageItem imageItem = new ImageItem(imageUrl, imageType, eventId, title, storagePath);

        assertNotNull("ImageItem should be created", imageItem);
        assertEquals("Image URL should match", imageUrl, imageItem.getImageUrl());
        assertEquals("Image type should be EVENT_POSTER", imageType, imageItem.getImageType());
        assertEquals("Associated ID should match event ID", eventId, imageItem.getAssociatedId());
        assertEquals("Title should match event title", title, imageItem.getTitle());
        assertEquals("Storage path should match", storagePath, imageItem.getStoragePath());
    }

    /**
     * Test that event poster URL can be removed (set to null)
     */
    @Test
    public void testRemoveEventPosterUrl() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create event with poster
        Event event = new Event("event-1", "organizer-1", "Sample Event", "Description");
        String posterUrl = "https://firebasestorage.googleapis.com/test/poster.jpg";
        event.setPosterUrl(posterUrl);

        // Verify poster is set
        assertNotNull("Poster URL should be set", event.getPosterUrl());
        assertEquals("Poster URL should match", posterUrl, event.getPosterUrl());

        // Simulate admin removing the poster
        event.setPosterUrl(null);

        // Verify poster is removed
        assertNull("Poster URL should be null after removal", event.getPosterUrl());
    }

    /**
     * Test that multiple images can be loaded into a list
     */
    @Test
    public void testLoadMultipleImages() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<ImageItem> imageList = new ArrayList<>();

        // Add multiple event posters
        imageList.add(new ImageItem(
                "https://storage.com/poster1.jpg",
                "EVENT_POSTER",
                "event-1",
                "Event One",
                "posters/poster1.jpg"
        ));

        imageList.add(new ImageItem(
                "https://storage.com/poster2.jpg",
                "EVENT_POSTER",
                "event-2",
                "Event Two",
                "posters/poster2.jpg"
        ));

        imageList.add(new ImageItem(
                "https://storage.com/poster3.jpg",
                "EVENT_POSTER",
                "event-3",
                "Event Three",
                "posters/poster3.jpg"
        ));

        assertEquals("Should have 3 images", 3, imageList.size());
        assertEquals("First image should be Event One", "Event One", imageList.get(0).getTitle());
        assertEquals("Second image should be Event Two", "Event Two", imageList.get(1).getTitle());
        assertEquals("Third image should be Event Three", "Event Three", imageList.get(2).getTitle());
    }

    /**
     * Test that an image can be removed from a list
     */
    @Test
    public void testRemoveImageFromList() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<ImageItem> imageList = new ArrayList<>();

        ImageItem image1 = new ImageItem(
                "https://storage.com/poster1.jpg",
                "EVENT_POSTER",
                "event-1",
                "Event One",
                "posters/poster1.jpg"
        );

        ImageItem image2 = new ImageItem(
                "https://storage.com/poster2.jpg",
                "EVENT_POSTER",
                "event-2",
                "Event Two",
                "posters/poster2.jpg"
        );

        imageList.add(image1);
        imageList.add(image2);

        assertEquals("Should have 2 images initially", 2, imageList.size());

        // Remove first image (simulate admin deleting it)
        imageList.remove(0);

        assertEquals("Should have 1 image after removal", 1, imageList.size());
        assertEquals("Remaining image should be Event Two", "Event Two", imageList.get(0).getTitle());
    }

    /**
     * Test filtering images by type
     */
    @Test
    public void testFilterImagesByType() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<ImageItem> allImages = new ArrayList<>();

        allImages.add(new ImageItem(
                "https://storage.com/poster1.jpg",
                "EVENT_POSTER",
                "event-1",
                "Event One",
                "posters/poster1.jpg"
        ));

        allImages.add(new ImageItem(
                "https://storage.com/profile1.jpg",
                "PROFILE_PICTURE",
                "user-1",
                "User One",
                "profiles/profile1.jpg"
        ));

        allImages.add(new ImageItem(
                "https://storage.com/poster2.jpg",
                "EVENT_POSTER",
                "event-2",
                "Event Two",
                "posters/poster2.jpg"
        ));

        // Filter for event posters only
        List<ImageItem> eventPosters = new ArrayList<>();
        for (ImageItem image : allImages) {
            if ("EVENT_POSTER".equals(image.getImageType())) {
                eventPosters.add(image);
            }
        }

        assertEquals("Should have 2 event posters", 2, eventPosters.size());
        assertTrue("All filtered items should be event posters",
                eventPosters.stream().allMatch(img -> "EVENT_POSTER".equals(img.getImageType())));
    }

    /**
     * Test that empty image list is handled correctly
     */
    @Test
    public void testEmptyImageList() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<ImageItem> imageList = new ArrayList<>();

        assertTrue("List should be empty initially", imageList.isEmpty());
        assertEquals("List size should be 0", 0, imageList.size());
    }

    /**
     * Test that image with null URL is handled correctly
     */
    @Test
    public void testImageWithNullUrl() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        ImageItem imageItem = new ImageItem(
                null,
                "EVENT_POSTER",
                "event-1",
                "Event One",
                "posters/poster1.jpg"
        );

        assertNull("Image URL should be null", imageItem.getImageUrl());
        assertNotNull("Image should still have other properties", imageItem.getTitle());
    }

    /**
     * Test removing image when event has no poster
     */
    @Test
    public void testRemoveNonExistentPoster() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        Event event = new Event("event-1", "organizer-1", "Sample Event", "Description");

        // Event has no poster initially
        assertNull("Poster URL should be null initially", event.getPosterUrl());

        // Try to remove poster (should not cause error)
        event.setPosterUrl(null);

        assertNull("Poster URL should still be null", event.getPosterUrl());
    }

    /**
     * Test that storage path can be extracted correctly
     */
    @Test
    public void testStoragePathHandling() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        String storagePath = "posters/uuid-12345.jpg";
        ImageItem imageItem = new ImageItem(
                "https://storage.com/image.jpg",
                "EVENT_POSTER",
                "event-1",
                "Test Event",
                storagePath
        );

        assertEquals("Storage path should match", storagePath, imageItem.getStoragePath());
        assertTrue("Storage path should contain 'posters'", imageItem.getStoragePath().contains("posters"));
    }

    /**
     * Test bulk removal of images
     */
    @Test
    public void testBulkImageRemoval() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<ImageItem> imageList = new ArrayList<>();

        // Add 5 images
        for (int i = 1; i <= 5; i++) {
            imageList.add(new ImageItem(
                    "https://storage.com/poster" + i + ".jpg",
                    "EVENT_POSTER",
                    "event-" + i,
                    "Event " + i,
                    "posters/poster" + i + ".jpg"
            ));
        }

        assertEquals("Should have 5 images initially", 5, imageList.size());

        // Remove images at indices 1 and 3 (remove from end to avoid index shifting)
        imageList.remove(3);
        imageList.remove(1);

        assertEquals("Should have 3 images after removal", 3, imageList.size());
    }
}

