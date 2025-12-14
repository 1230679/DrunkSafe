# DrunkSafe - Component Diagrams

This document contains comprehensive component diagrams illustrating the architecture and interactions of the DrunkSafe mobile application.

## 1. High-Level Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      DrunkSafe Application                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    UI Layer (Compose)                    │  │
│  │  ┌──────────────┬──────────────┬──────────────┐           │  │
│  │  │  Login UI    │  Map Screen  │  Profile UI  │           │  │
│  │  │  Setup UI    │  Emergency   │  Contacts UI │           │  │
│  │  └──────────────┴──────────────┴──────────────┘           │  │
│  └────────────────────┬─────────────────────────────────────┘  │
│                       │                                         │
│  ┌────────────────────▼─────────────────────────────────────┐  │
│  │              ViewModel Layer (MVVM)                      │  │
│  │  ┌──────────────┬──────────────┬──────────────┐           │  │
│  │  │  LoginVM     │  MapVM       │  SetupVM     │           │  │
│  │  │  TrustedVM   │  ProfileVM   │  EmergencyVM │           │  │
│  │  └──────────────┴──────────────┴──────────────┘           │  │
│  └────────────────────┬─────────────────────────────────────┘  │
│                       │                                         │
│  ┌────────────────────▼─────────────────────────────────────┐  │
│  │            Repository Layer (Data Access)               │  │
│  │  ┌──────────────┬──────────────┬──────────────┐           │  │
│  │  │  AuthRepo    │  UserRepo    │  ContactsRepo│           │  │
│  │  │  DirectionsRepo │ StorageRepo │ Preferences │           │  │
│  │  └──────────────┴──────────────┴──────────────┘           │  │
│  └────────────────────┬─────────────────────────────────────┘  │
│                       │                                         │
│  ┌────────────────────▼─────────────────────────────────────┐  │
│  │         External Services Integration Layer             │  │
│  │  ┌──────────────┬──────────────┬──────────────┐           │  │
│  │  │  Firebase    │  Google Maps │  Retrofit    │           │  │
│  │  │  Services    │  API         │  HTTP Client │           │  │
│  │  └──────────────┴──────────────┴──────────────┘           │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Data Flow Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    User Interactions                         │
│           (Login, Map Navigation, Emergency)                 │
└──────────────────────┬───────────────────────────────────────┘
                       │
        ┌──────────────▼──────────────┐
        │   UI Screens (Composables)  │
        │  - LoginScreen              │
        │  - MapHomeScreen            │
        │  - EmergencyScreen          │
        │  - ProfileScreen            │
        │  - TrustedContactsScreen    │
        └──────────────┬───────────────┘
                       │ Updates State
        ┌──────────────▼──────────────┐
        │    ViewModels (MVVM)        │
        │  - Collect from Repositories│
        │  - Manage UI State          │
        │  - Handle Business Logic    │
        └──────────────┬───────────────┘
                       │ Calls Methods
        ┌──────────────▼──────────────┐
        │   Repositories (Data Layer) │
        │  - AuthRepository           │
        │  - UserRepository           │
        │  - ContactsRepository       │
        │  - DirectionsRepository     │
        └──────────────┬───────────────┘
                       │
        ┌──────────────▼──────────────┐
        │   External Services         │
        │  - Firebase Auth            │
        │  - Firestore (Database)     │
        │  - Google Maps API          │
        │  - Google Directions API    │
        └─────────────────────────────┘
