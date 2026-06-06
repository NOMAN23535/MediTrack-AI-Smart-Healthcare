package com.example.meditrackaiproject.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ItemDoctorBinding;
import com.example.meditrackaiproject.models.Patient;
import java.util.List;

public class AdminPatientAdapter extends RecyclerView.Adapter<AdminPatientAdapter.ViewHolder> {

    private final List<Patient> patients;

    public AdminPatientAdapter(List<Patient> patients) {
        this.patients = patients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDoctorBinding binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.binding.tvDoctorName.setText(patient.getFullName());
        holder.binding.tvSpecialization.setText(patient.getEmail());
        String info = "Age: " + patient.getAge() + " | Gender: " + patient.getGender();
        holder.binding.tvExperience.setText(info);
        
        holder.binding.btnBook.setVisibility(View.GONE);

        if (patient.getProfileImageUrl() != null && !patient.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(patient.getProfileImageUrl())
                    .placeholder(R.drawable.profile)
                    .into(holder.binding.ivDoctorProfile);
        } else {
            holder.binding.ivDoctorProfile.setImageResource(R.drawable.profile);
        }
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDoctorBinding binding;
        ViewHolder(ItemDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}