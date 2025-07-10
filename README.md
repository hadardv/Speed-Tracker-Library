# SpeedTrackerLibrary

**SpeedTrackerLibrary** is an Android library to monitor your ride in real time:

-  **GPS speed tracking** (km/h) via `FusedLocationProviderClient`
-  **Ride analytics**: max, min, avg speed; time spent in Urban/Suburban/Highway zones
-  **Aggressive driving detection** using accelerometer & gyroscope
-  **Live UI updates** with broadcasts
-  **Persistence**: save each ride summary as JSON in `SharedPreferences`
-  **Auto‑start** your tracking service when your device connects to a specific Bluetooth device (e.g. Toyota Touch)

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

---

## Installation

1. **Add permissions** to your `AndroidManifest.xml`:

   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION"/>
   <uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
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

---

Tap **Start Speed Tracking** to begin manually, or simply connect to your specified Bluetooth device to kick off automatically.

---
## Screenshots
- Here are the logs that detects the Bluetooth device and start automatically the service
<img width="1882" height="323" alt="Screenshot 2025-07-10 141047" src="https://github.com/user-attachments/assets/db323fc8-122a-4170-bf5f-dd42c9c679ce" />

https://github.com/user-attachments/assets/c6517adf-2a1d-45a9-bc4f-d252d4bd58c3




