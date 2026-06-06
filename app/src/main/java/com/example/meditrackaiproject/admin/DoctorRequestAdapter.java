package com.example.meditrackaiproject.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ItemDoctorRequestBinding;
import com.example.meditrackaiproject.models.Doctor;
import java.util.List;

public class DoctorRequestAdapter extends RecyclerView.Adapter<DoctorRequestAdapter.ViewHolder> {

    private final List<Doctor> doctors;
    private final OnDoctorActionListener listener;

    public interface OnDoctorActionListener {
        void onAction(Doctor doctor, boolean approved);
    }

    public DoctorRequestAdapter(List<Doctor> doctors, OnDoctorActionListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDoctorRequestBinding binding = ItemDoctorRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.binding.tvDoctorName.setText(doctor.getFullName());
        holder.binding.tvSpecialization.setText(doctor.getSpecialization());
        holder.binding.tvExp.setText(doctor.getQualifications() + " | " + doctor.getExperience() + " Years Exp");

        if (doctor.getProfileImageUrl() != null && !doctor.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(doctor.getProfileImageUrl())
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .into(holder.binding.ivDoctorProfile);
        } else {
            holder.binding.ivDoctorProfile.setImageResource(R.drawable.ic_doctor_placeholder);
        }

        holder.binding.btnApprove.setOnClickListener(v -> listener.onAction(doctor, true));
        holder.binding.btnReject.setOnClickListener(v -> listener.onAction(doctor, false));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDoctorRequestBinding binding;
        ViewHolder(ItemDoctorRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}