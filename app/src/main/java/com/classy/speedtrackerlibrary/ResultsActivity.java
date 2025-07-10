package com.classy.speedtrackerlibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ResultsActivity extends AppCompatActivity {
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


    }
}