```

---

## 3. Authentication & User Management Component

```
┌────────────────────────────────────────────────────────┐
│          Authentication & User Management              │
├────────────────────────────────────────────────────────┤
│                                                        │
│  ┌───────────────────────────────────────────────┐    │
│  │        LoginScreen / SignUpScreen UI          │    │
│  │  ┌──────────────────┬──────────────────┐      │    │
│  │  │  Email Input     │  Password Input  │      │    │
│  │  │  Sign In Button  │  Sign Up Button  │      │    │
│  │  │  Reset Password  │  Display Name    │      │    │
│  │  └──────────────────┴──────────────────┘      │    │
│  └──────────────────┬──────────────────────────┘    │
│                     │ emits events                   │
│  ┌──────────────────▼──────────────────────────┐    │
│  │        LoginViewModel                       │    │
│  │  • signIn(email, password)                  │    │
│  │  • signUp(email, password, displayName)     │    │
│  │  • resetPassword(email)                     │    │
│  │  • signOut()                                │    │
│  │  • uiState: StateFlow<AuthUiState>          │    │
│  └──────────────────┬──────────────────────────┘    │
│                     │ delegates to                   │
│  ┌──────────────────▼──────────────────────────┐    │
│  │        AuthRepository                       │    │
│  │  • signIn(): FirebaseUser?                  │    │
│  │  • signUp(): FirebaseUser?                  │    │
│  │  • sendPasswordReset()                      │    │
│  │  • signOut()                                │    │
│  │  • currentUser(): FirebaseUser?             │    │
│  └──────────────────┬──────────────────────────┘    │
│                     │ uses                          │
│  ┌──────────────────▼──────────────────────────┐    │
│  │  Firebase Authentication Service            │    │
│  │  • Email/Password Authentication            │    │
│  │  • User Profile Management                  │    │
│  │  • Email Verification                       │    │
│  │  • Password Reset                           │    │
│  └──────────────────────────────────────────────┘    │
│                                                       │
└────────────────────────────────────────────────────────┘

After successful authentication:
        │
        └──► UserRepository.saveUserProfile(profile)
             ↓
        Firebase Firestore (users collection)
```

---

## 4. User Profile & Setup Component

```
┌─────────────────────────────────────────────────────┐
│    User Profile & Initial Setup Management          │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │         SetupScreen UI / ProfileScreen UI    │  │
│  │  ┌──────────────────┬────────────────────┐   │  │
│  │  │  Add Contacts    │  Set Home Address  │   │  │
│  │  │  Name Fields     │  Map Selection     │   │  │
│  │  │  Phone Fields    │  Save Button       │   │  │
│  │  └──────────────────┴────────────────────┘   │  │
│  └──────────────┬────────────────────────────────┘  │
│                 │ triggers                          │
│  ┌──────────────▼────────────────────────────────┐  │
│  │         SetupViewModel                        │  │
│  │  • checkIfSetupNeeded()                       │  │
│  │  • completeSetup(contacts, homeAddress)       │  │
│  │  • state: StateFlow<SetupState>               │  │
│  └──────────────┬────────────────────────────────┘  │
│                 │ coordinates                       │
│     ┌───────────┼───────────┐                       │
│     │           │           │                       │
│  ┌──▼──┐   ┌────▼────┐  ┌──▼─────┐                 │
│  │User │   │Contacts │  │Home    │                 │
│  │Repo │   │Repo     │  │Address │                 │
│  │     │   │         │  │Prefs   │                 │
│  └──┬──┘   └────┬────┘  └──┬─────┘                 │
│     │           │           │                       │
│     │    ┌──────▼──────┐    │                       │
│     │    │  Firebase   │    │                       │
│     │    │  Firestore  │    │                       │
│     │    │ (users,     │    │                       │
│     │    │  contacts)  │    │                       │
│     │    └─────────────┘    │                       │
│     │                       │                       │
│     └──────┬─────────────────┘                       │
│            │                                        │
│     ┌──────▼──────────┐                             │
│     │  SharedPreferences                            │
│     │ (Home Address   │                             │
│     │  LatLng)        │                             │
│     └─────────────────┘                             │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 5. Trusted Contacts Management Component

