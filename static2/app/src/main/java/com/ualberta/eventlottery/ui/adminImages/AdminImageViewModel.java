package com.ualberta.eventlottery.ui.adminImages;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ualberta.eventlottery.model.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for admin images screen.
 * Manages fetching and deleting images from Firebase.
 */
public class AdminImageViewModel extends ViewModel {
    private static final String TAG = "AdminImageViewModel";
    private final MutableLiveData<List<ImageItem>> images = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public AdminImageViewModel() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        loadImages();
    }

    public LiveData<List<ImageItem>> getImages() {
        return images;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Loads all images from Firestore (currently only event posters).
     */
    public void loadImages() {
        isLoading.setValue(true);
        List<ImageItem> imageList = new ArrayList<>();

        // Fetch all events and extract poster URLs
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String posterUrl = document.getString("posterUrl");
                        if (posterUrl != null && !posterUrl.isEmpty()) {
                            String eventId = document.getId();
                            String title = document.getString("title");

                            // Extract storage path from URL
                            String storagePath = extractStoragePathFromUrl(posterUrl);

                            ImageItem imageItem = new ImageItem(
                                    posterUrl,
                                    "EVENT_POSTER",
                                    eventId,
                                    title != null ? title : "Untitled Event",
                                    storagePath
                            );
                            imageList.add(imageItem);
                        }
                    }
                    images.setValue(imageList);
                    isLoading.setValue(false);
                    Log.d(TAG, "Loaded " + imageList.size() + " images");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading images", e);
                    errorMessage.setValue("Failed to load images: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    /**
     * Deletes an image from Firebase Storage and updates Firestore.
     */
    public void deleteImage(ImageItem imageItem, OnDeleteCompleteListener listener) {
        if (imageItem.getImageUrl() == null || imageItem.getImageUrl().isEmpty()) {
            listener.onFailure("Invalid image URL");
            return;
        }

        // Delete from Firebase Storage
        StorageReference imageRef = storage.getReferenceFromUrl(imageItem.getImageUrl());
        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Image deleted from storage");

                    // Update Firestore to remove the poster URL
                    if ("EVENT_POSTER".equals(imageItem.getImageType())) {
                        db.collection("events")
                                .document(imageItem.getAssociatedId())
                                .update("posterUrl", null)
                                .addOnSuccessListener(aVoid1 -> {
                                    Log.d(TAG, "Event posterUrl updated in Firestore");
                                    listener.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error updating Firestore", e);
                                    listener.onFailure("Failed to update event: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting image from storage", e);
                    listener.onFailure("Failed to delete image: " + e.getMessage());
                });
    }

    /**
     * Extracts the storage path from a Firebase Storage URL.
     */
    private String extractStoragePathFromUrl(String url) {
        try {
            // URL format: https://firebasestorage.googleapis.com/v0/b/{bucket}/o/{path}?...
            int startIndex = url.indexOf("/o/") + 3;
            int endIndex = url.indexOf("?");
            if (startIndex > 2 && endIndex > startIndex) {
                return url.substring(startIndex, endIndex).replace("%2F", "/");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting storage path", e);
        }
        return null;
    }

    public interface OnDeleteCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }
}
