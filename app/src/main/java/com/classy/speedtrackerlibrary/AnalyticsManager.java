package com.classy.speedtrackerlibrary;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AnalyticsManager {
    private final List<Float> speeds = new ArrayList<>();
    private int urbanCount = 0;
    private int suburbanCount = 0;
    private int highwayCount = 0;

    public void addSpeed(float speed){
        speeds.add(speed);
    }

    public void categorizeSpeed(float speed){
        if(speed < 30){
            urbanCount++;
        }else if(speed < 70) {
            suburbanCount++;
        }else {
            highwayCount++;
        }

    }

    public float getMaxSpeed(){
        return speeds.isEmpty() ? 0 : Collections.max(speeds);
    }

    public float getMinSpeed(){
        return speeds.isEmpty() ? 0 : Collections.min(speeds);
    }

    public float getAverageSpeed() {
        if(speeds.isEmpty()) return 0;
        float sum = 0;
        for (float s : speeds){
            sum += s;
        }
        return sum/speeds.size();
    }

    public void reset(){
        speeds.clear();
        urbanCount = 0;
        suburbanCount = 0;
        highwayCount = 0;
    }

    public int getTotalSamples(){
        return speeds.size();
    }

    public int getUrbanCount(){
        return urbanCount;
    }

    public int getSuburbanCount(){
        return suburbanCount;
    }

    public int getHighwayCount(){
        return highwayCount;
    }
}
