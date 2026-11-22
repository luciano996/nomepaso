package com.example.nomepaso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.nomepaso.service.TrackingService;
import java.util.Locale;

public class TrackingActivity extends AppCompatActivity {
    private String destName;
    private double destLat;
    private double destLon;

    private TextView tvDestName;
    private TextView tvDistance;
    private TextView tvCurrentLat;
    private TextView tvCurrentLon;

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && TrackingService.ACTION_LOCATION_UPDATE.equals(intent.getAction())) {
                double lat = intent.getDoubleExtra(TrackingService.EXTRA_LATITUDE, 0.0d);
                double lon = intent.getDoubleExtra(TrackingService.EXTRA_LONGITUDE, 0.0d);
                float distance = intent.getFloatExtra(TrackingService.EXTRA_DISTANCE, 0.0f);
                updateUI(lat, lon, distance);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        this.tvDestName = findViewById(R.id.tvDestName);
        this.tvDistance = findViewById(R.id.tvDistance);
        this.tvCurrentLat = findViewById(R.id.tvCurrentLat);
        this.tvCurrentLon = findViewById(R.id.tvCurrentLon);
        Button btnStop = findViewById(R.id.btnStopTracking);

        this.destName = getIntent().getStringExtra("DEST_NAME");
        this.destLat = getIntent().getDoubleExtra("DEST_LAT", 0.0d);
        this.destLon = getIntent().getDoubleExtra("DEST_LON", 0.0d);

        this.tvDestName.setText(this.destName);

        startTrackingService();

        btnStop.setOnClickListener(v -> {
            stopService(new Intent(TrackingActivity.this, TrackingService.class));
            finish();
        });
    }

    private void startTrackingService() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.putExtra("DEST_NAME", this.destName);
        serviceIntent.putExtra("DEST_LAT", this.destLat);
        serviceIntent.putExtra("DEST_LON", this.destLon);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void updateUI(double lat, double lon, float distanceInMeters) {
        this.tvCurrentLat.setText(String.format(Locale.getDefault(), "Lat: %.6f", lat));
        this.tvCurrentLon.setText(String.format(Locale.getDefault(), "Lon: %.6f", lon));

        if (distanceInMeters >= 1000.0f) {
            this.tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distanceInMeters / 1000.0f));
        } else {
            this.tvDistance.setText(String.format(Locale.getDefault(), "%.0f m", distanceInMeters));
        }

        if (distanceInMeters < 300.0f) {
            this.tvDistance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else if (distanceInMeters < 1000.0f) {
            this.tvDistance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        } else {
            this.tvDistance.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TrackingService.ACTION_LOCATION_UPDATE);
        ContextCompat.registerReceiver(this, this.locationReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(this.locationReceiver);
        } catch (IllegalArgumentException ignored) {
            // Receiver not registered
        }
    }
}