```
┌─────────────────────────────────────────────────────┐
│       Trusted Contacts Management                   │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │       TrustedContactsScreen UI               │  │
│  │  ┌──────────────────┬────────────────────┐   │  │
│  │  │  Contact List    │  Search Box        │   │  │
│  │  │  Name Display    │  Filter Contacts   │   │  │
│  │  │  Phone Number    │  Add Button        │   │  │
│  │  │  Delete Button   │  Edit Button       │   │  │
│  │  └──────────────────┴────────────────────┘   │  │
│  └──────────────┬────────────────────────────────┘  │
│                 │ emits                             │
│  ┌──────────────▼────────────────────────────────┐  │
│  │    TrustedContactsViewModel                   │  │
│  │  • loadContacts()                             │  │
│  │  • addContact(name, phoneNumber)              │  │
│  │  • deleteContact(contactId)                   │  │
│  │  • updateSearchQuery(query)                   │  │
│  │  • getFilteredContacts(): List<Contact>       │  │
│  │  • uiState: StateFlow<TrustedContactsUiState> │  │
│  └──────────────┬────────────────────────────────┘  │
│                 │ observes                          │
│  ┌──────────────▼────────────────────────────────┐  │
│  │      ContactsRepository                       │  │
│  │  • getContactsFlow(): Flow<List<>>            │  │
│  │  • addContact(name, phoneNumber)              │  │
│  │  • deleteContact(contactId)                   │  │
│  │  • addMultipleContacts(list)                  │  │
│  └──────────────┬────────────────────────────────┘  │
│                 │ reads/writes                      │
│  ┌──────────────▼────────────────────────────────┐  │
│  │    Firebase Firestore                         │  │
│  │  └─ contacts collection                       │  │
│  │     ├─ id: String                             │  │
│  │     ├─ name: String                           │  │
│  │     ├─ phoneNumber: String                    │  │
│  │     └─ userId: String (filter key)            │  │
│  │                                               │  │
│  └─────────────────────────────────────────────┘  │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 6. Navigation & Maps Component

```
┌──────────────────────────────────────────────────────┐
│     Navigation & Maps Integration                   │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │         MapHomeScreen UI                      │  │
│  │  ┌─────────────┬───────────┬─────────────┐    │  │
│  │  │ Google Map  │  Route    │  Controls   │    │  │
│  │  │ Display     │  Display  │  (Start,    │    │  │
│  │  │ Polyline    │  Duration │   Cancel)   │    │  │
│  │  │ Markers     │  Distance │  Search Box │    │  │
│  │  └─────────────┴───────────┴─────────────┘    │  │
│  └───────────────┬────────────────────────────────┘  │
│                  │ user input                        │
│  ┌───────────────▼────────────────────────────────┐  │
│  │         MapViewModel                          │  │
│  │  • startNavigation(currentLoc, dest?)         │  │
│  │  • cancelNavigation()                         │  │
│  │  • searchAndNavigate(query, currentLoc)       │  │
│  │  • loadHomeLocation()                         │  │
│  │  • Observables:                               │  │
│  │    - routePoints: List<LatLng>                │  │
│  │    - displayDistance: String                  │  │
│  │    - displayDuration: String                  │  │
│  │    - isNavigationMode: Boolean                │  │
│  └───────────────┬────────────────────────────────┘  │
│                  │ queries                           │
│  ┌───────────────┼──────────────┬──────────────────┐ │
│  │               │              │                  │ │
│  ▼               ▼              ▼                  ▼ │
│ ┌──────┐   ┌──────────┐   ┌──────────┐   ┌────────┐ │
│ │Geocoder│  │Directions│   │Home Addr │   │Retrofit│ │
│ │        │  │Repository │   │Prefs    │   │Client  │ │
│ └──────┘   └──────────┘   └──────────┘   └────────┘ │
│               │                                      │
│    ┌──────────▼──────────┐                           │
│    │  Google APIs        │                           │
│    │  ┌────────────────┐ │                           │
│    │  │ Directions API │ │                           │
│    │  │ Geocoding API  │ │                           │
│    │  └────────────────┘ │                           │
│    └─────────────────────┘                           │
│                                                      │
│    SharedPreferences:                                │
│    • Saved home LatLng                               │
│                                                      │
└──────────────────────────────────────────────────────┘
```

---

## 7. Emergency Alert Component

```
┌──────────────────────────────────────────────────────┐
│          Emergency Alert System                      │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │         EmergencyScreen UI                    │  │
│  │  ┌─────────────────────────────────────────┐  │  │
│  │  │  Big SOS/Alert Button                   │  │  │
│  │  │  "Send Alert" / "Cancel"                │  │  │
│  │  │  Location Display                       │  │  │
│  │  │  Trusted Contacts List                  │  │  │
│  │  │  Message to Send                        │  │  │
│  │  └─────────────────────────────────────────┘  │  │
│  └───────────────┬────────────────────────────────┘  │
│                  │ triggers                          │
│  ┌───────────────▼────────────────────────────────┐  │
│  │      EmergencyViewModel                       │  │
│  │  • sendEmergencyAlert()                       │  │
│  │  • getCurrentLocation()                       │  │
│  │  • getTrustedContacts()                       │  │
│  │  • state: StateFlow<EmergencyState>           │  │
│  └───────────────┬────────────────────────────────┘  │
│                  │ calls                             │
│  ┌───────────────┴────────────────┬────────────────┐ │
│  │                                │                │ │
│  ▼                                ▼                ▼ │
│ ┌──────────────┐   ┌──────────────┐   ┌──────────┐  │
│ │User Repo     │   │Contacts Repo │   │Storage   │  │
│ │(save alert   │   │(get contacts)│   │Repo      │  │
│ │ history)     │   │              │   │(upload   │  │
│ │              │   │              │   │location) │  │
│ └──────────────┘   └──────────────┘   └──────────┘  │
│        │                  │                   │      │
│        └──────────┬───────┴───────────────────┘      │
│                   │                                  │
│         ┌─────────▼─────────┐                        │
│         │ Firebase Firestore│                        │
│         │ • alerts coll.    │                        │
│         │ • Send SMS/Email  │                        │
│         │   to contacts     │                        │
│         └───────────────────┘                        │
│                                                      │
│  Optional: Integration with SMS Services            │
│  • Twilio / Firebase Functions                      │
│                                                      │
└──────────────────────────────────────────────────────┘
```

---

## 8. External Services Integration

```
┌────────────────────────────────────────────────────────┐
│     External Services & Third-Party APIs              │
├────────────────────────────────────────────────────────┤
│                                                        │
│  ┌─────────────────────────────────────────────────┐  │
│  │          DrunkSafe Application                  │  │
│  │  ┌───────────────────────────────────────────┐  │  │
│  │  │       Repository Layer                    │  │  │
│  │  │  ┌─────────────────────────────────────┐  │  │  │
│  │  │  │ AuthRepository    UserRepository    │  │  │  │
│  │  │  │ ContactsRepository DirectionsRepo   │  │  │  │
│  │  │  │ StorageRepository  HomeAddressPrefs │  │  │  │
│  │  │  └──┬──────────────┬──────────────┬───┘  │  │  │
│  │  │     │              │              │       │  │  │
│  │  │     │ (HTTPS Calls)│              │       │  │  │
│  │  │  ┌──▼────────────┐ │              │       │  │  │
│  │  │  │ExternalApiClient│              │       │  │  │
│  │  │  └──┬────────────┘ │              │       │  │  │
│  │  │     │              │              │       │  │  │
│  │  │  ┌──▼──────────────▼──────────────▼──────┐ │  │
│  │  │  │    Retrofit HTTP Client                │ │  │
│  │  │  │  + OkHttp Interceptors                 │ │  │
│  │  │  └──┬───┬─────────────┬────────┬─────────┘ │  │
│  │  │     │   │             │        │           │  │
│  │  └─────┼───┼─────────────┼────────┼───────────┘  │
│  └────────┼───┼─────────────┼────────┼──────────────┘
│           │   │             │        │
│  ┌────────▼─┐ │             │        │
│  │ Firebase  │ │             │        │
│  │ ┌────────┐ │             │        │
│  │ │Auth    │ │             │        │
│  │ │Firestore│ │             │        │
│  │ │Storage │ │             │        │
│  │ │Analytics│             │        │
│  │ └────────┘ │             │        │
│  └──────────┘ │             │        │
│              │             │        │
│     ┌────────▼─────┐       │        │
│     │ Google Maps  │       │        │
│     │ Geocoding    │       │        │
│     │ Directions   │       │        │
│     └──────────────┘       │        │
│                            │        │
│              ┌─────────────▼────┐   │
│              │ Google Directions│   │
│              │ API              │   │
│              │ (Route data)     │   │
│              └──────────────────┘   │
│                                     │
│                    ┌────────────────▼──┐
│                    │ SharedPreferences │
│                    │ (Local Storage)   │
│                    └───────────────────┘
│                                        │
│  Optional Services:                    │
│  • SMS Provider (SMS alerts)           │
│  • Push Notifications                  │
│  • Analytics                           │
│                                        │
└────────────────────────────────────────┘
```

---

## 9. Data Persistence Architecture

```
┌──────────────────────────────────────────────────────┐
│         Data Persistence Strategy                    │
├──────────────────────────────────────────────────────┤
│                                                      │
│  Application Memory (Runtime)                        │
│  ┌──────────────────────────────────────────────┐   │
│  │  ViewModels & StateFlow                      │   │
│  │  (Temporary data during session)             │   │
│  │  • User session state                        │   │
│  │  • Navigation state                          │   │
│  │  • UI states                                 │   │
│  └──────────────────────────────────────────────┘   │
│                                                      │
│  Local Device Storage (SharedPreferences)            │
│  ┌──────────────────────────────────────────────┐   │
│  │  HomeAddressPreferences                      │   │
│  │  ├─ savedHomeLatLng: LatLng                   │   │
│  │  ├─ homeAddress: String                      │   │
│  │  └─ other user preferences                   │   │
│  └──────────────────────────────────────────────┘   │
│                    ▲                                 │
│                    │ (read on app startup)          │
│                    │ (write on preference change)   │
│                                                      │
│  Firebase Cloud Storage (Online)                     │
│  ┌──────────────────────────────────────────────┐   │
│  │  Firestore Database (Primary Data Source)    │   │
│  │  ├─ users collection                         │   │
│  │  │  ├─ uid (user ID)                         │   │
│  │  │  ├─ email: String                         │   │
│  │  │  ├─ displayName: String                   │   │
│  │  │  ├─ homeAddress: String                   │   │
│  │  │  └─ setupCompleted: Boolean               │   │
│  │  │                                            │   │
│  │  ├─ contacts collection                      │   │
│  │  │  ├─ id: String (auto-generated)           │   │
│  │  │  ├─ userId: String (query filter)         │   │
│  │  │  ├─ name: String                          │   │
│  │  │  ├─ phoneNumber: String                   │   │
│  │  │  └─ timestamp: Timestamp                  │   │
│  │  │                                            │   │
│  │  └─ alerts collection (optional)             │   │
│  │     ├─ userId: String                        │   │
│  │     ├─ timestamp: Timestamp                  │   │
│  │     ├─ location: GeoPoint                    │   │
│  │     └─ status: String                        │   │
│  │                                              │   │
│  │  Firebase Authentication                     │   │
│  │  ├─ Email/Password User Database             │   │
│  │  ├─ Session Tokens                           │   │
│  │  └─ Auth State                               │   │
│  │                                              │   │
│  │  Firebase Storage (Files)                    │   │
│  │  ├─ User Profile Pictures (optional)         │   │
│  │  └─ Emergency Alert Files                    │   │
│  │                                              │   │
│  └──────────────────────────────────────────────┘   │
│                                                      │
│  Data Sync Flow:                                     │
│  1. User creates/updates data → Repository          │
│  2. Repository → Firebase (async)                   │
│  3. Real-time listeners update local state          │
│  4. StateFlow notifies UI                           │
│                                                      │
└──────────────────────────────────────────────────────┘
```

---

## 10. Complete Component Interaction Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│                    DrunkSafe Complete Flow                         │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │                    PRESENTATION LAYER                        │ │
│  │  ┌──────────┬──────────┬──────────┬──────────┬──────────┐   │ │
│  │  │  Login   │  Setup   │  Profile │  Contacts│  Map     │   │ │
│  │  │  Screen  │  Screen  │  Screen  │  Screen  │  Screen  │   │ │
│  │  │ & SignUp │ & Home   │ & Home   │ (Search) │ & Navig  │   │ │
│  │  │          │ Address  │ Address  │ & List   │ & Emerg  │   │ │
│  │  └─────┬────┴────┬─────┴────┬─────┴────┬─────┴────┬─────┘   │ │
│  └────────┼─────────┼──────────┼──────────┼──────────┼─────────┘ │
│           │         │          │          │          │            │
│  ┌────────▼─────────▼──────────▼──────────▼──────────▼─────────┐ │
│  │              VIEWMODEL LAYER (State Management)             │ │
│  │  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐         │ │
│  │  │Login │  │Setup │  │Profile│ │Trusted│ │Map   │         │ │
│  │  │VM    │  │VM    │  │VM    │  │ContactsVM  │VM   │         │ │
│  │  │      │  │      │  │      │  │         │      │         │ │
│  │  └──┬───┘  └──┬───┘  └──┬───┘  └──┬─────┘  └──┬──┘         │ │
│  └─────┼─────────┼─────────┼─────────┼──────────┼────────────┘ │
│        │         │         │         │          │               │
│  ┌─────▼─────────▼─────────▼─────────▼──────────▼────────────┐ │
│  │            REPOSITORY LAYER (Data Access)                │ │
│  │  ┌────────────┬────────────┬───────────┬─────────────┐   │ │
│  │  │ AuthRepo   │ UserRepo   │ContactsRepo         │   │ │
│  │  │ • signIn() │ • saveProfile() │ • getContacts()   │   │ │
│  │  │ • signUp() │ • getProfile()  │ • addContact()    │   │ │
│  │  │ • signOut()│ • isSetupDone() │ • deleteContact() │   │ │
│  │  └────────────┴────────────┴───────────┴─────────────┘   │ │
│  │                                                            │ │
│  │  ┌────────────┬──────────────────────────────────────┐   │ │
│  │  │DirectionsRepo    StorageRepo    HomeAddrPrefs    │   │ │
│  │  │• getRoute()     • uploadFile()  • saveHome()     │   │ │
│  │  │                 • downloadFile()• getHome()      │   │ │
│  │  └────────────┴──────────────────────────────────────┘   │ │
│  └──────────────────────────────────────────────────────────┘ │
│          │                        │                  │         │
│  ┌───────▼──────────┐    ┌────────▼────────┐  ┌────▼──────┐  │
│  │  Firebase Auth   │    │  Firestore DB   │  │SharedPref │  │
│  │  • Email/Pass    │    │  • users        │  │ • Local   │  │
│  │  • Auth State    │    │  • contacts     │  │   storage │  │
│  │  • Session Mgmt  │    │  • alerts       │  │           │  │
│  └──────────────────┘    └─────────────────┘  └───────────┘  │
│          │                        │                  │         │
│          └────────────┬───────────┴──────────────────┘         │
│                       │                                        │
│         ┌─────────────▼──────────────┐                         │
│         │   External API Services    │                         │
│         │  ┌──────────────────────┐  │                         │
│         │  │ Google Maps APIs     │  │                         │
│         │  │ • Geocoding          │  │                         │
│         │  │ • Directions         │  │                         │
│         │  │ • Maps Display       │  │                         │
│         │  └──────────────────────┘  │                         │
│         │                            │                         │
│         │  ┌──────────────────────┐  │                         │
│         │  │ Retrofit HTTP Client │  │                         │
│         │  │ • API requests       │  │                         │
│         │  │ • Response parsing   │  │                         │
│         │  └──────────────────────┘  │                         │
│         └────────────────────────────┘                         │
│                                                                │
└────────────────────────────────────────────────────────────────┘

Legend:
  → Data Flow
  ← State Updates
```

