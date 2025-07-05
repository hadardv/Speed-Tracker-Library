package com.classy.speedtrackerlibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
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

        Intent intent = getIntent();

        float max = getIntent().getFloatExtra("max", 0f);
        float min = getIntent().getFloatExtra("min", 0f);
        float avg = getIntent().getFloatExtra("avg", 0f);
        long urbanMin = getIntent().getIntExtra("urban", 0) / 60;
        long suburbanMin = getIntent().getIntExtra("suburban", 0) / 60;
        long highwayMin = getIntent().getIntExtra("highway", 0) / 60;

        maxSpeedText.setText("Max Speed: " + max + " km/h");
        minSpeedText.setText("Min Speed: " + min + " km/h");
        avgSpeedText.setText("Avg Speed: " + avg + " km/h");

        urbanTimeText.setText("Urban Time: " +urbanMin+ " min");
        suburbanTimeText.setText("Suburban Time: " + suburbanMin + " min");
        highwayTimeText.setText("Highway Time: " + highwayMin + " min");

    }
}
