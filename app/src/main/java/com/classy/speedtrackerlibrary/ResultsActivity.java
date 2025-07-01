package com.classy.speedtrackerlibrary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView resultsText = findViewById(R.id.resultsText);

        float max = getIntent().getFloatExtra("max", 0f);
        float min = getIntent().getFloatExtra("min", 0f);
        float avg = getIntent().getFloatExtra("avg", 0f);
        long urbanSec = getIntent().getIntExtra("urban", 0);
        long suburbanSec = getIntent().getIntExtra("suburban", 0);
        long highwaySec = getIntent().getIntExtra("highway", 0);

        resultsText.setText(
                "Max Speed: " + max + " km/h\n" +
                        "Min Speed: " + min + " km/h\n" +
                        "Avg Speed: " + avg + " km/h\n" +
                        "Urban Time: " + (urbanSec / 60) + " min\n" +
                        "Suburban Time: " + (suburbanSec / 60) + " min\n" +
                        "Highway Time: " + (highwaySec / 60) + " min"
        );
    }
}
