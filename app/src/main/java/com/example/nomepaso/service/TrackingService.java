package com.example.nomepaso.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.nomepaso.MainActivity;
import com.example.nomepaso.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

public class TrackingService extends Service {
    public static final String ACTION_LOCATION_UPDATE = "com.example.nomepaso.LOCATION_UPDATE";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_DISTANCE = "extra_distance";

    private static final String CHANNEL_ID = "TrackingServiceChannel";
    private static final int NOTIFICATION_ID = 123;

    private String destName;
    private double destLat;
    private double destLon;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private boolean alert1000Fired = false;
    private boolean alert300Fired = false;
    private boolean alert100Fired = false;

    @Override
    public void onCreate() {
        super.onCreate();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        processLocation(location);
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            this.destName = intent.getStringExtra("DEST_NAME");
            this.destLat = intent.getDoubleExtra("DEST_LAT", 0.0d);
            this.destLon = intent.getDoubleExtra("DEST_LON", 0.0d);
        }
        createNotificationChannel();
        Notification notification = createNotification("Seguimiento activo para " + (this.destName != null ? this.destName : ""));
        startForeground(NOTIFICATION_ID, notification);
        startLocationUpdates();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                .setMinUpdateIntervalMillis(2000L)
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            this.fusedLocationClient.requestLocationUpdates(locationRequest, this.locationCallback, Looper.getMainLooper());
        }
    }

    private void processLocation(Location location) {
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), this.destLat, this.destLon, results);
        float distance = results[0];

        checkAlerts(distance);

        Intent intent = new Intent(ACTION_LOCATION_UPDATE);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_LATITUDE, location.getLatitude());
        intent.putExtra(EXTRA_LONGITUDE, location.getLongitude());
        intent.putExtra(EXTRA_DISTANCE, distance);
        sendBroadcast(intent);
    }

    private void checkAlerts(float distance) {
        if (distance <= 100.0f && !this.alert100Fired) {
            triggerAlert("¡BAJAR AHORA!", "Has llegado a tu destino.", 3);
            this.alert100Fired = true;
            this.alert300Fired = true;
            this.alert1000Fired = true;
        } else if (distance <= 300.0f && !this.alert300Fired) {
            triggerAlert("Prepararse para bajar", "Faltan 300 metros.", 2);
            this.alert300Fired = true;
            this.alert1000Fired = true;
        } else if (distance <= 1000.0f && !this.alert1000Fired) {
            triggerAlert("Falta poco para llegar", "Faltan 1000 metros.", 1);
            this.alert1000Fired = true;
        } else {
            updateNotification(String.format(Locale.getDefault(), "A %.0f metros de %s", distance, this.destName != null ? this.destName : ""));
        }
    }

    private void triggerAlert(String title, String message, int level) {
        updateNotification(title + ": " + message);
        playAlertSound(level);
        vibrate(level);
    }

    private void playAlertSound(int level) {
        int ringtoneType = (level >= 2) ? RingtoneManager.TYPE_ALARM : RingtoneManager.TYPE_NOTIFICATION;
        Uri soundUri = RingtoneManager.getDefaultUri(ringtoneType);
        if (soundUri == null) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        try {
            RingtoneManager.getRingtone(getApplicationContext(), soundUri).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrate(int level) {
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager != null ? vibratorManager.getDefaultVibrator() : null;
        } else {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        long[] pattern;
        switch (level) {
            case 2:
                pattern = new long[]{0, 700, 200, 700, 200, 700};
                break;
            case 3:
                pattern = new long[]{0, 1000, 200, 1000, 200, 1000, 200, 1000, 200, 1000};
                break;
            default:
                pattern = new long[]{0, 500, 200, 500};
                break;
        }
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Seguimiento de Viaje",
                    NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification createNotification(String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("NoMePaso")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    private void updateNotification(String contentText) {
        Notification notification = createNotification(contentText);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.fusedLocationClient != null && this.locationCallback != null) {
            this.fusedLocationClient.removeLocationUpdates(this.locationCallback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
