package com.example.meditrackaiproject.patient;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemAdherenceBinding;
import com.example.meditrackaiproject.models.MedicationLog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdherenceAdapter extends RecyclerView.Adapter<AdherenceAdapter.ViewHolder> {

    private final List<MedicationLog> logs;

    public AdherenceAdapter(List<MedicationLog> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdherenceBinding binding = ItemAdherenceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicationLog log = logs.get(position);
        holder.binding.tvMedName.setText(log.getMedName());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        holder.binding.tvTimestamp.setText(sdf.format(new Date(log.getTimestamp())));
        
        holder.binding.tvStatus.setText(log.getStatus());
        if ("Taken".equalsIgnoreCase(log.getStatus())) {
            holder.binding.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.binding.tvStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemAdherenceBinding binding;
        ViewHolder(ItemAdherenceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}