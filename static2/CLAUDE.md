# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application called "EventLottery" built with Java and Firebase. The app allows users to participate in event lotteries with three distinct user roles:
- **Entrant**: Participants who can join event waiting lists
- **Organizer**: Event creators who manage events and draws
- **Admin**: System administrators with oversight capabilities

## Technology Stack

- **Language**: Java
- **Build System**: Gradle (Kotlin DSL)
- **UI Framework**: Android SDK with Material Design components
- **Backend**: Firebase (Authentication, Firestore, Cloud Messaging)
- **Testing**: JUnit, Espresso, Mockito, Robolectric

## Project Structure

```
static2/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ualberta/eventlottery/ - Main source code
│   │   │   └── res/ - Resources (layouts, drawables, values, etc.)
│   │   ├── test/ - Unit tests
│   │   └── androidTest/ - Instrumentation tests
│   └── build.gradle.kts - App-level build configuration
├── build.gradle.kts - Top-level build configuration
└── gradle/ - Gradle wrapper and properties
```

## Common Development Commands

### Building the Project

```bash
# On Windows
./gradlew.bat build

# On macOS/Linux
./gradlew build
```

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run a specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ualberta.static2.entrant.EntrantMainActivityTest
```

### Installing Debug APK

```bash
# Install the debug APK on a connected device/emulator
./gradlew installDebug
```

## Architecture & Key Components

### Firebase Integration
The app heavily relies on Firebase services:
- **Firebase Authentication**: User authentication
- **Firebase Firestore**: Database for storing events, users, and registrations
- **Firebase Messaging**: Push notifications
- **Firebase Functions**: Server-side logic

### Main Activities
- `MainActivity`: Entry point that routes users to their respective roles
- `EntrantMainActivity`: Main interface for entrants
- `OrganizerMainActivity`: Main interface for organizers
- `AdminMainActivity`: Administrative interface
- `ProfileSetupActivity`: User profile configuration

### Testing Framework
The project uses Android instrumentation testing with Espresso:
- UI testing with Espresso framework
- Idling resources for asynchronous operations
- Custom matchers for specific view interactions
- Tests organized by user role functionality

## Key Features Implemented

Based on the test files, the app implements these user stories:
- US 01.01.01: Entrants can join waiting lists for events
- US 01.01.02: Entrants can leave waiting lists for events
- US 01.01.03: Entrants can see a list of events they can join

## Development Patterns

- Fragment-based navigation
- MVVM (Model-View-ViewModel) architecture pattern
- Repository pattern for data access
- Firebase integration for real-time data synchronization
- View binding for UI components