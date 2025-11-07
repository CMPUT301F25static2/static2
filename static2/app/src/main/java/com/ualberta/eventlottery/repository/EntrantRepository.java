package com.ualberta.eventlottery.repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.Entrant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class for managing Entrant data operations with Firebase Firestore.
 * This class follows the Singleton pattern to provide a single instance
 * for handling all entrant-related database operations.
 *
 * Responsibilities:
 * - CRUD operations for Entrant entities
 * - Data conversion between Entrant objects and Firestore documents
 * - Sample data initialization for testing and demonstration
 *
 * @author static2
 *
 */
public class EntrantRepository {
    private static EntrantRepository instance;
    private FirebaseFirestore db;
    private static final String COLLECTION_ENTRANTS = "users";

    // Callback interfaces

    /**
     * Callback interface for single Entrant operations.
     */
    public interface EntrantCallback {
        /**
         * Called when the entrant operation is successful.
         *
         * @param entrant the retrieved or processed Entrant object
         */
        void onSuccess(Entrant entrant);

        /**
         * Called when the entrant operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for multiple Entrant operations.
     */
    public interface EntrantListCallback {
        /**
         * Called when the entrant list operation is successful.
         *
         * @param entrants the list of retrieved Entrant objects
         */
        void onSuccess(List<Entrant> entrants);

        /**
         * Called when the entrant list operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for basic operations without return values.
     */
    public interface OperationCallback {
        /**
         * Called when the operation is successful.
         */
        void onSuccess();

        /**
         * Called when the operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for operations that return a boolean result.
     */
    public interface BooleanCallback {
        /**
         * Called when the operation is successful.
         *
         * @param result the boolean result of the operation
         */
        void onSuccess(boolean result);

        /**
         * Called when the operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Private constructor for Singleton pattern.
     * Initializes Firebase Firestore instance .
     */
    private EntrantRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Gets the singleton instance of EntrantRepository.
     *
     * @return the singleton EntrantRepository instance
     */
    public static synchronized EntrantRepository getInstance() {
        if (instance == null) {
            instance = new EntrantRepository();
        }
        return instance;
    }

    /**
     * Converts a Firestore DocumentSnapshot to an Entrant object.
     *
     * @param document the Firestore document snapshot to convert
     * @return the converted Entrant object, or null if conversion fails
     */
    private Entrant documentToEntrant(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        try {
            Entrant entrant = new Entrant();
            entrant.setUserId(document.getId());
            entrant.setName(document.getString("name"));
            entrant.setEmail(document.getString("email"));
            entrant.setPhone(document.getString("phoneNumber"));

            return entrant;
        } catch (Exception e) {
            Log.e("EntrantRepository", "Error converting document to Entrant", e);
            return null;
        }
    }

    /**
     * Converts an Entrant object to a Firestore data map.
     * @param entrant the Entrant object to convert
     * @return a Map containing the entrant data for Firestore
     */
    private Map<String, Object> entrantToMap(Entrant entrant) {
        Map<String, Object> entrantMap = new HashMap<>();
        entrantMap.put("name", entrant.getName());
        entrantMap.put("email", entrant.getEmail());
        entrantMap.put("phoneNumber", entrant.getPhone());
        entrantMap.put("userId", entrant.getUserId());

        return entrantMap;
    }

    /**
     * Finds an entrant by their unique identifier.
     * @param entrantId the unique identifier of the entrant to find
     * @param callback the callback to handle the result of the operation
     */
    public void findEntrantById(String entrantId, EntrantCallback callback) {
        db.collection(COLLECTION_ENTRANTS)
                .document(entrantId)
                .get()
                .addOnSuccessListener(document -> {
                    Entrant entrant = documentToEntrant(document);
                    if (entrant != null) {
                        callback.onSuccess(entrant);
                    } else {
                        callback.onFailure(new Exception("Entrant not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves all entrants from the database.
     * @param callback the callback to handle the list of entrants
     */
    public void getAllEntrants(EntrantListCallback callback) {
        db.collection(COLLECTION_ENTRANTS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Entrant> entrants = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Entrant entrant = documentToEntrant(document);
                        if (entrant != null) {
                            entrants.add(entrant);
                        }
                    }
                    callback.onSuccess(entrants);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Adds a new entrant to the database.
     * @param entrant the Entrant object to add
     * @param callback the callback to handle the operation result
     */
    public void addEntrant(Entrant entrant, OperationCallback callback) {
        if (entrant.getUserId() == null || entrant.getUserId().isEmpty()) {
            String newId = db.collection(COLLECTION_ENTRANTS).document().getId();
            entrant.setUserId(newId);
        }

        Map<String, Object> entrantData = entrantToMap(entrant);
        db.collection(COLLECTION_ENTRANTS)
                .document(entrant.getUserId())
                .set(entrantData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Updates an existing entrant in the database.
     *
     * @param updatedEntrant the Entrant object with updated information
     * @param callback the callback to handle the operation result
     */
    public void updateEntrant(Entrant updatedEntrant, BooleanCallback callback) {
        Map<String, Object> entrantData = entrantToMap(updatedEntrant);
        db.collection(COLLECTION_ENTRANTS)
                .document(updatedEntrant.getUserId())
                .update(entrantData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Deletes an entrant from the database by their ID.
     *
     * @param entrantId the unique identifier of the entrant to delete
     * @param callback the callback to handle the operation result
     */
    public void deleteEntrant(String entrantId, BooleanCallback callback) {
        db.collection(COLLECTION_ENTRANTS)
                .document(entrantId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }


}