---

## 11. Sequence Diagrams

### A. Login Flow Sequence

```
User          LoginUI         LoginVM         AuthRepo      Firebase
 │              │               │               │               │
 ├─ Enter ─────→│               │               │               │
 │  Credentials │               │               │               │
 │              │               │               │               │
 │              ├─ signIn() ───→│               │               │
 │              │               │               │               │
 │              │               ├─ signIn() ───→│               │
 │              │               │               │               │
 │              │               │               ├─ Authenticate─→│
 │              │               │               │               │
 │              │               │               │←─ Return User─┤
 │              │               │←─ FirebaseUser│               │
 │              │               │               │               │
 │              │   Success     │               │               │
 │              │←─ (userId) ───│               │               │
 │              │               │               │               │
 │              ├─ saveUserProfile()────┐       │               │
 │              │                       │       │               │
 │              │                ┌──────▼───────▼──────┐       │
 │              │                │ UserRepository      │       │
 │              │                │ saves to Firestore  │       │
 │              │                └─────────────────────┘       │
 │              │               │               │               │
 │←─ Show ──────│               │               │               │
   Home Screen
```

### B. Send Emergency Alert Sequence

```
User           EmergencyUI     EmergencyVM    ContactsRepo    Firebase
 │                 │               │               │               │
 ├─ Tap ────────→  │               │               │               │
 │  SOS Button     │               │               │               │
 │                 │               │               │               │
 │                 ├─ Send Alert ─→│               │               │
 │                 │               │               │               │
 │                 │               ├─ getTrusted ─→│               │
 │                 │               │               │               │
 │                 │               │←─ List ───────│               │
 │                 │               │   Contacts    │               │
 │                 │               │               │               │
 │                 │               ├─────────────────→ Save Alert │
 │                 │               │               │  in Firestore│
 │                 │               │               │               │
 │                 │               │               │←─ Confirmed  │
 │                 │               │               │               │
 │                 │←─ Alert Sent  │               │               │
 │                 │               │               │               │
 │←─ Show Confirm  │               │               │               │
   Message
   
Optional:
  • Send SMS to contacts (Firebase Cloud Functions)
  • Send push notifications
  • Upload location to Firestore
```

