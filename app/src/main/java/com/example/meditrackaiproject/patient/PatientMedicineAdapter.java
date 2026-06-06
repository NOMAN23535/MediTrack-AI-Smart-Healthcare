package com.example.meditrackaiproject.patient;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemMedicinePrescribedBinding;
import com.example.meditrackaiproject.models.Medicine;
import java.util.List;

public class PatientMedicineAdapter extends RecyclerView.Adapter<PatientMedicineAdapter.ViewHolder> {

    private final List<Medicine> medicines;
    private final OnMedicineActionListener listener;

    public interface OnMedicineActionListener {
        void onEdit(Medicine medicine, int position);
        void onDelete(int position);
    }

    public PatientMedicineAdapter(List<Medicine> medicines, OnMedicineActionListener listener) {
        this.medicines = medicines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMedicinePrescribedBinding binding = ItemMedicinePrescribedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine med = medicines.get(position);
        holder.binding.tvMedName.setText(med.getName());
        holder.binding.tvMedDetails.setText(med.getDosage() + " | " + med.getFrequency() + " | " + med.getDuration());
        
        StringBuilder schedule = new StringBuilder();
        if (med.isMorning()) schedule.append("Morning ");
        if (med.isAfternoon()) schedule.append("Afternoon ");
        if (med.isEvening()) schedule.append("Evening ");
        if (med.isNight()) schedule.append("Night ");
        schedule.append(med.isBeforeMeal() ? "| Before Meal" : "| After Meal");
        if (med.isBeforeSleep()) schedule.append(" | Before Sleep");
        
        holder.binding.tvMedSchedule.setText(schedule.toString());
        
        holder.binding.ivEdit.setOnClickListener(v -> listener.onEdit(med, position));
        holder.binding.ivDelete.setOnClickListener(v -> listener.onDelete(position));
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMedicinePrescribedBinding binding;
        ViewHolder(ItemMedicinePrescribedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}