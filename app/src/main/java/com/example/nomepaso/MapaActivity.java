package com.example.nomepaso;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
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

/* loaded from: classes5.dex */
public class MapaActivity extends AppCompatActivity {
    private JsonDataManager dataManager;
    private MyLocationNewOverlay locationOverlay;
    private List<Stop> savedStops;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataManager = new JsonDataManager(this);
        this.savedStops = this.dataManager.loadStops();
        getApplicationContext();
        Configuration.getInstance().setUserAgentValue("NoMePasoApp/1.0 (https://github.com/tu-usuario/NoMePaso)");
        File basePath = new File(getCacheDir().getAbsolutePath(), DefaultConfigurationProvider.DEFAULT_USER_AGENT);
        Configuration.getInstance().setOsmdroidBasePath(basePath);
        File tileCache = new File(basePath.getAbsolutePath(), DatabaseFileArchive.COLUMN_TILE);
        Configuration.getInstance().setOsmdroidTileCache(tileCache);
        setContentView(R.layout.activity_mapa);
        this.map = (MapView) findViewById(R.id.map);
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.map.setMultiTouchControls(true);
        requestPermissionsIfNecessary(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"});
        setupLocationOverlay();
        setupMapEvents();
        displaySavedMarkers();
    }

    private void setupMapEvents() {
        MapEventsReceiver mReceive = new MapEventsReceiver() { // from class: com.example.nomepaso.MapaActivity.1
            @Override // org.osmdroid.events.MapEventsReceiver
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override // org.osmdroid.events.MapEventsReceiver
            public boolean longPressHelper(GeoPoint p) {
                MapaActivity.this.showAddStopDialog(p);
                return true;
            }
        };
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mReceive);
        this.map.getOverlays().add(0, mapEventsOverlay);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAddStopDialog(final GeoPoint p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Parada");
        final EditText input = new EditText(this);
        input.setInputType(1);
        input.setHint("Nombre del destino (Ej: Casa, Trabajo)");
        builder.setView(input);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() { // from class: com.example.nomepaso.MapaActivity$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MapaActivity.this.m147lambda$showAddStopDialog$0$comexamplenomepasoMapaActivity(input, p, dialogInterface, i);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() { // from class: com.example.nomepaso.MapaActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$showAddStopDialog$0$com-example-nomepaso-MapaActivity, reason: not valid java name */
    public /* synthetic */ void m147lambda$showAddStopDialog$0$comexamplenomepasoMapaActivity(EditText input, GeoPoint p, DialogInterface dialog, int which) {
        String name = input.getText().toString();
        if (!name.isEmpty()) {
            saveNewStop(name, p);
        } else {
            Toast.makeText(this, "El nombre no puede estar vacío", 0).show();
        }
    }

    private void saveNewStop(String name, GeoPoint p) {
        Stop newStop = new Stop(name, p.getLatitude(), p.getLongitude());
        this.savedStops.add(newStop);
        this.dataManager.saveStops(this.savedStops);
        addMarkerToMap(newStop);
        Toast.makeText(this, "Destino guardado: " + name, 0).show();
    }

    private void displaySavedMarkers() {
        for (Stop stop : this.savedStops) {
            addMarkerToMap(stop);
        }
    }

    private void addMarkerToMap(Stop stop) {
        Marker startMarker = new Marker(this.map);
        startMarker.setPosition(new GeoPoint(stop.getLatitude(), stop.getLongitude()));
        startMarker.setAnchor(0.5f, 1.0f);
        startMarker.setTitle(stop.getName());
        this.map.getOverlays().add(startMarker);
        this.map.invalidate();
    }

    private void setupLocationOverlay() {
        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), this.map);
        this.locationOverlay.enableMyLocation();
        this.locationOverlay.enableFollowLocation();
        this.locationOverlay.runOnFirstFix(new Runnable() { // from class: com.example.nomepaso.MapaActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MapaActivity.this.m146lambda$setupLocationOverlay$3$comexamplenomepasoMapaActivity();
            }
        });
        this.map.getOverlays().add(this.locationOverlay);
        this.map.getController().setZoom(15.0d);
        GeoPoint startPoint = new GeoPoint(-34.6037d, -58.3816d);
        this.map.getController().setCenter(startPoint);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$setupLocationOverlay$3$com-example-nomepaso-MapaActivity, reason: not valid java name */
    public /* synthetic */ void m146lambda$setupLocationOverlay$3$comexamplenomepasoMapaActivity() {
        runOnUiThread(new Runnable() { // from class: com.example.nomepaso.MapaActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MapaActivity.this.m145lambda$setupLocationOverlay$2$comexamplenomepasoMapaActivity();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$setupLocationOverlay$2$com-example-nomepaso-MapaActivity, reason: not valid java name */
    public /* synthetic */ void m145lambda$setupLocationOverlay$2$comexamplenomepasoMapaActivity() {
        if (this.locationOverlay.getMyLocation() != null) {
            this.map.getController().animateTo(this.locationOverlay.getMyLocation());
            this.map.getController().setZoom(17.0d);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (this.map != null) {
            this.map.onResume();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (this.map != null) {
            this.map.onPause();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result == 0) {
                    setupLocationOverlay();
                    return;
                }
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != 0) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, (String[]) permissionsToRequest.toArray(new String[0]), 1);
        }
    }
}
