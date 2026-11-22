package com.example.nomepaso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.nomepaso.service.TrackingService;
import java.util.Locale;

/* loaded from: classes5.dex */
public class TrackingActivity extends AppCompatActivity {
    private double destLat;
    private double destLon;
    private String destName;
    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() { // from class: com.example.nomepaso.TrackingActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (TrackingService.ACTION_LOCATION_UPDATE.equals(intent.getAction())) {
                double lat = intent.getDoubleExtra(TrackingService.EXTRA_LATITUDE, 0.0d);
                double lon = intent.getDoubleExtra(TrackingService.EXTRA_LONGITUDE, 0.0d);
                float distance = intent.getFloatExtra(TrackingService.EXTRA_DISTANCE, 0.0f);
                TrackingActivity.this.updateUI(lat, lon, distance);
            }
        }
    };
    private TextView tvCurrentLat;
    private TextView tvCurrentLon;
    private TextView tvDestName;
    private TextView tvDistance;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        this.tvDestName = (TextView) findViewById(R.id.tvDestName);
        this.tvDistance = (TextView) findViewById(R.id.tvDistance);
        this.tvCurrentLat = (TextView) findViewById(R.id.tvCurrentLat);
        this.tvCurrentLon = (TextView) findViewById(R.id.tvCurrentLon);
        Button btnStop = (Button) findViewById(R.id.btnStopTracking);
        this.destName = getIntent().getStringExtra("DEST_NAME");
        this.destLat = getIntent().getDoubleExtra("DEST_LAT", 0.0d);
        this.destLon = getIntent().getDoubleExtra("DEST_LON", 0.0d);
        this.tvDestName.setText(this.destName);
        startTrackingService();
        btnStop.setOnClickListener(new View.OnClickListener() { // from class: com.example.nomepaso.TrackingActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TrackingActivity.this.m150lambda$onCreate$0$comexamplenomepasoTrackingActivity(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-nomepaso-TrackingActivity, reason: not valid java name */
    public /* synthetic */ void m150lambda$onCreate$0$comexamplenomepasoTrackingActivity(View v) {
        stopService(new Intent(this, (Class<?>) TrackingService.class));
        finish();
    }

    private void startTrackingService() {
        Intent serviceIntent = new Intent(this, (Class<?>) TrackingService.class);
        serviceIntent.putExtra("DEST_NAME", this.destName);
        serviceIntent.putExtra("DEST_LAT", this.destLat);
        serviceIntent.putExtra("DEST_LON", this.destLon);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUI(double lat, double lon, float distanceInMeters) {
        this.tvCurrentLat.setText(String.format(Locale.getDefault(), "Lat: %.6f", Double.valueOf(lat)));
        this.tvCurrentLon.setText(String.format(Locale.getDefault(), "Lon: %.6f", Double.valueOf(lon)));
        if (distanceInMeters >= 1000.0f) {
            this.tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", Float.valueOf(distanceInMeters / 1000.0f)));
        } else {
            this.tvDistance.setText(String.format(Locale.getDefault(), "%.0f m", Float.valueOf(distanceInMeters)));
        }
        if (distanceInMeters < 300.0f) {
            this.tvDistance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else if (distanceInMeters < 1000.0f) {
            this.tvDistance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        } else {
            this.tvDistance.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TrackingService.ACTION_LOCATION_UPDATE);
        registerReceiver(this.locationReceiver, filter, 4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        unregisterReceiver(this.locationReceiver);
    }
}
