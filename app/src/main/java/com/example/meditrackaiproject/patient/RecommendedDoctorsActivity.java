package com.example.meditrackaiproject.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityRecommendedDoctorsBinding;
import com.example.meditrackaiproject.models.Doctor;
import com.example.meditrackaiproject.repository.AppRepository;
import java.util.ArrayList;
import java.util.List;

public class RecommendedDoctorsActivity extends AppCompatActivity {

    private ActivityRecommendedDoctorsBinding binding;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;
    private String specialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendedDoctorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        specialization = getIntent().getStringExtra("specialization");
        if (specialization != null && !specialization.isEmpty()) {
            binding.tvLabel.setText("Specialization: " + specialization);
        } else {
            binding.tvLabel.setText("Recommended Doctors");
        }

        binding.ivBack.setOnClickListener(v -> finish());

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(doctorList, this::onDoctorClick);
        binding.rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDoctors.setAdapter(adapter);

        fetchDoctors();
    }

    private void onDoctorClick(Doctor doctor) {
        Intent intent = new Intent(this, BookAppointmentActivity.class);
        intent.putExtra("doctorId", doctor.getUid());
        intent.putExtra("doctorName", doctor.getFullName());
        intent.putExtra("specialization", doctor.getSpecialization());
        startActivity(intent);
    }

    private void fetchDoctors() {
        AppRepository.getInstance().getRelevantDoctors(specialization, new AppRepository.RepositoryCallback<List<Doctor>>() {
            @Override
            public void onSuccess(List<Doctor> result) {
                doctorList.clear();
                doctorList.addAll(result);
                adapter.notifyDataSetChanged();
                
                if (doctorList.isEmpty()) {
                    Toast.makeText(RecommendedDoctorsActivity.this, "No doctors found for this specialization", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RecommendedDoctorsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}