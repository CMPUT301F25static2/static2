package com.ualberta.eventlottery.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RegistrationRepository {
    private static RegistrationRepository instance;
    private static String COLLECTION_REGISTRATIONS = "registrations";
    private List<Registration> registrationCache;
    private EntrantRepository entrantRepository;
    private FirebaseFirestore db;

    public interface RegistrationCallback {
        void onSuccess(Registration registration);
        void onFailure(Exception e);
    }


    private RegistrationRepository() {
        registrationCache = new ArrayList<>();
        entrantRepository = EntrantRepository.getInstance();
        db = FirebaseFirestore.getInstance();
        initializeSampleData();
    }

    public static synchronized RegistrationRepository getInstance() {
        if (instance == null) {
            instance = new RegistrationRepository();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Sample registrations for testing
        Registration reg1 = new Registration("reg1", "1", "entrant1");
        reg1.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg1.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 3));


        Registration reg2 = new Registration("reg2", "1", "entrant2");
        reg2.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg2.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 2));

        Registration reg3 = new Registration("reg3", "1", "entrant3");
        reg3.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg3.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 1));

        Registration reg4 = new Registration("reg4", "1", "entrant4");
        reg4.setStatus(EntrantRegistrationStatus.WAITING);
        reg4.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 4));

        Registration reg5 = new Registration("reg5", "1", "entrant5");
        reg5.setStatus(EntrantRegistrationStatus.WAITING);
        reg5.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 3));

        Registration reg6 = new Registration("reg6", "1", "entrant6");
        reg6.setStatus(EntrantRegistrationStatus.WAITING);
        reg6.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 2));

        Registration reg7 = new Registration("reg7", "1", "entrant7");
        reg7.setStatus(EntrantRegistrationStatus.SELECTED);
        reg7.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 5));

        Registration reg8 = new Registration("reg8", "1", "entrant8");
        reg8.setStatus(EntrantRegistrationStatus.SELECTED);
        reg8.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 4));

        Registration reg9 = new Registration("reg9", "1", "entrant9");
        reg9.setStatus(EntrantRegistrationStatus.DECLINED);
        reg9.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 6));
        reg9.setRespondedAt(new Date(System.currentTimeMillis() - 86400000 * 5));

        Registration reg10 = new Registration("reg10", "1", "entrant10");
        reg10.setStatus(EntrantRegistrationStatus.DECLINED);
        reg10.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 5));
        reg10.setRespondedAt(new Date(System.currentTimeMillis() - 86400000 * 4));

        Registration reg11 = new Registration("reg11", "2", "entrant1");
        reg11.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg11.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 7));

        Registration reg12 = new Registration("reg12", "2", "entrant2");
        reg12.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg12.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 6));

        Registration reg13 = new Registration("reg13", "2", "entrant3");
        reg13.setStatus(EntrantRegistrationStatus.WAITING);
        reg13.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 5));

        Registration reg14 = new Registration("reg14", "2", "entrant4");
        reg14.setStatus(EntrantRegistrationStatus.WAITING);
        reg14.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 4));

        Registration reg15 = new Registration("reg15", "2", "entrant5");
        reg15.setStatus(EntrantRegistrationStatus.SELECTED);
        reg15.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 3));

        Registration reg16 = new Registration("reg16", "3", "entrant6");
        reg16.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg16.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 10));

        Registration reg17 = new Registration("reg17", "3", "entrant7");
        reg17.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg17.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 9));

        Registration reg18 = new Registration("reg18", "3", "entrant8");
        reg18.setStatus(EntrantRegistrationStatus.CONFIRMED);
        reg18.setRegisteredAt(new Date(System.currentTimeMillis() - 86400000 * 8));


        registrationCache.add(reg1);
        registrationCache.add(reg2);
        registrationCache.add(reg3);
        registrationCache.add(reg4);
        registrationCache.add(reg5);
        registrationCache.add(reg6);
        registrationCache.add(reg7);
        registrationCache.add(reg8);
        registrationCache.add(reg9);
        registrationCache.add(reg10);
        registrationCache.add(reg11);
        registrationCache.add(reg12);
        registrationCache.add(reg13);
        registrationCache.add(reg14);
        registrationCache.add(reg15);
        registrationCache.add(reg16);
        registrationCache.add(reg17);
        registrationCache.add(reg18);
    }

    public Registration findRegistrationById(String registrationId) {
        for (Registration reg : registrationCache) {
            if (reg.getId().equals(registrationId)) {
                return reg;
            }
        }
        return null;
    }

    public Registration findRegistrationByEventAndUser(String eventId, String userId) {
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId) && reg.getEntrantId().equals(userId)) {
                return reg;
            }
        }
        return null;
    }

    public List<Registration> getRegistrationsByEvent(String eventId) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId)) {
                result.add(reg);
            }
        }
        return result;
    }

    public List<Registration> getRegistrationsByEntrant(String entrantId) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrationCache) {
            if (reg.getEntrantId().equals(entrantId)) {
                result.add(reg);
            }
        }
        return result;
    }

    public List<Registration> getRegistrationsByStatus(String eventId, EntrantRegistrationStatus status) {
        List<Registration> result = new ArrayList<>();
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId) && reg.getStatus() == status) {
                result.add(reg);
            }
        }
        return result;
    }

    public List<Registration> getRegistrationsByEntrantAndStatus(String entrantId, EntrantRegistrationStatus status) {
        List<Registration> registrations = new ArrayList<>();
        for (Registration reg : registrationCache) {
            if (reg.getEntrantId().equals(entrantId) && reg.getStatus() == status) {
                registrations.add(reg);
            }
        }
        return registrations;
    }

    public List<Registration> getRegistrationsByEventAndStatus(String eventId, EntrantRegistrationStatus status) {
        List<Registration> registrations = new ArrayList<>();
        for (Registration reg : registrationCache) {

            if (reg.getEventId().equals(eventId) && reg.getStatus() == status) {
                registrations.add(reg);
            }
        }
        return registrations;

    }

    public List<Registration> getAllRegistrations() {
        return registrationCache;
    }
    public void addRegistration(Registration registration) {
        registrationCache.add(registration);
    }

    public boolean updateRegistration(Registration updatedRegistration) {
        for (int i = 0; i < registrationCache.size(); i++) {
            if (registrationCache.get(i).getId().equals(updatedRegistration.getId())) {
                registrationCache.set(i, updatedRegistration);
                return true;
            }
        }
        return false;
    }

    public boolean deleteRegistration(String registrationId) {
        for (int i = 0; i < registrationCache.size(); i++) {
            if (registrationCache.get(i).getId().equals(registrationId)) {
                registrationCache.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getRegistrationCountByStatus(String eventId, EntrantRegistrationStatus status) {
        int count = 0;
        for (Registration reg : registrationCache) {
            if (reg.getEventId().equals(eventId) && reg.getStatus() == status) {
                count++;
            }
        }
        return count;
    }
    public void findRegistrationByEventAndUser(String eventId, String userId, RegistrationCallback callback) {
        queryRegistrationByEventAndUser(eventId, userId)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Registration registration = querySnapshot.getDocuments().get(0).toObject(Registration.class);
                        callback.onSuccess(registration);
                    } else {
                        callback.onSuccess(null); // No registration found
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }


    private Task<QuerySnapshot> queryRegistrationByEventAndUser(String eventId, String userId) {
        return db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("entrantId", userId)
                .limit(1)
                .get();
    }

    public void registerUser(String eventId, String userId, RegistrationCallback callback) {
        findRegistrationByEventAndUser(eventId, userId, new RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                if (registration != null) {
                    // Already registered so nothing to do.
                    callback.onSuccess(registration);
                } else {
                    final Registration newRegistration = new Registration();
                    DocumentReference docRef = db.collection(COLLECTION_REGISTRATIONS).document();
                    newRegistration.setId(docRef.getId());
                    newRegistration.setEventId(eventId);
                    newRegistration.setEntrantId(userId);
                    newRegistration.setStatus(EntrantRegistrationStatus.WAITING);
                    newRegistration.setRegisteredAt(new Date());

                    docRef.set(newRegistration)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(newRegistration))
                            .addOnFailureListener(callback::onFailure);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void unregisterUser(String eventId, String userId, RegistrationCallback callback) {
        findRegistrationByEventAndUser(eventId, userId, new RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                if (registration != null) {
                    queryRegistrationByEventAndUser(eventId, userId)
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                    db.collection(COLLECTION_REGISTRATIONS)
                                            .document(doc.getId())
                                            .delete()
                                            .addOnSuccessListener(v -> {
                                                callback.onSuccess(null);
                                            })
                                            .addOnFailureListener(callback::onFailure);
                                } else {
                                    // Not registered yet so nothing to do
                                    callback.onSuccess(null);
                                }
                            })
                            .addOnFailureListener(callback::onFailure);
                } else {
                    // Not yet registered so nothing to do.
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


}