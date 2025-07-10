package com.classy.speedtrackerlibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_CODE = 42;

    private TextView speedTextView;
    private final AnalyticsManager analytics = AnalyticsManager.getInstance();

    private final BroadcastReceiver speedReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            float speedKmh = intent.getFloatExtra("speed", 0f);
            Log.d(TAG, "Speed update: " + speedKmh);
            speedTextView.setText((int) speedKmh + " km/h");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        speedTextView = findViewById(R.id.speedTextView);

        if (!hasLocationPermissions()) {
            String[] perms;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                perms = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION
                };
            } else {
                perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            }
            ActivityCompat.requestPermissions(this, perms, LOCATION_PERMISSION_CODE);
        }


        Button startButton = findViewById(R.id.startServiceButton);
        Button stopButton = findViewById(R.id.stopServiceButton);

        startButton.setOnClickListener(v -> {
            if (hasLocationPermissions()) {
                startLocationService();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_CODE);
            }
        });

        stopButton.setOnClickListener(v -> {
            stopService(new Intent(this, LocationService.class));
            SharedPreferences prefs = getSharedPreferences("rides", MODE_PRIVATE);
            String json = analytics.getSummaryAsJson();
            prefs.edit()
                    .putString("ride_" + System.currentTimeMillis(), json)
                    .apply();

            Intent i = new Intent(this, ResultsActivity.class);
            i.putExtra("max", analytics.getMaxSpeed());
            i.putExtra("min", analytics.getMinSpeed());
            i.putExtra("avg", analytics.getAverageSpeed());
            i.putExtra("urban", analytics.getUrbanCount());
            i.putExtra("suburban", analytics.getSuburbanCount());
            i.putExtra("highway", analytics.getHighwayCount());
            i.putExtra("aggressive", analytics.getAggressiveEvents());
            startActivity(i);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(speedReceiver, new IntentFilter("com.classy.SPEED_UPDATE"), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(speedReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (hasLocationPermissions()) {
                startLocationService();
            } else {
                Log.w(TAG, "Location permissions denied, cannot start service");
            }
        }
    }

    private boolean hasLocationPermissions() {
        boolean fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            boolean fgs = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.FOREGROUND_SERVICE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            return fine && fgs;
        }
        return fine;
    }

    private void startLocationService() {
        Intent svc = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(svc);
        } else {
            startService(svc);
        }
    }
}
