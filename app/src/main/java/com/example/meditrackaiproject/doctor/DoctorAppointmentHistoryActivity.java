package com.example.meditrackaiproject.doctor;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityDoctorAppointmentHistoryBinding;
import com.example.meditrackaiproject.models.Appointment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentHistoryActivity extends AppCompatActivity {

    private ActivityDoctorAppointmentHistoryBinding binding;
    private DatabaseReference mDatabase;
    private String doctorId;
    private AppointmentAdapter adapter;
    private List<Appointment> allAppointments = new ArrayList<>();
    private List<Appointment> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorAppointmentHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new AppointmentAdapter(filteredList, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onAccept(Appointment appointment) {} // Not needed in history
            @Override
            public void onReject(Appointment appointment) {} // Not needed in history
            @Override
            public void onViewDetails(Appointment appointment) {
                // Could open details if needed
            }
        });

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterAppointments();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadAppointments();
    }

    private void loadAppointments() {
        mDatabase.child("Appointments").orderByChild("doctorId").equalTo(doctorId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allAppointments.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Appointment appointment = postSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                if (appointment.getId() == null) appointment.setId(postSnapshot.getKey());
                                allAppointments.add(appointment);
                            }
                        }
                        filterAppointments();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DoctorAppointmentHistoryActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterAppointments() {
        filteredList.clear();
        int selectedTab = binding.tabLayout.getSelectedTabPosition();
        String targetStatus = (selectedTab == 0) ? "Completed" : "Rejected";

        for (Appointment app : allAppointments) {
            if (targetStatus.equals(app.getStatus())) {
                filteredList.add(app);
            }
        }
        adapter.notifyDataSetChanged();
    }
}