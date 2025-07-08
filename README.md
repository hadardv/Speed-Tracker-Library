# SpeedTrackerLibrary

**SpeedTrackerLibrary** is an Android app that tracks real-time driving speed using GPS and motion sensors. It categorizes segments by road type (urban, suburban, highway) and provides ride analytics including max, min, and average speed, as well as aggressive driving events.

---

## Features

- Real-time speed tracking using GPS
- Accelerometer and gyroscope-based aggressive driving detection
- Categorization of speed by road type:
  - Urban: < 50 km/h
  - Suburban: 50–90 km/h
  - Highway: > 90 km/h
- Analytics screen with:
  - Max, Min, and Avg speed
  - Time spent in Urban, Suburban, and Highway zones
  - Count of aggressive driving events
- GPX file support with timestamps
- Live broadcast updates for UI integration
- Results saved using SharedPreferences
- `AnalyticsManager` handles ride analytics and history

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

## Usage

- Tap **Start Speed Tracking** to begin tracking your ride.
- Tap **Stop Speed Tracking** to end tracking and view analytics.
- The app:
  - Starts a foreground service
  - Uses `FusedLocationProviderClient` for accurate location updates
  - Registers accelerometer and gyroscope sensors
  - Detects aggressive acceleration and sharp turns
  - Broadcasts real-time speed updates
  - Calculates and saves ride statistics for display and storage

---

## Screenshots

---

## Permissions

Make sure to request runtime permissions in `MainActivity` for:

```java
Manifest.permission.ACCESS_FINE_LOCATION
```

---

## Requirements

- Android Studio
- Minimum SDK 26 (Android 8.0)
- Target SDK 34 (Android 14)

---
