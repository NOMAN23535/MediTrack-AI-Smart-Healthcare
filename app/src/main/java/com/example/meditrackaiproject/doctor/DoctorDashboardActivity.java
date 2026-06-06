package com.example.meditrackaiproject.doctor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ActivityDoctorDashboardBinding;
import com.example.meditrackaiproject.models.Appointment;
import com.example.meditrackaiproject.models.Notification;
import com.example.meditrackaiproject.models.User;
import com.example.meditrackaiproject.notifications.NotificationsActivity;
import com.example.meditrackaiproject.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboardActivity extends AppCompatActivity {

    private ActivityDoctorDashboardBinding binding;
    private DatabaseReference mDatabase;
    private String doctorId;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadDoctorData();
        setupNavigation();

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(appointmentList, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onAccept(Appointment appointment) {
                updateAppointmentStatus(appointment, "Approved");
            }

            @Override
            public void onReject(Appointment appointment) {
                updateAppointmentStatus(appointment, "Rejected");
            }

            @Override
            public void onViewDetails(Appointment appointment) {
                Intent intent = new Intent(DoctorDashboardActivity.this, AppointmentDetailActivity.class);
                intent.putExtra("appointmentId", appointment.getId());
                startActivity(intent);
            }
        });

        binding.rvTodaySchedule.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTodaySchedule.setAdapter(adapter);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_doctor_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_doctor_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
            } else if (id == R.id.nav_doctor_appointments) {
                startActivity(new Intent(this, DoctorAppointmentHistoryActivity.class));
            } else if (id == R.id.nav_doctor_patients) {
                Toast.makeText(this, "Feature coming soon: Patients List", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_doctor_home) {
                // Already on Home
            }
            return true;
        });

        loadAppointments();
        loadStats();
    }

    private void setupNavigation() {
        binding.cardTotalPatients.setOnClickListener(v -> Toast.makeText(this, "Feature coming soon: Patients List", Toast.LENGTH_SHORT).show());
        binding.cardPendingRequests.setOnClickListener(v -> {
            // Focus on Pending
            Toast.makeText(this, "Showing Pending Appointments", Toast.LENGTH_SHORT).show();
        });
        binding.cardActiveCases.setOnClickListener(v -> {
             // Focus on Approved
             Toast.makeText(this, "Showing Active Cases", Toast.LENGTH_SHORT).show();
        });
        binding.cardPrescriptions.setOnClickListener(v -> {
            // Open Prescription History or similar
            Toast.makeText(this, "Opening Prescriptions History", Toast.LENGTH_SHORT).show();
        });
        binding.ivDoctorProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadDoctorData() {
        mDatabase.child("Users").child(doctorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.tvWelcome.setText(String.format("Hi, %s", user.getFullName() != null ? user.getFullName() : "Doctor"));
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(DoctorDashboardActivity.this)
                                .load(Uri.parse(user.getProfileImageUrl()))
                                .placeholder(R.drawable.ic_doctor_placeholder)
                                .into(binding.ivDoctorProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadStats() {
        mDatabase.child("Appointments").orderByChild("doctorId").equalTo(doctorId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int pending = 0;
                        int active = 0;
                        List<String> patientIds = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            String status = postSnapshot.child("status").getValue(String.class);
                            String pId = postSnapshot.child("patientId").getValue(String.class);
                            if ("Pending".equals(status)) pending++;
                            else if ("Approved".equals(status)) active++;
                            
                            if (pId != null && !patientIds.contains(pId)) {
                                patientIds.add(pId);
                            }
                        }
                        binding.tvPendingRequests.setText(String.valueOf(pending));
                        binding.tvActiveCases.setText(String.valueOf(active));
                        binding.tvTotalPatients.setText(String.valueOf(patientIds.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        mDatabase.child("Prescriptions").orderByChild("doctorId").equalTo(doctorId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.tvTotalPrescriptions.setText(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadAppointments() {
        mDatabase.child("Appointments").orderByChild("doctorId").equalTo(doctorId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Appointment appointment = postSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                if (appointment.getId() == null) appointment.setId(postSnapshot.getKey());
                                
                                if (!"Completed".equals(appointment.getStatus()) && !"Rejected".equals(appointment.getStatus())) {
                                    appointmentList.add(appointment);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DoctorDashboardActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAppointmentStatus(Appointment appointment, String status) {
        if (appointment.getId() == null) return;
        
        mDatabase.child("Appointments").child(appointment.getId()).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment " + status, Toast.LENGTH_SHORT).show();
                    String title = "Appointment " + status;
                    String message = String.format("Your appointment with Dr. %s on %s has been %s.", appointment.getDoctorName(), appointment.getDate(), status.toLowerCase());
                    sendNotificationToPatient(appointment.getPatientId(), title, message);
                });
    }

    private void sendNotificationToPatient(String patientId, String title, String message) {
        DatabaseReference notifRef = mDatabase.child("Notifications").child(patientId).push();
        String id = notifRef.getKey();
        Notification notification = new Notification(id, title, message, "Appointment");
        notifRef.setValue(notification);
    }
}