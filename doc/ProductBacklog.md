# **Product Backlog Stories with Descriptions**

## **Entrant Stories**

| ID | User Story | Story Points | Risk Level | Half-way Release | Short Description | Implementation Description |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **US 01.01.01** | As an entrant, I want to join the waiting list for a specific event | 3 | Low | ✅ | Join an event waiting list | Provide a button to join waiting list and update event entry in database with entrant info |
| **US 01.01.02** | As an entrant, I want to leave the waiting list for a specific event | 2 | Low | ✅ | Leave event waiting list | Remove entrant from the event waiting list in the database |
| **US 01.01.03** | As an entrant, I want to be able to see a list of events that I can join the waiting list for | 5 | Low | ✅ | View available events | Fetch events from backend and display in a scrollable list|
| **US 01.01.04** | As an entrant, I want to filter events based on my interests and availability | 8 | Medium | ❌ | Filter events | Implement filtering on attributes on the events list |
| **US 01.02.01** | As an entrant, I want to provide my personal information such as name, email and optional phone number in the app | 3 | Low | ✅ | Add profile info | Create a profile form to save entrant information in the database |
| **US 01.02.02** | As an entrant I want to update information such as name, email and contact information on my profile | 3 | Low | ✅ | Update profile | Add profile editing functionality |
| **US 01.02.03** | As an entrant, I want to have a history of events I have registered for, whether I was selected or not | 5 | Medium | ✅ | Event history | Retrieve past events from database and display in chronological order |
| **US 01.02.04** | As an entrant, I want to delete my profile if I no longer wish to use the app | 2 | Low | ❌ | Delete profile | Add profile deletion functionality |
| **US 01.04.01** | As an entrant I want to receive notification when I am chosen to participate from the waiting list (when I "win" the lottery) | 5 | Medium | ✅ | Win notification | Send push and in-app notification when the system selects entrant |
| **US 01.04.02** | As an entrant I want to receive notification of when I am not chosen on the app (when I "lose" the lottery) | 3 | Low | ✅ | Lose notification | Send push and in-app notification when the system does not select entrant  |
| **US 01.04.03** | As an entrant I want to opt out of receiving notifications from organizers and admins | 3 | Low | ❌ | Notification opt-out | Add toggle in profile settings to unsubscribe from notifications |
| **US 01.05.01** | As an entrant I want to have another chance to be chosen from the waiting list if a selected user declines an invitation to sign up | 5 | Medium | ❌ | Replacement chance | Implement logic to draw replacement entrants|
| **US 01.05.02** | As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event | 3 | Low | ❌ | Accept invitation | Provide accept button and update registration status in database |
| **US 01.05.03** | As an entrant I want to be able to decline an invitation when chosen to participate in an event | 2 | Low | ✅ | Decline invitation | Add decline button and free up slot for another entrant |
| **US 01.05.04** | As an entrant, I want to know how many total entrants are on the waiting list for an event | 2 | Low | ✅ | View waiting list count | Show waiting list count retrieved from backend on event page |
| **US 01.05.05** | As an entrant, I want to be informed about the criteria or guidelines for the lottery selection process | 2 | Low | ✅ | Lottery guidelines | Display static or dynamic guidelines on event details page |
| **US 01.06.01** | As an entrant I want to view event details within the app by scanning the promotional QR code | 8 | High | ✅ | QR code event view | Integrate QR code scanner to fetch event details |
| **US 01.06.02** | As an entrant I want to be able to sign up for an event from the event details | 3 | Low | ❌ | Sign up from details | Add sign-up button and update database registration |
| **US 01.07.01** | As an entrant, I want to be identified by my device, so that I don't have to use a username and password | 5 | Medium | ✅ | Device-based login | Implement device ID authentication and session management |

---

## **Organizer Stories**

