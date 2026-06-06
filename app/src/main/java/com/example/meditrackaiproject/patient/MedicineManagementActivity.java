package com.example.meditrackaiproject.patient;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityMedicineManagementBinding;
import com.example.meditrackaiproject.models.Medicine;
import com.example.meditrackaiproject.models.MedicationLog;
import com.example.meditrackaiproject.models.Prescription;
import com.example.meditrackaiproject.utils.ReminderScheduler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MedicineManagementActivity extends AppCompatActivity {

    private ActivityMedicineManagementBinding binding;
    private DatabaseReference mDatabase;
    private String userId;
    private List<Medicine> medicineList;
    private MedicineInventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicineManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        medicineList = new ArrayList<>();
        adapter = new MedicineInventoryAdapter(medicineList, new MedicineInventoryAdapter.OnMedicineActionListener() {
            @Override
            public void onLogDose(Medicine medicine) {
                showLogDoseDialog(medicine);
            }

            @Override
            public void onToggleNotification(Medicine medicine, boolean enabled) {
                updateMedicineNotificationStatus(medicine, enabled);
            }
        });
        
        binding.rvMedicines.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMedicines.setAdapter(adapter);

        loadAssignedMedicines();

        binding.btnAddMedicine.setOnClickListener(v -> showAddStockDialog());
    }

    private void loadAssignedMedicines() {
        mDatabase.child("Prescriptions").orderByChild("patientId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        medicineList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Prescription prescription = postSnapshot.getValue(Prescription.class);
                            if (prescription != null && prescription.getMedicines() != null) {
                                for (Medicine med : prescription.getMedicines()) {
                                    if (!isAlreadyInList(med.getName())) {
                                        medicineList.add(med);
                                        if (med.isNotificationsEnabled()) {
                                            ReminderScheduler.scheduleMedicineReminders(MedicineManagementActivity.this, med);
                                        }
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private boolean isAlreadyInList(String name) {
        for (Medicine m : medicineList) {
            if (m.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    private void showLogDoseDialog(Medicine medicine) {
        String[] options = {"Medicine Taken", "Medicine Not Taken"};
        new AlertDialog.Builder(this)
                .setTitle("Log Dose for " + medicine.getName())
                .setItems(options, (dialog, which) -> {
                    String status = (which == 0) ? "Taken" : "Missed";
                    logMedicationDose(medicine, status);
                })
                .show();
    }

    private void logMedicationDose(Medicine medicine, String status) {
        if (status.equals("Taken") && medicine.getStockQuantity() <= 0) {
            Toast.makeText(this, "Out of stock! Please add more.", Toast.LENGTH_SHORT).show();
            return;
        }

        MedicationLog log = new MedicationLog(medicine.getName(), System.currentTimeMillis(), status);
        mDatabase.child("MedicationLogs").child(userId).push().setValue(log);
        
        if (status.equals("Taken")) {
            updateStock(medicine, medicine.getStockQuantity() - 1);
        }
        
        Toast.makeText(this, "Dose logged as " + status, Toast.LENGTH_SHORT).show();
    }

    private void updateStock(Medicine medicine, int newStock) {
        // We need to find where this medicine is stored. Prescriptions are grouped.
        // It's better to update it in all prescriptions or have a separate Medicines node.
        // For simplicity with current structure, update in Prescriptions.
        mDatabase.child("Prescriptions").orderByChild("patientId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot presSnapshot : snapshot.getChildren()) {
                            Prescription pres = presSnapshot.getValue(Prescription.class);
                            if (pres != null && pres.getMedicines() != null) {
                                boolean updated = false;
                                for (Medicine m : pres.getMedicines()) {
                                    if (m.getName().equalsIgnoreCase(medicine.getName())) {
                                        m.setStockQuantity(newStock);
                                        updated = true;
                                    }
                                }
                                if (updated) {
                                    presSnapshot.getRef().setValue(pres);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void updateMedicineNotificationStatus(Medicine medicine, boolean enabled) {
        mDatabase.child("Prescriptions").orderByChild("patientId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot presSnapshot : snapshot.getChildren()) {
                            Prescription pres = presSnapshot.getValue(Prescription.class);
                            if (pres != null && pres.getMedicines() != null) {
                                boolean updated = false;
                                for (Medicine m : pres.getMedicines()) {
                                    if (m.getName().equalsIgnoreCase(medicine.getName())) {
                                        m.setNotificationsEnabled(enabled);
                                        updated = true;
                                    }
                                }
                                if (updated) {
                                    presSnapshot.getRef().setValue(pres);
                                }
                            }
                        }
                        if (enabled) {
                            ReminderScheduler.scheduleMedicineReminders(MedicineManagementActivity.this, medicine);
                        } else {
                            ReminderScheduler.cancelMedicineReminders(MedicineManagementActivity.this, medicine);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showAddStockDialog() {
        if (medicineList.isEmpty()) {
            Toast.makeText(this, "No medicines available to add stock for.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] medNames = new String[medicineList.size()];
        for (int i = 0; i < medicineList.size(); i++) medNames[i] = medicineList.get(i).getName();

        new AlertDialog.Builder(this)
                .setTitle("Select Medicine")
                .setItems(medNames, (dialog, which) -> {
                    Medicine selectedMed = medicineList.get(which);
                    promptStockQuantity(selectedMed);
                })
                .show();
    }

    private void promptStockQuantity(Medicine medicine) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Available stock quantity");

        new AlertDialog.Builder(this)
                .setTitle("Add Stock for " + medicine.getName())
                .setMessage("How many medicines are available in stock?")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String val = input.getText().toString();
                    if (!val.isEmpty()) {
                        int qty = Integer.parseInt(val);
                        updateStock(medicine, medicine.getStockQuantity() + qty);
                        Toast.makeText(this, "Stock updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}