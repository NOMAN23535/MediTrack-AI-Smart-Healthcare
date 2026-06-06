package com.example.meditrackaiproject.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityAdminDoctorsBinding;
import com.example.meditrackaiproject.models.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminDoctorsActivity extends AppCompatActivity {

    private ActivityAdminDoctorsBinding binding;
    private DatabaseReference mDatabase;
    private List<Doctor> doctorList;
    private AdminDoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDoctorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        doctorList = new ArrayList<>();
        
        binding.rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminDoctorAdapter(doctorList);
        binding.rvDoctors.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());

        loadDoctors();
    }

    private void loadDoctors() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mDatabase.orderByChild("role").equalTo("Doctor")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctorList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Doctor doctor = data.getValue(Doctor.class);
                            if (doctor != null) {
                                doctorList.add(doctor);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.progressBar.setVisibility(View.GONE);
                        if (doctorList.isEmpty()) {
                            Toast.makeText(AdminDoctorsActivity.this, "No doctors found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminDoctorsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}