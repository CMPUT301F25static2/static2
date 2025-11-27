package com.ualberta.eventlottery.model;

/**
 * Model class representing an image in the system.
 * Used by admin to view and manage all uploaded images.
 */
public class ImageItem {
    private String imageUrl;
    private String imageType; // "EVENT_POSTER" or "PROFILE_PICTURE"
    private String associatedId; // eventId or userId
    private String title; // Event title or User name
    private String storagePath; // Firebase Storage path for deletion

    public ImageItem() {
    }

    public ImageItem(String imageUrl, String imageType, String associatedId, String title, String storagePath) {
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.associatedId = associatedId;
        this.title = title;
        this.storagePath = storagePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(String associatedId) {
        this.associatedId = associatedId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}

