package com.ualberta.eventlottery.repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.Entrant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantRepository {
    private static EntrantRepository instance;
    private FirebaseFirestore db;
    private static final String COLLECTION_ENTRANTS = "users";

    // Callback interfaces
    public interface EntrantCallback {
        void onSuccess(Entrant entrant);
        void onFailure(Exception e);
    }

    public interface EntrantListCallback {
        void onSuccess(List<Entrant> entrants);
        void onFailure(Exception e);
    }

    public interface OperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface BooleanCallback {
        void onSuccess(boolean result);
        void onFailure(Exception e);
    }

    private EntrantRepository() {
        db = FirebaseFirestore.getInstance();
        initializeSampleData();
    }

    public static synchronized EntrantRepository getInstance() {
        if (instance == null) {
            instance = new EntrantRepository();
        }
        return instance;
    }

    /**
     * Converts Firestore document to Entrant object
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
     * Converts Entrant object to Firestore data map
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
     * Finds an entrant by ID
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
     * Retrieves all entrants
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
     * Adds a new entrant
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
     * Updates an existing entrant
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
     * Deletes an entrant by ID
     */
    public void deleteEntrant(String entrantId, BooleanCallback callback) {
        db.collection(COLLECTION_ENTRANTS)
                .document(entrantId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Initialize sample data (for testing/demo purposes)
     */
    private void initializeSampleData() {
        // Check if collection is empty, then add sample data
        db.collection(COLLECTION_ENTRANTS).get().addOnSuccessListener(querySnapshot -> {
            if (querySnapshot.isEmpty()) {
                addSampleEntrants();
            }
        });
    }

    private void addSampleEntrants() {
        List<Entrant> sampleEntrants = new ArrayList<>();
        sampleEntrants.add(new Entrant("entrant1", "John Doe", "john.doe@ualberta.ca", "780-111-1111", "test"));
        sampleEntrants.add(new Entrant("entrant2", "Jane Smith", "jane.smith@ualberta.ca", "780-111-1112", "test"));
        sampleEntrants.add(new Entrant("entrant3", "Bob Johnson", "bob.johnson@ualberta.ca", "780-111-1113", "test"));
        sampleEntrants.add(new Entrant("entrant4", "Alice Brown", "alice.brown@ualberta.ca", "780-111-1114", "test"));
        sampleEntrants.add(new Entrant("entrant5", "Charlie Wilson", "charlie.wilson@ualberta.ca", "780-111-1115", "test"));
        sampleEntrants.add(new Entrant("entrant6", "Diana Lee", "diana.lee@ualberta.ca", "780-111-1116", "test"));
        sampleEntrants.add(new Entrant("entrant7", "Edward Zhang", "edward.zhang@ualberta.ca", "780-111-1117", "test"));
        sampleEntrants.add(new Entrant("entrant8", "Fiona Chen", "fiona.chen@ualberta.ca", "780-111-1118", "test"));
        sampleEntrants.add(new Entrant("entrant9", "George Kumar", "george.kumar@ualberta.ca", "780-111-1119", "test"));
        sampleEntrants.add(new Entrant("entrant10", "Helen Park", "helen.park@ualberta.ca", "780-111-1120", "test"));

        for (Entrant entrant : sampleEntrants) {
            addEntrant(entrant, new OperationCallback() {
                @Override
                public void onSuccess() {
                    Log.d("EntrantRepository", "Sample entrant added: " + entrant.getName());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("EntrantRepository", "Failed to add sample entrant: " + entrant.getName(), e);
                }
            });
        }
    }


}