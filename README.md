# SpeedTrackerLibrary

**SpeedTrackerLibrary** is an Android app that tracks real-time driving speed using GPS, categorizes segments by road type (urban, suburban, highway), and provides ride analytics such as max, min, and average speed.

---

##  Features

-  Real-time speed tracking with location service  
-  Uses Android's `FusedLocationProviderClient` for accurate updates  
-  Summary screen with:
  - Max, Min, and Avg speed
  - Time spent in Urban, Suburban, and Highway zones
-  Saves ride results to SharedPreferences
-  Broadcast receiver updates UI live
-  `AnalyticsManager` handles all statistics

---

## Installation

1. Clone the repository or copy the code into your Android Studio project.
2. Ensure the following permissions are declared in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

3. Add the required service and activity declarations in your manifest:

```xml
<activity android:name=".MainActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity android:name=".ResultsActivity" />

<service
    android:name=".LocationService"
    android:foregroundServiceType="location"
    android:exported="false" />
```

---

##  Usage

- Tap **Start Speed Tracking** to begin tracking your ride.
- Tap **Stop Speed Tracking** to end tracking and view analytics.
- The app:
  - Starts a foreground service
  - Collects and broadcasts speed values
  - Calculates and stores ride statistics
  - Displays results in a separate screen

---
 ranges as:
  - Urban: < 50 km/h
  - Suburban: 50–90 km/h
  - Highway: > 90 km/h
- Tracks:
  - Total duration in each category
  - Max / Min / Avg speed (real-time)
  - JSON export for historical storage
---

## Permissions

Make sure to handle runtime permission requests in `MainActivity` for:

```java
Manifest.permission.ACCESS_FINE_LOCATION
```

---

##  Requirements

- Android Studio 
- Minimum SDK 26 (Android 8.0)
- Target SDK 34 (Android 14)

---

