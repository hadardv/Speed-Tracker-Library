package com.classy.speedtrackerlibrary;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView speedTextView;
    private final AnalyticsManager analytics = AnalyticsManager.getInstance();


    private final BroadcastReceiver speedReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            float speed = intent.getFloatExtra("speed", 0f);
            speedTextView.setText("Speed: " + speed + " km/h");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Register the broadcast receiver for all Android versions
        IntentFilter filter = new IntentFilter("com.classy.SPEED_UPDATE");
        registerReceiver(speedReceiver, new IntentFilter("com.classy.SPEED_UPDATE"), Context.RECEIVER_NOT_EXPORTED);

        speedTextView = findViewById(R.id.speedTextView);

        Button startButton = findViewById(R.id.startServiceButton);
        Button stopButton = findViewById(R.id.stopServiceButton);

        startButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                startService(new Intent(this, LocationService.class));
            }
        });

        stopButton.setOnClickListener(v -> {
            stopService(new Intent(this, LocationService.class));
            // Save the ride summary in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("rides", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String json = analytics.getSummaryAsJson();  // Make sure you have this method in AnalyticsManager
            String key = "ride_" + System.currentTimeMillis();
            editor.putString(key, json);
            editor.apply();
            Intent i = new Intent(this, ResultsActivity.class);
            i.putExtra("max", analytics.getMaxSpeed());
            i.putExtra("min", analytics.getMinSpeed());
            i.putExtra("avg", analytics.getAverageSpeed());
            i.putExtra("urban", analytics.getUrbanCount());
            i.putExtra("suburban", analytics.getSuburbanCount());
            i.putExtra("highway", analytics.getHighwayCount());
            startActivity(i);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, LocationService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(speedReceiver);
    }

}