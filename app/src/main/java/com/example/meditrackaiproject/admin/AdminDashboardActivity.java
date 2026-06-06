package com.example.meditrackaiproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ActivityAdminDashboardBinding;
import com.example.meditrackaiproject.models.Doctor;
import com.example.meditrackaiproject.models.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private DatabaseReference mDatabase;
    private DoctorRequestAdapter adapter;
    private List<Doctor> doctorRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        doctorRequests = new ArrayList<>();
        adapter = new DoctorRequestAdapter(doctorRequests, this::onDoctorAction);

        binding.rvDoctorRequests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDoctorRequests.setAdapter(adapter);

        // Card Click Listeners
        binding.cardPatients.setOnClickListener(v -> startActivity(new Intent(this, AdminPatientsActivity.class)));
        binding.cardDoctors.setOnClickListener(v -> startActivity(new Intent(this, AdminDoctorsActivity.class)));
        binding.cardAppointments.setOnClickListener(v -> startActivity(new Intent(this, AdminAppointmentsActivity.class)));
        binding.cardPrescriptions.setOnClickListener(v -> startActivity(new Intent(this, AdminPrescriptionsActivity.class)));
        binding.cardReports.setOnClickListener(v -> startActivity(new Intent(this, AdminAppointmentsActivity.class)));

        // Feature: Show Pending Approvals in the same page by scrolling down
        binding.cardPendingDoctors.setOnClickListener(v -> {
            binding.nestedScrollView.post(() -> {
                int top = binding.tvPendingApprovalsTitle.getTop();
                binding.nestedScrollView.smoothScrollTo(0, top);
            });
        });

        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_profile) {
                startActivity(new Intent(this, LogoutActivity.class));
            } else if (id == R.id.nav_admin_doctors) {
                startActivity(new Intent(this, AdminDoctorsActivity.class));
            } else if (id == R.id.nav_admin_patients) {
                startActivity(new Intent(this, AdminPatientsActivity.class));
            } else if (id == R.id.nav_admin_dashboard) {
                // Already on Dashboard
            }
            return true;
        });

        loadStats();
        loadDoctorRequests();
    }

    private void loadStats() {
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int patients = 0;
                int doctors = 0;
                int pending = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String role = postSnapshot.child("role").getValue(String.class);
                    String status = postSnapshot.child("status").getValue(String.class);
                    if ("Patient".equals(role)) patients++;
                    else if ("Doctor".equals(role)) {
                        doctors++;
                        if ("Pending".equals(status)) {
                            pending++;
                        }
                    }
                }
                binding.tvTotalPatients.setText(String.valueOf(patients));
                binding.tvTotalDoctors.setText(String.valueOf(doctors));
                binding.tvPendingDoctors.setText(String.valueOf(pending));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        mDatabase.child("Appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.tvTotalAppointments.setText(String.valueOf(snapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        mDatabase.child("Prescriptions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.tvTotalPrescriptions.setText(String.valueOf(snapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        mDatabase.child("Reports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.tvTotalReports.setText(String.valueOf(snapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadDoctorRequests() {
        mDatabase.child("Users").orderByChild("role").equalTo("Doctor")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctorRequests.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Doctor doctor = postSnapshot.getValue(Doctor.class);
                            if (doctor != null && "Pending".equals(doctor.getStatus())) {
                                doctorRequests.add(doctor);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.tvNoPending.setVisibility(doctorRequests.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void onDoctorAction(Doctor doctor, boolean approved) {
        String status = approved ? "Approved" : "Rejected";
        mDatabase.child("Users").child(doctor.getUid()).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Doctor " + status, Toast.LENGTH_SHORT).show();
                    sendNotification(doctor.getUid(), "Account " + status, 
                        approved ? "Congratulations! Your professional account has been approved." 
                                 : "We regret to inform you that your registration request was not approved.", 
                        approved ? "Approval" : "Rejection");
                });
    }

    private void sendNotification(String userId, String title, String message, String type) {
        DatabaseReference notifRef = mDatabase.child("Notifications").child(userId).push();
        String id = notifRef.getKey();
        Notification notification = new Notification(id, title, message, type);
        notifRef.setValue(notification);
    }
}