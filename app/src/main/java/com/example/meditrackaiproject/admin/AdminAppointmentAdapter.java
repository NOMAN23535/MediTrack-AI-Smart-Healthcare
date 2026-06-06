package com.example.meditrackaiproject.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.databinding.ItemAppointmentBinding;
import com.example.meditrackaiproject.models.Appointment;
import java.util.List;

public class AdminAppointmentAdapter extends RecyclerView.Adapter<AdminAppointmentAdapter.ViewHolder> {

    private final List<Appointment> appointments;

    public AdminAppointmentAdapter(List<Appointment> appointments) {
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
        // Using correct IDs from item_appointment.xml
        holder.binding.tvPatientName.setText(appointment.getPatientName() + " (Dr. " + appointment.getDoctorName() + ")");
        holder.binding.tvStatus.setText(appointment.getStatus());
        holder.binding.tvAppointmentDateTime.setText(appointment.getDate() + " | " + appointment.getTime());
        holder.binding.tvSymptoms.setText("Symptoms: " + appointment.getSymptoms());
        
        // Admin view is read-only for now
        holder.binding.btnAccept.setVisibility(View.GONE);
        holder.binding.btnReject.setVisibility(View.GONE);
        holder.binding.btnPrescribe.setVisibility(View.GONE);
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