package com.example.meditrackaiproject.patient;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemMedicineInventoryBinding;
import com.example.meditrackaiproject.models.Medicine;
import java.util.List;

public class MedicineInventoryAdapter extends RecyclerView.Adapter<MedicineInventoryAdapter.ViewHolder> {

    private final List<Medicine> medicines;
    private final OnMedicineActionListener listener;

    public interface OnMedicineActionListener {
        void onLogDose(Medicine medicine);
        void onToggleNotification(Medicine medicine, boolean enabled);
    }

    public MedicineInventoryAdapter(List<Medicine> medicines, OnMedicineActionListener listener) {
        this.medicines = medicines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMedicineInventoryBinding binding = ItemMedicineInventoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine med = medicines.get(position);
        holder.binding.tvMedName.setText(med.getName());
        
        StringBuilder timing = new StringBuilder();
        if (med.isMorning()) timing.append("Morning ");
        if (med.isAfternoon()) timing.append("Afternoon ");
        if (med.isEvening()) timing.append("Evening ");
        if (med.isNight() || med.isBeforeSleep()) timing.append("Night ");
        
        holder.binding.tvMedDosage.setText(med.getDosage() + " | " + timing.toString().trim());
        holder.binding.tvStock.setText("Stock Remaining: " + med.getStockQuantity() + " Pills");
        
        holder.binding.switchNotifications.setChecked(med.isNotificationsEnabled());
        
        holder.binding.btnLogDose.setOnClickListener(v -> listener.onLogDose(med));
        
        holder.binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onToggleNotification(med, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMedicineInventoryBinding binding;
        ViewHolder(ItemMedicineInventoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}