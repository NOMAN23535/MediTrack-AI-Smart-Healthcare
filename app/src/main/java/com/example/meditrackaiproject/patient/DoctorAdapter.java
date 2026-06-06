package com.example.meditrackaiproject.patient;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ItemDoctorBinding;
import com.example.meditrackaiproject.models.Doctor;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    private final List<Doctor> doctors;
    private final OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorClickListener listener) {
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDoctorBinding binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.binding.tvDoctorName.setText(doctor.getFullName());
        holder.binding.tvSpecialization.setText(doctor.getSpecialization());
        holder.binding.tvExperience.setText(doctor.getExperience() + " years experience");

        if (doctor.getProfileImageUrl() != null && !doctor.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(doctor.getProfileImageUrl())
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .into(holder.binding.ivDoctorProfile);
        } else {
            holder.binding.ivDoctorProfile.setImageResource(R.drawable.ic_doctor_placeholder);
        }

        holder.binding.btnBook.setOnClickListener(v -> listener.onDoctorClick(doctor));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDoctorBinding binding;
        ViewHolder(ItemDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}