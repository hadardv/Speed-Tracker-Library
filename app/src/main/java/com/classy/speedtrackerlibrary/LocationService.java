package com.classy.speedtrackerlibrary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class LocationService extends Service {
    private FusedLocationProviderClient locationClient;
    private final List<LatLng> routePoints = new ArrayList<>();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private final float GYRO_THRESHOLD = 2.5f; // sharp turn
    private final float ACC_THRESHOLD = 15.0f; // sharp gaz/braking

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long now = System.currentTimeMillis();
            if (now - lastAggressiveSensorEventTime < 5000) return;

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;
                if (TEST_MODE) {
                    acceleration = 20.0;
                }

                if (acceleration > ACC_THRESHOLD) {
                    Log.d("AggressiveDriving", "Fake aggressive acceleration: " + acceleration);
                    aggressiveEventCount++;
                    lastAggressiveSensorEventTime = now;
                    analytics.setAggressiveEvents(aggressiveEventCount);
                }
            }

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                float angularSpeed = Math.abs(event.values[2]); // Z-axis turn
                if (TEST_MODE) {
                    angularSpeed = 3.0f;
                }

                if (angularSpeed > GYRO_THRESHOLD) {
                    Log.d("AggressiveDriving", "Fake aggressive turn: " + angularSpeed);
                    aggressiveEventCount++;
                    lastAggressiveSensorEventTime = now;
                    analytics.setAggressiveEvents(aggressiveEventCount);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // ignore
        }
    };


    private LocationCallback locationCallback;
    private final AnalyticsManager analytics = AnalyticsManager.getInstance();
    private float lastSpeed = -1f;
    private long lastSpeedTimestamp = 0;
    private int aggressiveEventCount = 0;
    private long lastAggressiveSensorEventTime = 0;

    private final boolean TEST_MODE = true;
    private Location lastLocation = null;






    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                for (Location location : result.getLocations()) {
                    float speedKmh = 0f;

                    if (lastLocation != null) {
                        float distance = lastLocation.distanceTo(location);
                        long deltaTime = location.getTime() - lastLocation.getTime();

                        if (deltaTime > 0) {
                            float speedMps = (distance / deltaTime) * 1000f;
                            speedKmh = speedMps * 3.6f;
                        }
                    }


                    long currentTime = location.getTime();
                    if (lastSpeed >= 0) {
                        float deltaSpeed = Math.abs(speedKmh - lastSpeed);
                        long deltaTime = currentTime - lastSpeedTimestamp;

                        if (deltaTime < 2000 && deltaSpeed > 15) {
                            aggressiveEventCount++;
                            Log.d("AggressiveDriving", "Aggressive event detected! Total: " + aggressiveEventCount);
                        }
                    }

                    lastSpeed = speedKmh;
                    lastSpeedTimestamp = currentTime;
                    lastLocation = location;
                    Log.d("SpeedTracker", "Speed: " + speedKmh + " km/h");

                    analytics.addSpeed(speedKmh);
                    analytics.categorizeSpeed(speedKmh);
                    analytics.setAggressiveEvents(aggressiveEventCount);

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
                    routePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));

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
            return;
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
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
        String json = new Gson().toJson(routePoints);
        SharedPreferences prefs = getSharedPreferences("rides", MODE_PRIVATE);
        prefs.edit()
                .putString("last_route", json)
                .apply();

        analytics.reset();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
