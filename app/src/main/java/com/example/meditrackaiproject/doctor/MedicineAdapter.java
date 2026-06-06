package com.example.meditrackaiproject.doctor;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemMedicinePrescribedBinding;
import com.example.meditrackaiproject.models.Medicine;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private final List<Medicine> medicines;

    public MedicineAdapter(List<Medicine> medicines) {
        this.medicines = medicines;
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
        holder.binding.tvMedDetails.setText(med.getDosage() + " | " + med.getFrequency() + " times/day | " + med.getDuration() + " days");
        
        StringBuilder schedule = new StringBuilder();
        if (med.isMorning()) schedule.append("Morning ");
        if (med.isAfternoon()) schedule.append("Afternoon ");
        if (med.isEvening()) schedule.append("Evening ");
        if (med.isNight()) schedule.append("Night ");
        schedule.append(med.isBeforeMeal() ? "| Before Meal" : "| After Meal");
        if (med.isBeforeSleep()) schedule.append(" | Before Sleep");
        
        holder.binding.tvMedSchedule.setText(schedule.toString());
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