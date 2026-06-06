package com.example.meditrackaiproject.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityAdminPatientsBinding;
import com.example.meditrackaiproject.models.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminPatientsActivity extends AppCompatActivity {

    private ActivityAdminPatientsBinding binding;
    private DatabaseReference mDatabase;
    private List<Patient> patientList;
    private AdminPatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminPatientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        patientList = new ArrayList<>();
        
        binding.rvPatients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminPatientAdapter(patientList);
        binding.rvPatients.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());

        loadPatients();
    }

    private void loadPatients() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mDatabase.orderByChild("role").equalTo("Patient")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        patientList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Patient patient = data.getValue(Patient.class);
                            if (patient != null) {
                                patientList.add(patient);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.progressBar.setVisibility(View.GONE);
                        if (patientList.isEmpty()) {
                            Toast.makeText(AdminPatientsActivity.this, "No patients found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminPatientsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}