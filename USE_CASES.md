# DrunkSafe - Use Case Diagrams (Selected Non-trivial Use Cases)

This file lists a selected set of non-trivial use cases for the DrunkSafe mobile application. Each use case includes:
- a PlantUML diagram you can render with PlantUML or a plugin
- a short textual description, main flow, alternate flows, pre- and post-conditions

Notes: To render the PlantUML blocks, save each block into a `.puml` file or use a PlantUML-enabled editor/extension.

---

**Use Case 1: Emergency Alert (SOS)**

@startuml
actor "User" as User
actor "Contacts (via SMS/Push)" as Contacts
actor "Firebase Firestore" as Firestore
actor "SMS/Push Service" as SMS

rectangle "DrunkSafe App" {
  User --> (Tap SOS button)
  (Tap SOS button) --> (Collect current location)
  (Collect current location) --> (Fetch trusted contacts)
  (Fetch trusted contacts) --> Firestore
  (Tap SOS button) --> (Compose alert message)
  (Compose alert message) --> (Send alert to contacts)
  (Send alert to contacts) --> SMS
  (Send alert to contacts) --> Contacts
  (Save alert event) --> Firestore
}
@enduml

Description: Sends an emergency alert with the user's current location to their trusted contacts and saves the event in Firestore.

Main flow:
1. User taps the SOS button.
2. App collects current GPS location.
3. App fetches the user's trusted contacts from Firestore.
4. App composes an alert message with a map link.
5. App sends the alert via SMS/push (or requests cloud function to send) and records the alert in Firestore.

Alternate flows:
- If location access is denied → prompt user to allow location or send message without precise coords.
- If no contacts configured → show error and offer to open Trusted Contacts setup.
- If SMS service fails → fallback to push notifications or store alert for retry.

Preconditions: User is authenticated; location permission allowed (optional but recommended).
Postconditions: Alert sent (or queued), alert record saved in Firestore.

---

**Use Case 2: Initial Setup — Add Trusted Contacts and Home Address**

@startuml
actor "New User" as NewUser
actor "Firebase Auth" as Auth
actor "Firestore" as Firestore

rectangle "DrunkSafe App" {
  NewUser --> (Open Setup screen)
  (Open Setup screen) --> (Add trusted contacts)
  (Add trusted contacts) --> Firestore
  (Open Setup screen) --> (Set home address)
  (Set home address) --> (Save home to preferences)
  (Complete setup) --> Firestore
  Auth --> (Create user account)
}
@enduml

Description: Guides a newly registered user to add trusted contacts and a home address, saving data locally and remotely.

Main flow:
1. After sign-up, app navigates user to Setup screen.
2. User adds names and phone numbers for trusted contacts.
3. Contacts are written to `contacts` collection in Firestore with `userId`.
4. User sets home address (text or map selection); saved locally (SharedPreferences) and to user profile in Firestore.
5. App marks `setupCompleted` = true in user's Firestore profile.

Alternate flows:
- Partial contact entries ignored when empty.
- If Firestore write fails, app retries and informs the user.

Preconditions: User account exists (Auth); connectivity recommended.
Postconditions: Contacts saved in Firestore; home address saved locally and in user profile.

---

**Use Case 3: Navigate to Home (Directions & Route Display)**

@startuml
actor "User" as User
actor "Google Directions API" as DirectionsAPI
actor "Geocoder/Maps" as Maps

rectangle "DrunkSafe App" {
  User --> (Request Navigate Home)
  (Request Navigate Home) --> (Load home location from prefs)
  (Request Navigate Home) --> (Get current location)
  (Compose origin/destination) --> (Call Directions API)
  (Call Directions API) --> DirectionsAPI
  DirectionsAPI --> (Return route polyline + legs)
  (Display route) --> Maps
}
@enduml

Description: User requests navigation to the saved home address; app fetches route using Google Directions API and renders polyline and distance/duration on the map.

Main flow:
1. User taps "Navigate Home".
2. App loads saved home LatLng from `HomeAddressPreferences`.
3. App obtains current location (device GPS).
4. App calls Directions API (via `DirectionsRepository` / Retrofit) with origin and destination.
5. App decodes the polyline and displays it on Google Map; shows distance and duration.

Alternate flows:
- If home address not set → prompt user to set home address in Profile.
- If Directions API returns no routable path → inform user there is no route.
- If network error → show retry option.

Preconditions: Home address saved; location permission allowed.
Postconditions: Route displayed; navigation state enabled.

---

**Use Case 4: Sign Up with Email & Create Profile**

@startuml
actor "Visitor" as Visitor
actor "Firebase Auth" as Auth
actor "Firestore" as Firestore

rectangle "DrunkSafe App" {
  Visitor --> (Enter email/password/display name)
  (Submit sign-up) --> (AuthRepository.signUp)
  (AuthRepository.signUp) --> Auth
  Auth --> (Return FirebaseUser)
  (Create user profile) --> Firestore
}
@enduml

Description: New users create an account; the app registers the user in Firebase Auth, then creates a corresponding `UserProfile` in Firestore with `setupCompleted=false`.

Main flow:
1. Visitor fills sign-up form and submits.
2. App calls `AuthRepository.signUp` to create Firebase user and set display name.
3. On success, app creates `UserProfile` in Firestore with `setupCompleted = false`.
4. App navigates to Setup screen to complete initial configuration.

Alternate flows:
- Email already in use → show error.
- Network error or Auth failure → show retry and explanation.

Preconditions: Internet connection; valid email format.
Postconditions: Firebase Auth account created; Firestore `users` document created.

---

**Use Case 5: Manage Trusted Contacts (Search / Add / Delete)**

@startuml
actor "User" as User
actor "Firestore" as Firestore

rectangle "DrunkSafe App" {
  User --> (Open Trusted Contacts screen)
  (Open Trusted Contacts screen) --> (View contacts list)
  (Search contacts) --> (Filter locally via ViewModel)
  (Add contact) --> (ContactsRepository.addContact)
  (Delete contact) --> (ContactsRepository.deleteContact)
  (Add/Delete) --> Firestore
}
@enduml

Description: User manages trusted contacts; the UI shows a reactive list that updates via realtime Firestore snapshots.

Main flow:
1. User opens Trusted Contacts screen.
2. `TrustedContactsViewModel` subscribes to `ContactsRepository.getContactsFlow()`.
3. Firestore snapshot listener pushes contact updates to the app, updating UI.
4. The user can add a contact (written to Firestore) or delete an entry.
5. The ViewModel filters the list according to the search query locally.

Alternate flows:
- If Firestore listener returns error → show fallback empty state and retry.

Preconditions: User authenticated.
Postconditions: Firestore `contacts` collection updated; UI reflects changes in realtime.

---

## Quick Rendering Tips

- Render PlantUML blocks with PlantUML (CLI or VS Code extension).
  Example CLI usage (requires `plantuml.jar` and Java):

```powershell
java -jar plantuml.jar USE_CASES.md -o output_folder
```

- Or copy each `@startuml`..`@enduml` block into a `.puml` file and render it.

---

If you want, I can:
- generate PNG/SVG images for each PlantUML diagram and add them to the repo, or
- produce simplified diagrams in Portuguese, or
- convert these into a single PlantUML file with multiple diagrams.

Which would you prefer next?