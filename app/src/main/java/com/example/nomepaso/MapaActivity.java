package com.example.nomepaso;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.nomepaso.data.JsonDataManager;
import com.example.nomepaso.model.Stop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.DefaultConfigurationProvider;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.modules.DatabaseFileArchive;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapaActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private JsonDataManager dataManager;
    private MyLocationNewOverlay locationOverlay;
    private List<Stop> savedStops;
    private MapView map = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dataManager = new JsonDataManager(this);
        this.savedStops = this.dataManager.loadStops();

        Configuration.getInstance().setUserAgentValue("NoMePasoApp/1.0");
        File basePath = new File(getCacheDir().getAbsolutePath(), DefaultConfigurationProvider.DEFAULT_USER_AGENT);
        Configuration.getInstance().setOsmdroidBasePath(basePath);
        File tileCache = new File(basePath.getAbsolutePath(), DatabaseFileArchive.COLUMN_TILE);
        Configuration.getInstance().setOsmdroidTileCache(tileCache);

        setContentView(R.layout.activity_mapa);
        this.map = findViewById(R.id.map);
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.map.setMultiTouchControls(true);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        setupLocationOverlay();
        setupMapEvents();
        displaySavedMarkers();
    }

    private void setupMapEvents() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                showAddStopDialog(p);
                return true;
            }
        };
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mReceive);
        this.map.getOverlays().add(0, mapEventsOverlay);
    }

    private void showAddStopDialog(final GeoPoint p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Parada");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nombre del destino (Ej: Casa, Trabajo)");
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                saveNewStop(name, p);
            } else {
                Toast.makeText(MapaActivity.this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveNewStop(String name, GeoPoint p) {
        Stop newStop = new Stop(name, p.getLatitude(), p.getLongitude());
        this.savedStops.add(newStop);
        this.dataManager.saveStops(this.savedStops);
        addMarkerToMap(newStop);
        Toast.makeText(this, "Destino guardado: " + name, Toast.LENGTH_SHORT).show();
    }

    private void displaySavedMarkers() {
        for (Stop stop : this.savedStops) {
            addMarkerToMap(stop);
        }
    }

    private void addMarkerToMap(Stop stop) {
        Marker startMarker = new Marker(this.map);
        startMarker.setPosition(new GeoPoint(stop.getLatitude(), stop.getLongitude()));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle(stop.getName());
        this.map.getOverlays().add(startMarker);
        this.map.invalidate();
    }

    private void setupLocationOverlay() {
        if (this.locationOverlay == null) {
            this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), this.map);
            this.locationOverlay.enableMyLocation();
            this.locationOverlay.enableFollowLocation();
            this.locationOverlay.runOnFirstFix(() -> runOnUiThread(() -> {
                if (this.locationOverlay != null && this.locationOverlay.getMyLocation() != null) {
                    this.map.getController().animateTo(this.locationOverlay.getMyLocation());
                    this.map.getController().setZoom(17.0d);
                }
            }));
            this.map.getOverlays().add(this.locationOverlay);
            this.map.getController().setZoom(15.0d);
            GeoPoint startPoint = new GeoPoint(-34.6037d, -58.3816d);
            this.map.getController().setCenter(startPoint);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.map != null) {
            this.map.onResume();
        }
        if (this.locationOverlay != null) {
            this.locationOverlay.enableMyLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.locationOverlay != null) {
            this.locationOverlay.disableMyLocation();
        }
        if (this.map != null) {
            this.map.onPause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationOverlay();
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
