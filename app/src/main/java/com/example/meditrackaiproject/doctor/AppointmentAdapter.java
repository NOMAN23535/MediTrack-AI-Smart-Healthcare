package com.example.meditrackaiproject.doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ItemAppointmentBinding;
import com.example.meditrackaiproject.models.Appointment;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private final List<Appointment> appointments;
    private final OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onAccept(Appointment appointment);
        void onReject(Appointment appointment);
        void onViewDetails(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointments, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.listener = listener;
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
        holder.binding.tvPatientName.setText(appointment.getPatientName());
        holder.binding.tvAppointmentDateTime.setText(appointment.getDate() + " | " + appointment.getTime());
        holder.binding.tvSymptoms.setText("Symptoms: " + appointment.getSymptoms());
        holder.binding.tvStatus.setText(appointment.getStatus());

        // Change status text color
        if ("Approved".equals(appointment.getStatus())) {
            holder.binding.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success_green));
            holder.binding.btnAccept.setVisibility(View.GONE);
            holder.binding.btnReject.setVisibility(View.GONE);
            holder.binding.btnPrescribe.setVisibility(View.VISIBLE);
        } else if ("Pending".equals(appointment.getStatus())) {
            holder.binding.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.warning_orange));
            holder.binding.btnAccept.setVisibility(View.VISIBLE);
            holder.binding.btnReject.setVisibility(View.VISIBLE);
            holder.binding.btnPrescribe.setVisibility(View.GONE);
        } else {
            holder.binding.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.error_red));
            holder.binding.btnAccept.setVisibility(View.GONE);
            holder.binding.btnReject.setVisibility(View.GONE);
            holder.binding.btnPrescribe.setVisibility(View.GONE);
        }

        holder.binding.btnAccept.setOnClickListener(v -> listener.onAccept(appointment));
        holder.binding.btnReject.setOnClickListener(v -> listener.onReject(appointment));
        
        // Clicking the card or prescribe button opens details/prescription
        holder.itemView.setOnClickListener(v -> listener.onViewDetails(appointment));
        holder.binding.btnPrescribe.setOnClickListener(v -> listener.onViewDetails(appointment));
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