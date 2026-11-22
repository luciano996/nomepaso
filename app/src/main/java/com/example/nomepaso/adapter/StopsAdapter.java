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

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.StopViewHolder> {
    private OnStopClickListener listener;
    private List<Stop> stops;

    public interface OnStopClickListener {
        void onDeleteClick(Stop stop, int i);

        void onEditClick(Stop stop, int i);

        void onStopClick(Stop stop);
    }

    public StopsAdapter(List<Stop> stops, OnStopClickListener listener) {
        this.stops = stops;
        this.listener = listener;
    }

    @Override
    public StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StopViewHolder holder, final int position) {
        final Stop stop = this.stops.get(position);
        holder.tvName.setText(stop.getName());
        holder.tvCoordinates.setText(String.format("Lat: %.5f, Lon: %.5f", Double.valueOf(stop.getLatitude()), Double.valueOf(stop.getLongitude())));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onStopClick(stop);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditClick(stop, position);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteClick(stop, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.stops.size();
    }

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