| ID | User Story | Story Points | Risk Level | Half-way Release | Short Description | Implementation Description |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **US 02.01.01** | As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app | 8 | High | ❌ | Event creation & QR code | Form to create event and generate QR code linking to details |
| **US 02.01.04** | As an organizer, I want to set a registration period | 3 | Low | ✅ | Set registration window | Add date pickers to define start and end registration dates |
| **US 02.02.01** | As an organizer I want to view the list of entrants who joined my event waiting list | 5 | Medium | ✅ | View entrants | Fetch waiting list from backend and display |
| **US 02.02.02** | As an organizer I want to see on a map where entrants joined my event waiting list from | 13 | High | ❌ | Map entrants | Integrate mapping API to show entrant geolocation pins |
| **US 02.02.03** | As an organizer I want to enable or disable the geolocation requirement for my event | 5 | Medium | ❌ | Geolocation toggle | Add checkbox to enable/disable geolocation validation for sign-ups |
| **US 02.03.01** | As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list | 3 | Low | ✅ | Limit entrants | Add max entrants field and enforce in backend |
| **US 02.04.01** | As an organizer I want to upload an event poster to the event details page to provide visual information to entrants | 5 | Medium | ❌ | Upload poster | File upload form with image storage and display |
| **US 02.04.02** | As an organizer I want to update an event poster to provide visual information to entrants | 3 | Low | ❌ | Update poster | Replace existing poster with new image in backend |
| **US 02.05.01** | As an organizer I want to send a notification to chosen entrants to sign up for events (notification that they "won" the lottery) | 5 | Medium | ✅ | Notify winners | Trigger push or in-app notification for selected entrants |
| **US 02.05.02** | As an organizer I want to set the system to sample a specified number of attendees to register for the event | 8 | High | ✅ | Set lottery size | Implement lottery sampling logic |
| **US 02.05.03** | As an organizer I want to be able to draw a replacement applicant from the pooling system when a previously selected applicant cancels or rejects the invitation | 5 | Medium | ❌ | Draw replacement | Select replacement from waiting list when slot opens |
| **US 02.06.01** | As an organizer I want to view a list of all chosen entrants who are invited to apply | 3 | Low | ✅ | View invited | Display list of entrants selected for event |
| **US 02.06.02** | As an organizer I want to see a list of all the cancelled entrants | 3 | Low | ✅ | View cancelled | Display list of entrants who declined |
| **US 02.06.03** | As an organizer I want to see a final list of entrants who enrolled for the event | 3 | Low | ✅ | Finalized list | Fetch and display enrolled entrants |
| **US 02.06.04** | As an organizer I want to cancel entrants that did not sign up for the event | 3 | Low | ✅ | Cancel non-registered | Remove non-registered entrants from selected list |
| **US 02.06.05** | As an organizer I want to export a final list of entrants who enrolled for the event in CSV format | 8 | High | ❌ | Export CSV | Generate CSV file from backend data for download |
| **US 02.07.01** | As an organizer I want to send notifications to all entrants on the waiting list | 5 | Medium | ✅ | Notify waiting list | Bulk push/in-app notifications to waiting list entrants |
| **US 02.07.02** | As an organizer I want to send notifications to all selected entrants | 3 | Low | ❌ | Notify selected | Send push/in-app notifications to chosen entrants |
| **US 02.07.03** | As an organizer I want to send a notification to all cancelled entrants | 3 | Low | ❌ | Notify cancelled | Send push/in-app notifications to entrants removed from list |

---

## **Admin Stories**

| ID | User Story | Story Points | Risk Level | Half-way Release | Short Description | Implementation Description |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **US 03.01.01** | As an administrator, I want to be able to remove events | 3 | Low | ✅ | Remove events | Add delete button with backend removal for events |
| **US 03.02.01** | As an administrator, I want to be able to remove profiles | 3 | Low | ✅ | Remove profiles | Admin can delete user profiles from backend |
| **US 03.03.01** | As an administrator, I want to be able to remove images | 2 | Low | ❌ | Remove images | Delete uploaded images from storage |
| **US 03.04.01** | As an administrator, I want to be able to browse events | 3 | Low | ✅ | Browse events | Display all events with filters and search |
| **US 03.05.01** | As an administrator, I want to be able to browse profiles | 3 | Low | ✅ | Browse profiles | Display profiles with search |
| **US 03.06.01** | As an administrator, I want to be able to browse images that are uploaded so I can remove them if necessary | 3 | Low | ❌ | Browse & manage images | List uploaded images and allow deletion |
| **US 03.07.01** | As an administrator I want to remove organizers that violate app policy | 3 | Low | ❌ | Remove organizers | Admin can deactivate organizer accounts |
| **US 03.08.01** | As an administrator, I want to review logs of all notifications sent to entrants by organizers | 8 | High | ❌ | Review notifications | Display notification logs |

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
