package com.example.meditrackaiproject.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityAdminPrescriptionsBinding;
import com.example.meditrackaiproject.models.Prescription;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminPrescriptionsActivity extends AppCompatActivity {

    private ActivityAdminPrescriptionsBinding binding;
    private DatabaseReference mDatabase;
    private List<Prescription> prescriptionList;
    private AdminPrescriptionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminPrescriptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("Prescriptions");
        prescriptionList = new ArrayList<>();
        
        binding.rvPrescriptions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminPrescriptionAdapter(prescriptionList);
        binding.rvPrescriptions.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());

        loadPrescriptions();
    }

    private void loadPrescriptions() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                prescriptionList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Prescription prescription = data.getValue(Prescription.class);
                    if (prescription != null) {
                        prescriptionList.add(prescription);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminPrescriptionsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}