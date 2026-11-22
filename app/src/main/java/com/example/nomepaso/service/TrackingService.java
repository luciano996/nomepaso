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
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import com.example.nomepaso.MainActivity;
import com.example.nomepaso.R;
import com.google.android.gms.location.DeviceOrientationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.Locale;

/* loaded from: classes8.dex */
public class TrackingService extends Service {
    public static final String ACTION_LOCATION_UPDATE = "com.example.nomepaso.LOCATION_UPDATE";
    private static final String CHANNEL_ID = "TrackingServiceChannel";
    public static final String EXTRA_DISTANCE = "extra_distance";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    private static final int NOTIFICATION_ID = 123;
    private double destLat;
    private double destLon;
    private String destName;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean alert1000Fired = false;
    private boolean alert300Fired = false;
    private boolean alert100Fired = false;

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.locationCallback = new LocationCallback() { // from class: com.example.nomepaso.service.TrackingService.1
            @Override // com.google.android.gms.location.LocationCallback
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    TrackingService.this.processLocation(location);
                }
            }
        };
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            this.destName = intent.getStringExtra("DEST_NAME");
            this.destLat = intent.getDoubleExtra("DEST_LAT", 0.0d);
            this.destLon = intent.getDoubleExtra("DEST_LON", 0.0d);
        }
        createNotificationChannel();
        Notification notification = createNotification("Seguimiento activo para " + this.destName);
        startForeground(NOTIFICATION_ID, notification);
        startLocationUpdates();
        return 1;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(100, DeviceOrientationRequest.OUTPUT_PERIOD_FAST).setMinUpdateIntervalMillis(2000L).build();
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            this.fusedLocationClient.requestLocationUpdates(locationRequest, this.locationCallback, Looper.getMainLooper());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processLocation(Location location) {
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
            return;
        }
        if (distance <= 300.0f && !this.alert300Fired) {
            triggerAlert("Prepararse para bajar", "Faltan 300 metros.", 2);
            this.alert300Fired = true;
            this.alert1000Fired = true;
        } else if (distance <= 1000.0f && !this.alert1000Fired) {
            triggerAlert("Falta poco para llegar", "Faltan 1000 metros.", 1);
            this.alert1000Fired = true;
        } else {
            updateNotification(String.format(Locale.getDefault(), "A %.0f metros de %s", Float.valueOf(distance), this.destName));
        }
    }

    private void triggerAlert(String title, String message, int level) {
        updateNotification(title + ": " + message);
        playAlertSound(level);
        vibrate(level);
    }

    private void playAlertSound(int level) {
        Uri soundUri;
        switch (level) {
            case 2:
                soundUri = RingtoneManager.getDefaultUri(2);
                break;
            case 3:
                soundUri = RingtoneManager.getDefaultUri(4);
                break;
            default:
                soundUri = RingtoneManager.getDefaultUri(2);
                break;
        }
        try {
            RingtoneManager.getRingtone(getApplicationContext(), soundUri).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrate(int level) {
        long[] pattern;
        Vibrator vibrator = (Vibrator) getSystemService("vibrator");
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }
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
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Seguimiento de Viaje", 4);
        serviceChannel.setSound(null, null);
        NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification(String contentText) {
        Intent notificationIntent = new Intent(this, (Class<?>) MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL);
        return new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("NoMePaso").setContentText(contentText).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).setOngoing(true).setPriority(1).build();
    }

    private void updateNotification(String contentText) {
        Notification notification = createNotification(contentText);
        NotificationManager manager = (NotificationManager) getSystemService("notification");
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.fusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }
}
