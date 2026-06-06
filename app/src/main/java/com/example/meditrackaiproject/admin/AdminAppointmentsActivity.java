package com.example.meditrackaiproject.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityAdminAppointmentsBinding;
import com.example.meditrackaiproject.models.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminAppointmentsActivity extends AppCompatActivity {

    private ActivityAdminAppointmentsBinding binding;
    private DatabaseReference mDatabase;
    private List<Appointment> appointmentList;
    private AdminAppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("Appointments");
        appointmentList = new ArrayList<>();
        
        binding.rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminAppointmentAdapter(appointmentList);
        binding.rvAppointments.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());

        loadAppointments();
    }

    private void loadAppointments() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Appointment appointment = data.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminAppointmentsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}