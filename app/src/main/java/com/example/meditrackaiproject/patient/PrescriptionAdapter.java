package com.example.meditrackaiproject.patient;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemPrescriptionBinding;
import com.example.meditrackaiproject.models.Prescription;
import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    private final List<Prescription> prescriptions;
    private final OnPrescriptionClickListener listener;

    public interface OnPrescriptionClickListener {
        void onViewDetails(Prescription prescription);
    }

    public PrescriptionAdapter(List<Prescription> prescriptions, OnPrescriptionClickListener listener) {
        this.prescriptions = prescriptions;
        this.listener = listener;
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
        holder.binding.tvDoctorName.setText(prescription.getDoctorName());
        holder.binding.tvDate.setText(prescription.getDate());
        holder.binding.tvDiagnosis.setText(prescription.getDiagnosis());

        holder.binding.btnViewDetails.setOnClickListener(v -> listener.onViewDetails(prescription));
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