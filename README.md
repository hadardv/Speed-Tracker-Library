# SpeedTrackerLibrary

**SpeedTrackerLibrary** is an Android library to monitor your ride in real time:

-  **GPS speed tracking** (km/h) via `FusedLocationProviderClient`
-  **Ride analytics**: max, min, avg speed; time spent in Urban/Suburban/Highway zones
-  **Aggressive driving detection** using accelerometer & gyroscope
-  **Live UI updates** with broadcasts
-  **Persistence**: save each ride summary as JSON in `SharedPreferences`
-  **Auto‑start** your tracking service when your device connects to a specific Bluetooth device (e.g. Toyota Touch)
- **Google Maps Integration** show your entire route from start to finish on a SupportMapFragment
---

## Features

1. **Real‑time speed updates**
2. **Categorization**:
   - Urban: < 50 km/h
   - Suburban: 50–90 km/h
   - Highway: > 90 km/h
3. **Aggressive Events**:
   - Sharp acceleration/braking
   - Sharp turns
4. **Bluetooth Auto‑Start**:
   - Define a target device name (`"Toyota Touch"` by default)
   - When connected, `LocationService` fires up automatically (requires proper permissions)
5. **Broadcast API**:
   - `com.classy.SPEED_UPDATE` broadcasts speed values
   - UI listens and renders live speed
6. **Persistence**:
   - Each ride summary saved under `"ride_<timestamp>"` in `SharedPreferences`
7. **Google Maps Integration**
   - show your entire route from start to finish on a SupportMapFragment

---

## Installation

1. **Add permissions** to your `AndroidManifest.xml`:

   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
   <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
   <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
   ```

2. **Register components** in the `<application>` block:

   ```xml
   <receiver
     android:name=".BluetoothReceiver"
     android:exported="true">
     <intent-filter>
       <action android:name="android.bluetooth.device.action.ACL_CONNECTED"/>
     </intent-filter>
   </receiver>

   <service
     android:name=".LocationService"
     android:foregroundServiceType="location"
     android:exported="false" />

   <activity android:name=".MainActivity" android:exported="true">
     <intent-filter>
       <action android:name="android.intent.action.MAIN" />
       <category android:name="android.intent.category.LAUNCHER" />
     </intent-filter>
   </activity>
   ```

3. **Implement or integrate**:

   - **`BluetoothReceiver`**: watches for `ACTION_ACL_CONNECTED`, verifies the target device, checks location & FGS permissions, then starts `LocationService` in the foreground.

4. **Runtime permissions** (in your `MainActivity`):

   - `ACCESS_FINE_LOCATION`
   - `FOREGROUND_SERVICE_LOCATION` (API 34+)
   - `BLUETOOTH_CONNECT` (API 31+)

5. **Add Dependencies**
```xml
   dependencies {
  implementation "androidx.appcompat:appcompat:1.7.0"
  implementation "com.google.android.material:material:1.9.0"
  implementation "com.google.android.gms:play-services-location:21.3.0"
  implementation "com.google.android.gms:play-services-maps:19.2.0"
  implementation "com.google.code.gson:gson:2.10.1"
}
```

6. **Provide your google maps API in gradle.properties**
   ```xml
   MAPS_API_KEY=YOUR_API_KEY_HERE
   ```

---

Tap the play button to begin manually and tap ANALYTICS to pause, or simply connect to your specified Bluetooth device to kick off automatically.

---
## Screenshots
<img width="480" height="1064" alt="image" src="https://github.com/user-attachments/assets/9bd17ba6-72fe-450c-9e6c-fd577be5a02c" />

<img width="478" height="1075" alt="image" src="https://github.com/user-attachments/assets/891da424-92c8-4220-89d1-198038e9da0f" />


https://github.com/user-attachments/assets/b0e53f34-9c73-473e-99aa-7b13c0d72a39








