package com.classy.speedtrackerlibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private List<LatLng> routePoints;


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);



        TextView maxSpeedText = findViewById(R.id.maxSpeedText);
        TextView minSpeedText = findViewById(R.id.minSpeedText);
        TextView avgSpeedText = findViewById(R.id.avgSpeedText);
        TextView urbanTimeText = findViewById(R.id.urbanTimeText);
        TextView suburbanTimeText = findViewById(R.id.suburbanTimeText);
        TextView highwayTimeText = findViewById(R.id.highwayTimeText);
        TextView anomalyEventsText = findViewById(R.id.anomalyEvents);

        Intent intent = getIntent();

        float max = getIntent().getFloatExtra("max", 0f);
        float min = getIntent().getFloatExtra("min", 0f);
        float avg = getIntent().getFloatExtra("avg", 0f);
        long urbanMin = getIntent().getIntExtra("urban", 0) / 60;
        long suburbanMin = getIntent().getIntExtra("suburban", 0) / 60;
        long highwayMin = getIntent().getIntExtra("highway", 0) / 60;
        int aggressiveEvents = getIntent().getIntExtra("aggressive", 0);

        maxSpeedText.setText(String.format("Max Speed: %.1f km/h", max));
        minSpeedText.setText(String.format("Min Speed: %.1f km/h", min));
        avgSpeedText.setText(String.format("Avg Speed: %.1f km/h", avg));


        urbanTimeText.setText("Urban Time: " +urbanMin+ " min");
        suburbanTimeText.setText("Suburban Time: " + suburbanMin + " min");
        highwayTimeText.setText("Highway Time: " + highwayMin + " min");

        anomalyEventsText.setText("Aggressive Driving Events: " + aggressiveEvents);

        SharedPreferences prefs = getSharedPreferences("rides", MODE_PRIVATE);
        String json = prefs.getString("last_route", "");
        if (!json.isEmpty()) {
            Type listType = new TypeToken<List<LatLng>>(){}.getType();
            routePoints = new Gson().fromJson(json, listType);
        }
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);




    }
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        if (routePoints != null && !routePoints.isEmpty()) {
            map.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .width(6f));
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(routePoints.get(0), 14f));
        }
    }

}
