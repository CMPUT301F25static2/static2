# **Story Points Scale**

| Story Points  | Relative Size   | Estimated Time     | Description                                     |
| ------------- | --------------- | ------------------ | ----------------------------------------------- |
| **1 point**   | Very Small      | Few hours to 1 day | Simple tasks, minimal code changes              |
| **2 points**  | Small           | 1-2 days           | Straightforward features with some complexity   |
| **3 points**  | Medium          | 2-3 days           | Standard features requiring multiple components |
| **5 points**  | Large           | 3-5 days           | Complex features with integrations              |
| **8 points**  | Very Large      | 1-2 weeks          | Highly complex, multiple integrations required  |
| **13 points** | Extremely Large | 2-3 weeks          | Most complex features, high technical challenge |



# Product Backlog Stories

## Entrant Stories

| ID          | User Story                                                   | Story Points | Risk Level | Half-way Release |
| ----------- | ------------------------------------------------------------ | ------------ | ---------- | ---------------- |
| US 01.01.01 | As an entrant, I want to join the waiting list for a specific event | 3            | Low        | ✅                |
| US 01.01.02 | As an entrant, I want to leave the waiting list for a specific event | 2            | Low        | ✅                |
| US 01.01.03 | As an entrant, I want to be able to see a list of events that I can join the waiting list for | 5            | Low        | ✅                |
| US 01.01.04 | As an entrant, I want to filter events based on my interests and availability | 8            | Medium     | ❌                |
| US 01.02.01 | As an entrant, I want to provide my personal information such as name, email and optional phone number in the app | 3            | Low        | ✅                |
| US 01.02.02 | As an entrant I want to update information such as name, email and contact information on my profile | 3            | Low        | ✅                |
| US 01.02.03 | As an entrant, I want to have a history of events I have registered for, whether I was selected or not | 5            | Medium     | ✅                |
| US 01.02.04 | As an entrant, I want to delete my profile if I no longer wish to use the app | 2            | Low        | ❌                |
| US 01.04.01 | As an entrant I want to receive notification when I am chosen to participate from the waiting list (when I "win" the lottery) | 5            | Medium     | ✅                |
| US 01.04.02 | As an entrant I want to receive notification of when I am not chosen on the app (when I "lose" the lottery) | 3            | Low        | ✅                |
| US 01.04.03 | As an entrant I want to opt out of receiving notifications from organizers and admins | 3            | Low        | ❌                |
| US 01.05.01 | As an entrant I want to have another chance to be chosen from the waiting list if a selected user declines an invitation to sign up | 5            | Medium     | ✅                |
| US 01.05.02 | As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event | 3            | Low        | ✅                |
| US 01.05.03 | As an entrant I want to be able to decline an invitation when chosen to participate in an event | 2            | Low        | ✅                |
| US 01.05.04 | As an entrant, I want to know how many total entrants are on the waiting list for an event | 2            | Low        | ✅                |
| US 01.05.05 | As an entrant, I want to be informed about the criteria or guidelines for the lottery selection process | 2            | Low        | ✅                |
| US 01.06.01 | As an entrant I want to view event details within the app by scanning the promotional QR code | 8            | High       | ✅                |
| US 01.06.02 | As an entrant I want to be able to sign up for an event from the event details | 3            | Low        | ✅                |
| US 01.07.01 | As an entrant, I want to be identified by my device, so that I don't have to use a username and password | 5            | Medium     | ✅                |



## Organizer Stories

