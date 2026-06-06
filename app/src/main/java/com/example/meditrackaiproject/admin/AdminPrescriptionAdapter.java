package com.example.meditrackaiproject.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemPrescriptionBinding;
import com.example.meditrackaiproject.models.Prescription;
import java.util.List;

public class AdminPrescriptionAdapter extends RecyclerView.Adapter<AdminPrescriptionAdapter.ViewHolder> {

    private final List<Prescription> prescriptions;

    public AdminPrescriptionAdapter(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPrescriptionBinding binding = ItemPrescriptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prescription prescription = prescriptions.get(position);
        holder.binding.tvDoctorName.setText("Dr. " + prescription.getDoctorName() + " -> Patient: " + prescription.getPatientName());
        holder.binding.tvDate.setText(prescription.getDate());
        holder.binding.tvDiagnosis.setText(prescription.getDiagnosis());
        
        // Admin view is read-only
        holder.binding.btnViewDetails.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPrescriptionBinding binding;
        ViewHolder(ItemPrescriptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}