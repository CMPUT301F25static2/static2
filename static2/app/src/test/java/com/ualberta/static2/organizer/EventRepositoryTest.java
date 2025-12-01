package com.ualberta.static2.organizer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import android.net.Uri;

import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.repository.EventRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class EventRepositoryTest {

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private FirebaseStorage mockStorage;

    @Mock
    private DocumentReference mockDocRef;

    @Mock
    private StorageReference mockStorageRef;

    @Mock
    private UploadTask mockUploadTask;

    @Mock
    private Query mockQuery;

    private EventRepository eventRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the singleton instance
        eventRepository = spy(new EventRepository());
        eventRepository.db = mockDb;
        eventRepository.storage = mockStorage;
    }

    @Test
    public void testGetInstance_ReturnsSameInstance() {
        EventRepository instance1 = EventRepository.getInstance();
        EventRepository instance2 = EventRepository.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    public void testDocumentToEvent_NullDocument() {
        DocumentSnapshot nullDoc = null;
        Event result = eventRepository.documentToEvent(nullDoc);
        assertNull(result);
    }

    @Test
    public void testFromDocument_NullDocument() {
        DocumentSnapshot nullDoc = null;
        Event result = EventRepository.fromDocument(nullDoc);
        assertNull(result);
    }

    @Test
    public void testEventToMap() {
        Event event = new Event();
        event.setId("eventId123");
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setMaxAttendees(100);
        event.setCategory("TestCategory");
        event.setOrganizerId("orgId123");
        event.setStartTime(new Date());
        event.setEndTime(new Date());
        event.setRegistrationStart(new Date());
        event.setRegistrationEnd(new Date());
        event.setEventStatus(EventStatus.UPCOMING);
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        event.setPosterUrl("http://example.com/poster.jpg");
        event.setQrCodeUrl("http://example.com/qrcode.png");
        event.setLocation("Test Location");
        event.setLocationRequired(true);
        event.setLocationUrl("http://example.com/location");
        event.setCreatedAt(new Date());
        event.setConfirmedAttendees(50);

        Map<String, Object> result = eventRepository.eventToMap(event);

        assertNotNull(result);
        assertEquals("Test Event", result.get("title"));
        assertEquals("Test Description", result.get("description"));
        assertEquals(100, result.get("maxAttendees"));
        assertEquals("TestCategory", result.get("category"));
        assertEquals("orgId123", result.get("organizerId"));
        assertEquals(EventStatus.UPCOMING.toString(), result.get("eventStatus"));
        assertEquals(EventRegistrationStatus.REGISTRATION_OPEN.toString(), result.get("registrationStatus"));
        assertEquals("http://example.com/poster.jpg", result.get("posterUrl"));
        assertEquals("http://example.com/qrcode.png", result.get("qrCodeUrl"));
        assertEquals("Test Location", result.get("location"));
        assertEquals(true, result.get("locationRequired"));
        assertEquals("http://example.com/location", result.get("locationUrl"));
        assertNotNull(result.get("createdAt"));
        assertNotNull(result.get("updatedAt"));
        assertEquals(50, result.get("confirmedAttendees"));
    }

    @Test
    public void testUpdateEventStatus_EventNull() {
        eventRepository.updateEventStatus(null);
        // Should not crash
    }

    @Test
    public void testUpdateEventStatus_WithDates() {
        Event event = new Event();
        Date now = new Date();
        Date past = new Date(now.getTime() - 1000000);
        Date future = new Date(now.getTime() + 1000000);

        event.setStartTime(past);
        event.setEndTime(future);
        event.setRegistrationStart(past);
        event.setRegistrationEnd(future);
        event.setMaxAttendees(100);
        event.setConfirmedAttendees(50);

        eventRepository.updateEventStatus(event);

        assertEquals(EventStatus.ONGOING, event.getEventStatus());
        assertEquals(EventRegistrationStatus.REGISTRATION_OPEN, event.getRegistrationStatus());
    }

    @Test
    public void testUpdateEventStatus_EventFull() {
        Event event = new Event();
        Date now = new Date();
        Date past = new Date(now.getTime() - 1000000);
        Date future = new Date(now.getTime() + 1000000);

        event.setStartTime(past);
        event.setEndTime(future);
        event.setRegistrationStart(past);
        event.setRegistrationEnd(future);
        event.setMaxAttendees(100);
        event.setConfirmedAttendees(100); // Full capacity

        eventRepository.updateEventStatus(event);

        assertEquals(EventRegistrationStatus.REGISTRATION_CLOSED, event.getRegistrationStatus());
    }

    @Test
    public void testFindEventById_Success() {
        // Setup mocks
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        when(mockDoc.exists()).thenReturn(true);
        when(mockDoc.getId()).thenReturn("eventId123");
        when(mockDoc.getString("title")).thenReturn("Test Event");
        when(mockDoc.getString("description")).thenReturn("Test Description");
        when(mockDoc.getLong("maxAttendees")).thenReturn(100L);
        when(mockDoc.getString("category")).thenReturn("TestCategory");
        when(mockDoc.getString("organizerId")).thenReturn("orgId123");
        when(mockDoc.getDate("eventStart")).thenReturn(new Date());
        when(mockDoc.getDate("eventEnd")).thenReturn(new Date());
        when(mockDoc.getDate("registrationStart")).thenReturn(new Date());
        when(mockDoc.getDate("registrationEnd")).thenReturn(new Date());
        when(mockDoc.getString("dailyStartTime")).thenReturn("09:00:00");
        when(mockDoc.getString("dailyEndTime")).thenReturn("17:00:00");
        when(mockDoc.getString("eventStatus")).thenReturn("UPCOMING");
        when(mockDoc.getString("registrationStatus")).thenReturn("REGISTRATION_OPEN");
        when(mockDoc.getString("posterUrl")).thenReturn("http://example.com/poster.jpg");
        when(mockDoc.getBoolean("locationRequired")).thenReturn(true);

        // Setup the Firestore call
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).document(anyString())).thenReturn(mockDocRef);
        when(mockDocRef.get()).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        EventRepository.EventCallback mockCallback = mock(EventRepository.EventCallback.class);
        eventRepository.findEventById("eventId123", mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(any(Event.class));
    }

    @Test
    public void testGetEventsByOrganizer_Success() {
        // Setup mocks
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        List<DocumentSnapshot> documents = new ArrayList<>();
        documents.add(mockDoc);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documents);

        when(mockDoc.exists()).thenReturn(true);
        when(mockDoc.getId()).thenReturn("eventId123");
        when(mockDoc.getString("title")).thenReturn("Test Event");
        when(mockDoc.getString("description")).thenReturn("Test Description");
        when(mockDoc.getLong("maxAttendees")).thenReturn(100L);
        when(mockDoc.getString("category")).thenReturn("TestCategory");
        when(mockDoc.getString("organizerId")).thenReturn("orgId123");
        when(mockDoc.getDate("eventStart")).thenReturn(new Date());
        when(mockDoc.getDate("eventEnd")).thenReturn(new Date());
        when(mockDoc.getDate("registrationStart")).thenReturn(new Date());
        when(mockDoc.getDate("registrationEnd")).thenReturn(new Date());
        when(mockDoc.getString("dailyStartTime")).thenReturn("09:00:00");
        when(mockDoc.getString("dailyEndTime")).thenReturn("17:00:00");
        when(mockDoc.getString("eventStatus")).thenReturn("UPCOMING");
        when(mockDoc.getString("registrationStatus")).thenReturn("REGISTRATION_OPEN");
        when(mockDoc.getString("posterUrl")).thenReturn("http://example.com/poster.jpg");
        when(mockDoc.getBoolean("locationRequired")).thenReturn(true);

        // Setup the Firestore call
        when(mockDb.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(mockDb.collection(anyString()).whereEqualTo(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mock(com.google.android.gms.tasks.Task.class));

        // Call the method
        EventRepository.EventListCallback mockCallback = mock(EventRepository.EventListCallback.class);
        eventRepository.getEventsByOrganizer("orgId123", mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess(any(List.class));
    }

    @Test
    public void testAddEventWithPoster_Success() {
        // Setup mocks
        when(mockStorage.getReference()).thenReturn(mockStorageRef);
        when(mockStorageRef.child(anyString())).thenReturn(mockStorageRef);
        when(mockStorageRef.putFile(any(Uri.class))).thenReturn(mockUploadTask);
        when(mockUploadTask.continueWithTask(any())).thenReturn(mockUploadTask);
        when(mockUploadTask.isSuccessful()).thenReturn(true);
        when(mockUploadTask.getResult()).thenReturn(mock(Uri.class));

        // Setup Firestore
        when(mockDb.collection(anyString())).thenReturn(mock(CollectionReference.class));
        when(mockDb.collection(anyString()).add(any(Map.class))).thenReturn(mock(Task.class));

        // Call the method
        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setOrganizerId("orgId123");

        EventRepository.OperationCallback mockCallback = mock(EventRepository.OperationCallback.class);
        eventRepository.addEventWithPoster(event, mock(Uri.class), mockCallback);

        // Verify the callback was called
        verify(mockCallback, times(1)).onSuccess();
    }
}