| ID          | User Story                                                   | Story Points | Risk Level | Half-way Release |
| ----------- | ------------------------------------------------------------ | ------------ | ---------- | ---------------- |
| US 02.01.01 | As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app | 8            | High       | ✅                |
| US 02.01.04 | As an organizer, I want to set a registration period         | 3            | Low        | ✅                |
| US 02.02.01 | As an organizer I want to view the list of entrants who joined my event waiting list | 5            | Medium     | ✅                |
| US 02.02.02 | As an organizer I want to see on a map where entrants joined my event waiting list from | 13           | High       | ❌                |
| US 02.02.03 | As an organizer I want to enable or disable the geolocation requirement for my event | 5            | Medium     | ❌                |
| US 02.03.01 | As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list | 3            | Low        | ✅                |
| US 02.04.01 | As an organizer I want to upload an event poster to the event details page to provide visual information to entrants | 5            | Medium     | ✅                |
| US 02.04.02 | As an organizer I want to update an event poster to provide visual information to entrants | 3            | Low        | ❌                |
| US 02.05.01 | As an organizer I want to send a notification to chosen entrants to sign up for events (notification that they "won" the lottery) | 5            | Medium     | ✅                |
| US 02.05.02 | As an organizer I want to set the system to sample a specified number of attendees to register for the event | 8            | High       | ✅                |
| US 02.05.03 | As an organizer I want to be able to draw a replacement applicant from the pooling system when a previously selected applicant cancels or rejects the invitation | 5            | Medium     | ✅                |
| US 02.06.01 | As an organizer I want to view a list of all chosen entrants who are invited to apply | 3            | Low        | ✅                |
| US 02.06.02 | As an organizer I want to see a list of all the cancelled entrants | 3            | Low        | ✅                |
| US 02.06.03 | As an organizer I want to see a final list of entrants who enrolled for the event | 3            | Low        | ✅                |
| US 02.06.04 | As an organizer I want to cancel entrants that did not sign up for the event | 3            | Low        | ✅                |
| US 02.06.05 | As an organizer I want to export a final list of entrants who enrolled for the event in CSV format | 8            | High       | ❌                |
| US 02.07.01 | As an organizer I want to send notifications to all entrants on the waiting list | 5            | Medium     | ❌                |
| US 02.07.02 | As an organizer I want to send notifications to all selected entrants | 3            | Low        | ❌                |
| US 02.07.03 | As an organizer I want to send a notification to all cancelled entrants | 3            | Low        | ❌                |



## Admin Stories

| ID          | User Story                                                   | Story Points | Risk Level | Half-way Release |
| ----------- | ------------------------------------------------------------ | ------------ | ---------- | ---------------- |
| US 03.01.01 | As an administrator, I want to be able to remove events      | 3            | Low        | ✅                |
| US 03.02.01 | As an administrator, I want to be able to remove profiles    | 3            | Low        | ✅                |
| US 03.03.01 | As an administrator, I want to be able to remove images      | 2            | Low        | ❌                |
| US 03.04.01 | As an administrator, I want to be able to browse events      | 3            | Low        | ✅                |
| US 03.05.01 | As an administrator, I want to be able to browse profiles    | 3            | Low        | ✅                |
| US 03.06.01 | As an administrator, I want to be able to browse images that are uploaded so I can remove them if necessary | 3            | Low        | ❌                |
| US 03.07.01 | As an administrator I want to remove organizers that violate app policy | 3            | Low        | ❌                |
| US 03.08.01 | As an administrator, I want to review logs of all notifications sent to entrants by organizers | 8            | High       | ❌                |

------



## Risk Analysis Summary

### High Risk Features (13+ Story Points)
- US 01.06.01 - QR code scanning integration
- US 02.01.01 - Event creation with QR code generation
- US 02.02.02 - Geolocation mapping functionality
- US 02.05.02 - Lottery sampling algorithm
- US 02.06.05 - CSV export functionality
- US 03.08.01 - Notification log system

### Medium Risk Features (5-8 Story Points)
- Event filtering system
- Notification systems
- Geolocation toggle
- Image upload functionality
- Replacement applicant drawing

### Low Risk Features (1-3 Story Points)
- Basic CRUD operations (create, read, update, delete)
- Profile management
- Basic UI screens
- Simple notification settings

------

## Half-way Release Summary
- **Total Stories Selected**: 24 out of 45
- **Focus**: Core functionality for basic app operation
- **Excluded**: Advanced features, complex integrations, and nice-to-have enhancements