package com.example.nomepaso;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nomepaso.adapter.StopsAdapter;
import com.example.nomepaso.data.JsonDataManager;
import com.example.nomepaso.model.Stop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

/* loaded from: classes5.dex */
public class MainActivity extends AppCompatActivity implements StopsAdapter.OnStopClickListener {
    private StopsAdapter adapter;
    private JsonDataManager dataManager;
    private RecyclerView rvStops;
    private List<Stop> stopsList;
    private TextView tvEmpty;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() { // from class: com.example.nomepaso.MainActivity$$ExternalSyntheticLambda2
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return MainActivity.lambda$onCreate$0(view, windowInsetsCompat);
            }
        });
        this.dataManager = new JsonDataManager(this);
        this.rvStops = (RecyclerView) findViewById(R.id.rvStops);
        this.tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        ImageButton btnConfig = (ImageButton) findViewById(R.id.btnConfig);
        fabAdd.setOnClickListener(new View.OnClickListener() { // from class: com.example.nomepaso.MainActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m140lambda$onCreate$1$comexamplenomepasoMainActivity(view);
            }
        });
        btnConfig.setOnClickListener(new View.OnClickListener() { // from class: com.example.nomepaso.MainActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.m141lambda$onCreate$2$comexamplenomepasoMainActivity(view);
            }
        });
        loadData();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ WindowInsetsCompat lambda$onCreate$0(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$1$com-example-nomepaso-MainActivity, reason: not valid java name */
    public /* synthetic */ void m140lambda$onCreate$1$comexamplenomepasoMainActivity(View v) {
        Intent intent = new Intent(this, (Class<?>) MapaActivity.class);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$2$com-example-nomepaso-MainActivity, reason: not valid java name */
    public /* synthetic */ void m141lambda$onCreate$2$comexamplenomepasoMainActivity(View v) {
        Intent intent = new Intent(this, (Class<?>) ConfiguracionActivity.class);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        this.stopsList = this.dataManager.loadStops();
        if (this.stopsList.isEmpty()) {
            this.tvEmpty.setVisibility(0);
            this.rvStops.setVisibility(8);
        } else {
            this.tvEmpty.setVisibility(8);
            this.rvStops.setVisibility(0);
            this.adapter = new StopsAdapter(this.stopsList, this);
            this.rvStops.setAdapter(this.adapter);
        }
    }

    @Override // com.example.nomepaso.adapter.StopsAdapter.OnStopClickListener
    public void onStopClick(Stop stop) {
        Intent intent = new Intent(this, (Class<?>) TrackingActivity.class);
        intent.putExtra("DEST_NAME", stop.getName());
        intent.putExtra("DEST_LAT", stop.getLatitude());
        intent.putExtra("DEST_LON", stop.getLongitude());
        startActivity(intent);
    }

    @Override // com.example.nomepaso.adapter.StopsAdapter.OnStopClickListener
    public void onEditClick(final Stop stop, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Nombre");
        final EditText input = new EditText(this);
        input.setText(stop.getName());
        builder.setView(input);
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() { // from class: com.example.nomepaso.MainActivity$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.m143lambda$onEditClick$3$comexamplenomepasoMainActivity(input, stop, position, dialogInterface, i);
            }
        });
        builder.setNegativeButton("Cancelar", (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onEditClick$3$com-example-nomepaso-MainActivity, reason: not valid java name */
    public /* synthetic */ void m143lambda$onEditClick$3$comexamplenomepasoMainActivity(EditText input, Stop stop, int position, DialogInterface dialog, int which) {
        String newName = input.getText().toString();
        if (!newName.isEmpty()) {
            stop.setName(newName);
            this.dataManager.saveStops(this.stopsList);
            this.adapter.notifyItemChanged(position);
        }
    }

    @Override // com.example.nomepaso.adapter.StopsAdapter.OnStopClickListener
    public void onDeleteClick(Stop stop, final int position) {
        new AlertDialog.Builder(this).setTitle("Eliminar destino").setMessage("¿Estás seguro de que quieres eliminar " + stop.getName() + "?").setPositiveButton("Sí", new DialogInterface.OnClickListener() { // from class: com.example.nomepaso.MainActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.m142lambda$onDeleteClick$4$comexamplenomepasoMainActivity(position, dialogInterface, i);
            }
        }).setNegativeButton("No", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onDeleteClick$4$com-example-nomepaso-MainActivity, reason: not valid java name */
    public /* synthetic */ void m142lambda$onDeleteClick$4$comexamplenomepasoMainActivity(int position, DialogInterface dialog, int which) {
        this.stopsList.remove(position);
        this.dataManager.saveStops(this.stopsList);
        loadData();
    }
}