### C. Navigate Home Sequence

```
User          MapUI          MapVM           DirectionsRepo    Google APIs
 │             │               │                  │                │
 ├─ Tap ──────→│               │                  │                │
 │  Navigate   │               │                  │                │
 │  Home       │               │                  │                │
 │             │               │                  │                │
 │             ├─ startNav() ─→│                  │                │
 │             │               │                  │                │
 │             │               ├─ getRoute() ───→ │                │
 │             │               │                  │                │
 │             │               │                  ├─ Call Directions API
 │             │               │                  │                │
 │             │               │                  │←─ Route ──────→│
 │             │               │                  │   (polyline)   │
 │             │               │←─ routePoints ──│                │
 │             │               │    + distance   │                │
 │             │               │    + duration   │                │
 │             │               │                  │                │
 │             │               ├─ Load HomeAddr ─┤                │
 │             │               │  from Prefs     │                │
 │             │               │                  │                │
 │             │               ├─ isNavigationMode = true
 │             │               │                  │                │
 │             │←─ Show Route  │                  │                │
 │             │   on Map      │                  │                │
 │             │               │                  │                │
 │←─ Display  │               │                  │                │
   Navigation
```

---

## 12. Component Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│              Dependency Graph                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  MainActivity                                              │
│    │                                                        │
│    ├─→ AppNavHost                                           │
│    │    ├─→ LoginScreen                                    │
│    │    │   └─→ LoginViewModel                            │
│    │    │       ├─→ AuthRepository                        │
│    │    │       └─→ UserRepository                        │
│    │    │                                                  │
│    │    ├─→ SetupScreen                                    │
│    │    │   └─→ SetupViewModel                            │
│    │    │       ├─→ UserRepository                        │
│    │    │       ├─→ ContactsRepository                    │
│    │    │       └─→ FirebaseAuth                          │
│    │    │                                                  │
│    │    ├─→ ProfileScreen                                  │
│    │    │   └─→ ProfileViewModel                          │
│    │    │       ├─→ UserRepository                        │
│    │    │       └─→ HomeAddressPreferences                │
│    │    │                                                  │
│    │    ├─→ TrustedContactsScreen                          │
│    │    │   └─→ TrustedContactsViewModel                  │
│    │    │       └─→ ContactsRepository                    │
│    │    │                                                  │
│    │    ├─→ MapHomeScreen                                  │
│    │    │   └─→ MapViewModel                              │
│    │    │       ├─→ DirectionsRepository                  │
│    │    │       ├─→ HomeAddressPreferences                │
│    │    │       ├─→ Geocoder                              │
│    │    │       └─→ PolyUtil (Maps Utils)                 │
│    │    │                                                  │
│    │    └─→ EmergencyScreen                                │
│    │        └─→ EmergencyViewModel                         │
│    │            ├─→ UserRepository                        │
│    │            ├─→ ContactsRepository                    │
│    │            └─→ StorageRepository                     │
│    │                                                       │
│    └─→ Firebase Services                                   │
│        ├─→ FirebaseAuth                                    │
│        ├─→ FirebaseFirestore                              │
│        └─→ FirebaseStorage                                │
│                                                             │
│  Repositories Layer:                                        │
│    ├─→ AuthRepository                                      │
│    │   └─→ FirebaseAuth                                   │
│    ├─→ UserRepository                                      │
│    │   └─→ FirebaseFirestore                              │
│    ├─→ ContactsRepository                                  │
│    │   ├─→ FirebaseFirestore                              │
│    │   └─→ FirebaseAuth                                   │
│    ├─→ DirectionsRepository                                │
│    │   ├─→ RetrofitClient                                 │
│    │   └─→ DirectionsApiServices                          │
│    ├─→ StorageRepository                                   │
│    │   └─→ FirebaseStorage                                │
│    └─→ HomeAddressPreferences                              │
│        └─→ SharedPreferences                               │
│                                                             │
│  External Libraries:                                        │
│    ├─→ Retrofit (HTTP Client)                              │
│    ├─→ OkHttp (HTTP Interceptors)                          │
│    ├─→ Google Maps API (Geocoding, Directions)             │
│    ├─→ Compose (UI Framework)                              │
│    ├─→ Coroutines (Async Operations)                       │
│    └─→ Firebase SDK                                        │
│        ├─→ Authentication                                  │
│        ├─→ Firestore                                       │
│        └─→ Storage                                         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Summary

The DrunkSafe application follows a **clean architecture pattern** with clear separation of concerns:

1. **Presentation Layer**: Compose-based UI screens with state management
2. **ViewModel Layer**: MVVM pattern for UI logic and state management using StateFlow
3. **Repository Layer**: Data access abstraction for Firestore, SharedPreferences, and external APIs
4. **External Services**: Firebase (Auth, Firestore, Storage) and Google APIs (Maps, Directions)

**Key Integration Points:**
- Firebase Authentication for user management
- Firestore for real-time data synchronization
- Google Maps & Directions for navigation
- SharedPreferences for local caching
- Retrofit for HTTP API calls

**Data Flow:**
User interactions → UI Screen → ViewModel → Repository → Firebase/APIs → StateFlow updates → UI re-renders

---

## 13. Use Cases

Detailed, selected non-trivial use cases are available in `USE_CASES.md` (PlantUML + flows). Render the PlantUML blocks with a PlantUML extension or the PlantUML CLI to produce PNG/SVG diagrams.
