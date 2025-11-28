package com.ualberta.static2.organizer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.repository.RegistrationRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationRepositoryTest {

    @Mock
    private FirebaseFirestore mockDb;

    private RegistrationRepository registrationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the singleton instance
        registrationRepository = spy(new RegistrationRepository());
        registrationRepository.db = mockDb;
    }

    @Test
    public void testGetInstance_ReturnsSameInstance() {
        RegistrationRepository instance1 = RegistrationRepository.getInstance();
        RegistrationRepository instance2 = RegistrationRepository.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    public void testDocumentToRegistration_NullDocument() {
        DocumentSnapshot nullDoc = null;
        Registration result = registrationRepository.documentToRegistration(nullDoc);
        assertNull(result);
    }

    @Test
    public void testRegistrationToMap() {
        Registration registration = new Registration();
        registration.setId("regId123");
        registration.setEventId("eventId123");
        registration.setEntrantId("entrantId123");
        registration.setStatus(EntrantRegistrationStatus.WAITING);
        registration.setRegisteredAt(new Date(1234567890000L));
        registration.setRespondedAt(new Date(1234567890000L));
        registration.setCancelledAt(new Date(1234567890000L));
        registration.setLatitude(40.7128);
        registration.setLongitude(-74.0060);
        registration.setLocationAddress("New York, NY");

        Map<String, Object> result = registrationRepository.registrationToMap(registration);

        assertNotNull(result);
        assertEquals("regId123", result.get("id"));
        assertEquals("eventId123", result.get("eventId"));
        assertEquals("entrantId123", result.get("entrantId"));
        assertEquals("WAITING", result.get("status"));
        assertEquals(new Date(1234567890000L), result.get("registeredAt"));
        assertEquals(new Date(1234567890000L), result.get("respondedAt"));
        assertEquals(new Date(1234567890000L), result.get("cancelledAt"));
        assertEquals(40.7128, result.get("latitude"));
        assertEquals(-74.0060, result.get("longitude"));
        assertEquals("New York, NY", result.get("locationAddress"));
        assertNotNull(result.get("updatedAt"));
    }

    @Test
    public void testFindRegistrationById_Success() {
        // Setup mocks
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        when(mockDoc.exists()).thenReturn(true);
        when(mockDoc.getId()).thenReturn("regId123");
        when(mockDoc.getString("eventId")).thenReturn("eventId123");
        when(mockDoc.getString("entrantId")).thenReturn("entrantId123");
        when(mockDoc.getString("status")).thenReturn("WAITING");
        when(mockDoc.getDate("registeredAt")).thenReturn(new Date());
        when(mockDoc.getDate("respondedAt")).thenReturn(new Date());
        when(mockDoc.getDate("cancelledAt")).thenReturn(new Date());
        when(mockDoc.getDouble("latitude")).thenReturn(40.7128);
        when(mockDoc.getDouble("longitude")).thenReturn(-74.0060);
        when(mockDoc.getString("locationAddress")).thenReturn("New York, NY");

        // Setup the Firestore call
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).document(anyString())).thenReturn(mock(com.google.firebase.firestore.DocumentReference.class));
        when(mock(com.google.firebase.firestore.DocumentReference.class).get()).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        RegistrationRepository.RegistrationCallback mockCallback = mock(RegistrationRepository.RegistrationCallback.class);
        registrationRepository.findRegistrationById("regId123", mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(any(Registration.class));
    }

    @Test
    public void testFindRegistrationByEventAndUser_Success() {
        // Setup mocks
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        List<DocumentSnapshot> documents = new ArrayList<>();
        documents.add(mockDoc);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documents);

        when(mockDoc.exists()).thenReturn(true);
        when(mockDoc.getId()).thenReturn("regId123");
        when(mockDoc.getString("eventId")).thenReturn("eventId123");
        when(mockDoc.getString("entrantId")).thenReturn("entrantId123");
        when(mockDoc.getString("status")).thenReturn("WAITING");
        when(mockDoc.getDate("registeredAt")).thenReturn(new Date());
        when(mockDoc.getDate("respondedAt")).thenReturn(new Date());
        when(mockDoc.getDate("cancelledAt")).thenReturn(new Date());
        when(mockDoc.getDouble("latitude")).thenReturn(40.7128);
        when(mockDoc.getDouble("longitude")).thenReturn(-74.0060);
        when(mockDoc.getString("locationAddress")).thenReturn("New York, NY");

        // Setup the Firestore call
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).whereEqualTo(anyString(), anyString())).thenReturn(mock(Query.class));
        when(mock(Query.class).get()).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        RegistrationRepository.RegistrationCallback mockCallback = mock(RegistrationRepository.RegistrationCallback.class);
        registrationRepository.findRegistrationByEventAndUser("eventId123", "entrantId123", mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(any(Registration.class));
    }

    @Test
    public void testGetRegistrationsByEvent_Success() {
        // Setup mocks
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        List<DocumentSnapshot> documents = new ArrayList<>();
        documents.add(mockDoc);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documents);

        when(mockDoc.exists()).thenReturn(true);
        when(mockDoc.getId()).thenReturn("regId123");
        when(mockDoc.getString("eventId")).thenReturn("eventId123");
        when(mockDoc.getString("entrantId")).thenReturn("entrantId123");
        when(mockDoc.getString("status")).thenReturn("WAITING");
        when(mockDoc.getDate("registeredAt")).thenReturn(new Date());
        when(mockDoc.getDate("respondedAt")).thenReturn(new Date());
        when(mockDoc.getDate("cancelledAt")).thenReturn(new Date());
        when(mockDoc.getDouble("latitude")).thenReturn(40.7128);
        when(mockDoc.getDouble("longitude")).thenReturn(-74.0060);
        when(mockDoc.getString("locationAddress")).thenReturn("New York, NY");

        // Setup the Firestore call
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).whereEqualTo(anyString(), anyString())).thenReturn(mock(Query.class));
        when(mock(Query.class).get()).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        RegistrationRepository.RegistrationListCallback mockCallback = mock(RegistrationRepository.RegistrationListCallback.class);
        registrationRepository.getRegistrationsByEvent("eventId123", mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(any(List.class));
    }

    @Test
    public void testAddRegistration_Success() {
        // Setup mocks
        DocumentReference mockDocRef = mock(DocumentReference.class);
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).document()).thenReturn(mockDocRef);
        when(mockDocRef.getId()).thenReturn("regId123");
        when(mockDocRef.set(any(Map.class))).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        Registration registration = new Registration();
        registration.setEventId("eventId123");
        registration.setEntrantId("entrantId123");
        registration.setStatus(EntrantRegistrationStatus.WAITING);

        RegistrationRepository.OperationCallback mockCallback = mock(RegistrationRepository.OperationCallback.class);
        registrationRepository.addRegistration(registration, mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess();
    }

    @Test
    public void testUpdateRegistration_Success() {
        // Setup mocks
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).document(anyString())).thenReturn(mock(com.google.firebase.firestore.DocumentReference.class));
        when(mock(com.google.firebase.firestore.DocumentReference.class).update(any(Map.class))).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        Registration registration = new Registration();
        registration.setId("regId123");
        registration.setEventId("eventId123");
        registration.setEntrantId("entrantId123");
        registration.setStatus(EntrantRegistrationStatus.WAITING);

        RegistrationRepository.BooleanCallback mockCallback = mock(RegistrationRepository.BooleanCallback.class);
        registrationRepository.updateRegistration(registration, mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(true);
    }

    @Test
    public void testDeleteRegistration_Success() {
        // Setup mocks
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).document(anyString())).thenReturn(mock(com.google.firebase.firestore.DocumentReference.class));
        when(mock(com.google.firebase.firestore.DocumentReference.class).delete()).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        RegistrationRepository.BooleanCallback mockCallback = mock(RegistrationRepository.BooleanCallback.class);
        registrationRepository.deleteRegistration("regId123", mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(true);
    }

    @Test
    public void testGetRegistrationCountByStatus_Success() {
        // Setup mocks
        Query mockQuery = mock(Query.class);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        when(mockQuery.get()).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        RegistrationRepository.CountCallback mockCallback = mock(RegistrationRepository.CountCallback.class);
        registrationRepository.getRegistrationCountByStatus("eventId123", EntrantRegistrationStatus.WAITING, mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(0);
    }
}