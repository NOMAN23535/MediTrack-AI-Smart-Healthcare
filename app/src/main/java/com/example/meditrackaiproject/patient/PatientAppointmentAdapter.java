package com.example.meditrackaiproject.patient;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemAppointmentBinding;
import com.example.meditrackaiproject.models.Appointment;
import java.util.List;

public class PatientAppointmentAdapter extends RecyclerView.Adapter<PatientAppointmentAdapter.ViewHolder> {

    private final List<Appointment> appointments;

    public PatientAppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentBinding binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.binding.tvPatientName.setText("Dr. " + appointment.getDoctorName());
        holder.binding.tvAppointmentDateTime.setText(appointment.getDate() + " at " + appointment.getTime());
        holder.binding.tvSymptoms.setText("Symptoms: " + appointment.getSymptoms());
        holder.binding.tvStatus.setText("Status: " + appointment.getStatus());
        
        // Hide actions for patient view
        holder.binding.layoutActions.setVisibility(ViewGroup.GONE);

        switch (appointment.getStatus()) {
            case "Pending":
                holder.binding.tvStatus.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "Approved":
                holder.binding.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Rejected":
                holder.binding.tvStatus.setTextColor(Color.RED);
                break;
            case "Completed":
                holder.binding.tvStatus.setTextColor(Color.BLUE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemAppointmentBinding binding;
        ViewHolder(ItemAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}