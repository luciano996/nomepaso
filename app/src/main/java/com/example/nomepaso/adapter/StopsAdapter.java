package com.example.nomepaso.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nomepaso.R;
import com.example.nomepaso.model.Stop;
import com.google.android.material.button.MaterialButton;
import java.util.List;

/* loaded from: classes7.dex */
public class StopsAdapter extends RecyclerView.Adapter<StopViewHolder> {
    private OnStopClickListener listener;
    private List<Stop> stops;

    /* loaded from: classes7.dex */
    public interface OnStopClickListener {
        void onDeleteClick(Stop stop, int i);

        void onEditClick(Stop stop, int i);

        void onStopClick(Stop stop);
    }

    public StopsAdapter(List<Stop> stops, OnStopClickListener listener) {
        this.stops = stops;
        this.listener = listener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(StopViewHolder holder, final int position) {
        final Stop stop = this.stops.get(position);
        holder.tvName.setText(stop.getName());
        holder.tvCoordinates.setText(String.format("Lat: %.5f, Lon: %.5f", Double.valueOf(stop.getLatitude()), Double.valueOf(stop.getLongitude())));
        holder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.example.nomepaso.adapter.StopsAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StopsAdapter.this.m151xe2138246(stop, view);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() { // from class: com.example.nomepaso.adapter.StopsAdapter$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StopsAdapter.this.m152xc754f107(stop, position, view);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() { // from class: com.example.nomepaso.adapter.StopsAdapter$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StopsAdapter.this.m153xac965fc8(stop, position, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$0$com-example-nomepaso-adapter-StopsAdapter, reason: not valid java name */
    public /* synthetic */ void m151xe2138246(Stop stop, View v) {
        this.listener.onStopClick(stop);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$1$com-example-nomepaso-adapter-StopsAdapter, reason: not valid java name */
    public /* synthetic */ void m152xc754f107(Stop stop, int position, View v) {
        this.listener.onEditClick(stop, position);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$2$com-example-nomepaso-adapter-StopsAdapter, reason: not valid java name */
    public /* synthetic */ void m153xac965fc8(Stop stop, int position, View v) {
        this.listener.onDeleteClick(stop, position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.stops.size();
    }

    /* loaded from: classes7.dex */
    public static class StopViewHolder extends RecyclerView.ViewHolder {
        MaterialButton btnDelete;
        MaterialButton btnEdit;
        TextView tvCoordinates;
        TextView tvName;

        public StopViewHolder(View itemView) {
            super(itemView);
            this.tvName = (TextView) itemView.findViewById(R.id.tvStopName);
            this.tvCoordinates = (TextView) itemView.findViewById(R.id.tvStopCoordinates);
            this.btnEdit = (MaterialButton) itemView.findViewById(R.id.btnEdit);
            this.btnDelete = (MaterialButton) itemView.findViewById(R.id.btnDelete);
        }
    }
}
