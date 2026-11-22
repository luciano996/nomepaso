package com.example.nomepaso;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nomepaso.adapter.StopsAdapter;
import com.example.nomepaso.data.JsonDataManager;
import com.example.nomepaso.model.Stop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StopsAdapter.OnStopClickListener {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private StopsAdapter adapter;
    private JsonDataManager dataManager;
    private RecyclerView rvStops;
    private List<Stop> stopsList;
    private TextView tvEmpty;
    private Stop pendingStopToTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.dataManager = new JsonDataManager(this);
        this.rvStops = findViewById(R.id.rvStops);
        this.rvStops.setLayoutManager(new LinearLayoutManager(this));
        this.tvEmpty = findViewById(R.id.tvEmpty);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        ImageButton btnConfig = findViewById(R.id.btnConfig);

        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, MapaActivity.class)));
        btnConfig.setOnClickListener(v -> startActivity(new Intent(this, ConfiguracionActivity.class)));

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        this.stopsList = this.dataManager.loadStops();
        if (this.stopsList == null || this.stopsList.isEmpty()) {
            this.tvEmpty.setVisibility(View.VISIBLE);
            this.rvStops.setVisibility(View.GONE);
        } else {
            this.tvEmpty.setVisibility(View.GONE);
            this.rvStops.setVisibility(View.VISIBLE);
            this.adapter = new StopsAdapter(this.stopsList, this);
            this.rvStops.setAdapter(this.adapter);
        }
    }

    @Override
    public void onStopClick(Stop stop) {
        this.pendingStopToTrack = stop;
        if (checkAndRequestPermissions()) {
            startTrackingActivity(stop);
        }
    }

    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted && this.pendingStopToTrack != null) {
                startTrackingActivity(this.pendingStopToTrack);
            } else if (!allGranted) {
                Toast.makeText(this, "Se requieren permisos de ubicación y notificaciones para el seguimiento.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startTrackingActivity(Stop stop) {
        Intent intent = new Intent(this, TrackingActivity.class);
        intent.putExtra("DEST_NAME", stop.getName());
        intent.putExtra("DEST_LAT", stop.getLatitude());
        intent.putExtra("DEST_LON", stop.getLongitude());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Stop stop, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Nombre");
        final EditText input = new EditText(this);
        input.setText(stop.getName());
        builder.setView(input);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                stop.setName(newName);
                this.dataManager.saveStops(this.stopsList);
                if (this.adapter != null) {
                    this.adapter.notifyItemChanged(position);
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    public void onDeleteClick(Stop stop, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar destino")
                .setMessage("¿Estás seguro de que quieres eliminar " + stop.getName() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (position >= 0 && position < this.stopsList.size()) {
                        this.stopsList.remove(position);
                        this.dataManager.saveStops(this.stopsList);
                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
