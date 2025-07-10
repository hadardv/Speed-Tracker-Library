package com.classy.speedtrackerlibrary;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class BluetoothReceiver extends BroadcastReceiver {
    private static final String TAG    = "BluetoothReceiver";
    private static final String TARGET = "Toyota Touch";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())) {
            return;
        }

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Missing BLUETOOTH_CONNECT permission");
            return;
        }

        String name = device.getName();
        if (!TARGET.equals(name)) {
            return;
        }

        Log.d(TAG, "Matched “" + TARGET + "” – attempting to auto-start LocationService");

        boolean hasFine = ActivityCompat.checkSelfPermission(
                ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        boolean hasFgs  = Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                || ActivityCompat.checkSelfPermission(
                ctx, Manifest.permission.FOREGROUND_SERVICE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (!hasFine || !hasFgs) {
            Log.w(TAG, "Missing FINE_LOCATION or FGS_LOCATION – cannot auto-start");
            return;
        }

        Intent svc = new Intent(ctx, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(svc);
        } else {
            ctx.startService(svc);
        }
    }
}
