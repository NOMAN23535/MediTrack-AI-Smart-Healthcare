package com.example.meditrackaiproject.patient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityMedicineReportsBinding;
import com.example.meditrackaiproject.models.MedicationLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicineReportsActivity extends AppCompatActivity {

    private ActivityMedicineReportsBinding binding;
    private SharedPreferences preferences;
    private DatabaseReference mDatabase;
    private String userId;
    private List<MedicationLog> logList;
    private AdherenceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicineReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("MedicationLogs").child(userId);

        preferences = getSharedPreferences("MediTrackPrefs", MODE_PRIVATE);
        boolean remindersEnabled = preferences.getBoolean("reminders_enabled", true);
        binding.switchNotifications.setChecked(remindersEnabled);

        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("reminders_enabled", isChecked).apply();
            Toast.makeText(this, "Reminders " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        logList = new ArrayList<>();
        adapter = new AdherenceAdapter(logList);
        binding.rvAdherenceHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdherenceHistory.setAdapter(adapter);

        loadAdherenceHistory();
    }

    private void loadAdherenceHistory() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MedicationLog log = postSnapshot.getValue(MedicationLog.class);
                    if (log != null) {
                        logList.add(log);
                    }
                }
                Collections.reverse(logList); // Show latest logs first
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MedicineReportsActivity.this, "Failed to load logs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}