package com.example.meditrackaiproject.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.databinding.ActivityAppointmentDetailBinding;
import com.example.meditrackaiproject.models.Appointment;
import com.example.meditrackaiproject.models.Notification;
import com.example.meditrackaiproject.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ActivityAppointmentDetailBinding binding;
    private DatabaseReference mDatabase;
    private String appointmentId;
    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        appointmentId = getIntent().getStringExtra("appointmentId");

        if (appointmentId == null) {
            Toast.makeText(this, "Error: No appointment ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.ivBack.setOnClickListener(v -> finish());

        loadAppointmentDetails();

        binding.btnApprove.setOnClickListener(v -> updateStatus("Approved"));
        binding.btnReject.setOnClickListener(v -> updateStatus("Rejected"));

        binding.btnCreatePrescription.setOnClickListener(v -> {
            if (appointment != null) {
                Intent intent = new Intent(this, PrescriptionActivity.class);
                intent.putExtra("appointmentId", appointment.getId());
                intent.putExtra("patientId", appointment.getPatientId());
                intent.putExtra("patientName", appointment.getPatientName());
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadAppointmentDetails() {
        mDatabase.child("Appointments").child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointment = snapshot.getValue(Appointment.class);
                if (appointment != null) {
                    binding.tvUserName.setText(appointment.getPatientName());
                    binding.tvSymptoms.setText(appointment.getSymptoms());
                    binding.tvTime.setText(appointment.getDate() + " - " + appointment.getTime());
                    binding.tvAnalysis.setText("Status: " + appointment.getStatus());
                    
                    if ("Approved".equals(appointment.getStatus())) {
                        binding.btnApprove.setVisibility(View.GONE);
                        binding.btnReject.setVisibility(View.GONE);
                        binding.btnCreatePrescription.setVisibility(View.VISIBLE);
                    } else if ("Pending".equals(appointment.getStatus())) {
                        binding.btnApprove.setVisibility(View.VISIBLE);
                        binding.btnReject.setVisibility(View.VISIBLE);
                        binding.btnCreatePrescription.setVisibility(View.GONE);
                    } else {
                        binding.btnApprove.setVisibility(View.GONE);
                        binding.btnReject.setVisibility(View.GONE);
                        binding.btnCreatePrescription.setVisibility(View.GONE);
                    }

                    // Fetch patient phone if needed
                    fetchPatientPhone(appointment.getPatientId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchPatientPhone(String patientId) {
        mDatabase.child("Users").child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.tvUserPhone.setText(user.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateStatus(String status) {
        if (appointment == null) return;

        mDatabase.child("Appointments").child(appointmentId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AppointmentDetailActivity.this, "Appointment " + status, Toast.LENGTH_SHORT).show();
                    sendNotificationToPatient(status);
                    finish();
                });
    }

    private void sendNotificationToPatient(String status) {
        DatabaseReference notifRef = mDatabase.child("Notifications").child(appointment.getPatientId()).push();
        String id = notifRef.getKey();
        String title = "Appointment " + status;
        String message = "Your appointment with Dr. " + appointment.getDoctorName() + " has been " + status.toLowerCase();
        Notification notification = new Notification(id, title, message, "Appointment");
        notifRef.setValue(notification);
    }
}