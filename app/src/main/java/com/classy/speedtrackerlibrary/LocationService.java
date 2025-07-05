package com.classy.speedtrackerlibrary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.os.Looper;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;


public class LocationService extends Service {
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private final AnalyticsManager analytics = AnalyticsManager.getInstance();



    @Override
    public void onCreate() {
        Log.d("LocationService", "PID: " + android.os.Process.myPid());
        super.onCreate();
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                for (Location location : result.getLocations()) {
                    // this two lines are for real speed calculation, i'm on testing mode so i randomize the speed.
//                    float speedMps = location.getSpeed();
//                    float speedKmh = speedMps * 3.6f;
                    float speedKmh = new java.util.Random().nextInt(150); // simulate 0–120 km/h

                    Log.d("SpeedTracker", "Speed: " + speedKmh + " km/h");
                    analytics.addSpeed(speedKmh);
                    analytics.categorizeSpeed(speedKmh);
                    Log.d("SpeedTracker", "Max: " + analytics.getMaxSpeed() +
                            ", Min: " + analytics.getMinSpeed() +
                            ", Avg: " + analytics.getAverageSpeed() +
                            ", Urban: " + analytics.getUrbanCount() +
                            ", Suburban: " + analytics.getSuburbanCount() +
                            ", Highway: " + analytics.getHighwayCount());

                    Intent intent = new Intent("com.classy.SPEED_UPDATE");
                    intent.setPackage(getPackageName());
                    intent.putExtra("speed", speedKmh);
                    sendBroadcast(intent);
                    Log.d("SpeedTracker", "Broadcast sent: ");

                    sendBroadcast(intent);
                }
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1,createNotification());
        requestLocationUpdates();
        Log.d("SpeedTracker", "onStartCommand: Service started");
        return START_STICKY;

    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(5000)
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Permissions not granted, silently fail or log
        }

        locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }


    private Notification createNotification() {
        String channelId = "speed_tracker_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Speed Tracker", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Speed Tracking Active")
                .setContentText("Tracking your speed via GPS")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationClient.removeLocationUpdates(locationCallback);
        analytics.reset();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
