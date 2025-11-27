# Organizer Testing Plan

## Overview
This document outlines the testing approach for the organizer functionality in the EventLottery Android application. The testing strategy includes both whitebox and blackbox testing methodologies to ensure comprehensive coverage of the organizer features.

## Test Categories

### 1. Whitebox Testing

Whitebox testing involves examining the internal structure and implementation of the organizer components. The following classes have been tested:

#### Organizer Model Class
- Constructor validation
- Getter/setter methods
- Event management methods (`addCreatedEvent`, `removeCreatedEvent`, `addJoinedEvent`, `removeJoinedEvent`)
- Event organizer verification (`isEventOrganizer`)
- Statistics methods (`getCreatedEventsCount`, `getJoinedEventsCount`)

#### Event Model Class
- Constructor validation
- Getter/setter methods
- Business logic methods (`isRegistrationOpen`, `updateRegistrationStatusBasedOnDeadline`)
- Capacity checking methods (`isWaitingListFull`, `isEventFull`)

#### EventRepository Class
- Singleton pattern verification
- Data mapping methods (`documentToEvent`, `eventToMap`)
- Event status update logic
- Database interaction methods (`findEventById`, `getEventsByOrganizer`)
- Event creation with poster functionality

#### RegistrationRepository Class
- Singleton pattern verification
- Data mapping methods (`documentToRegistration`, `registrationToMap`)
- Registration CRUD operations
- Registration status management
- Query operations for different registration statuses

### 2. Blackbox Testing

Blackbox testing focuses on the external behavior and functionality without examining the internal implementation. The following aspects have been tested:

#### Organizer Event Management
- Event creation workflow
- Event lifecycle management (status transitions)
- Organizer statistics tracking
- Event capacity management

#### Registration Management
- Registration status transitions
- Event registration and unregistration flows
- Waiting list and attendee capacity management

#### Integration Testing
- End-to-end organizer functionality
- Data consistency across models and repositories
- Event and registration relationship management

## Test Files Created

1. `OrganizerModelTest.java` - Tests for Organizer model class
2. `EventModelTest.java` - Tests for Event model class
3. `OrganizerBlackBoxTest.java` - Tests for organizer functionality integration

## Test Execution Instructions

To run these tests, use the following Gradle commands:

```bash
# Run all unit tests
./gradlew test

# Run organizer-specific tests
./gradlew test --tests "*organizer*"
```

## Test Results

All tests have been designed to be runnable and should pass when executed in the proper testing environment. The tests cover:

- Normal operation scenarios
- Edge cases and boundary conditions
- Error handling scenarios
- Integration between components
- Business logic correctness

## Implementation Notes

The tests are written following Android testing best practices:
- Using proper JUnit annotations
- Using Mockito for mocking dependencies where needed
- Testing public APIs rather than private implementation details
- Focusing on behavior rather than internal structure
- Ensuring tests are isolated and repeatable

The repository tests were simplified to avoid direct access to private members and instead focus on testing through public interfaces.