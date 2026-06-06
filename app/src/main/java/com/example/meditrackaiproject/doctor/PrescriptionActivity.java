package com.example.meditrackaiproject.doctor;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityPrescriptionBinding;
import com.example.meditrackaiproject.databinding.DialogAddMedicineBinding;
import com.example.meditrackaiproject.models.Medicine;
import com.example.meditrackaiproject.models.Prescription;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrescriptionActivity extends AppCompatActivity {

    private ActivityPrescriptionBinding binding;
    private String appointmentId, patientId, patientName;
    private List<Medicine> medicineList;
    private MedicineAdapter adapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appointmentId = getIntent().getStringExtra("appointmentId");
        patientId = getIntent().getStringExtra("patientId");
        patientName = getIntent().getStringExtra("patientName");

        if (appointmentId == null || patientId == null) {
            Toast.makeText(this, "Error: Missing appointment data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.tvPatientName.setText("Patient: " + patientName);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        medicineList = new ArrayList<>();
        adapter = new MedicineAdapter(medicineList);
        binding.rvPrescribedMedicines.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPrescribedMedicines.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnAddMedicine.setOnClickListener(v -> showAddMedicineDialog());
        binding.btnSubmitPrescription.setOnClickListener(v -> submitPrescription());
    }

    private void showAddMedicineDialog() {
        DialogAddMedicineBinding dialogBinding = DialogAddMedicineBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(this)
                .setTitle("Add Medicine")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = dialogBinding.etMedName.getText().toString().trim();
                    String dosage = dialogBinding.etDosage.getText().toString().trim();
                    String duration = dialogBinding.etDuration.getText().toString().trim();
                    String frequency = dialogBinding.etFrequency.getText().toString().trim();

                    if (!TextUtils.isEmpty(name)) {
                        Medicine med = new Medicine(name, dosage, duration, frequency);
                        med.setMorning(dialogBinding.cbMorning.isChecked());
                        med.setAfternoon(dialogBinding.cbAfternoon.isChecked());
                        med.setEvening(dialogBinding.cbEvening.isChecked());
                        med.setNight(dialogBinding.cbNight.isChecked());
                        med.setBeforeMeal(dialogBinding.rbBeforeMeal.isChecked());
                        med.setAfterMeal(dialogBinding.rbAfterMeal.isChecked());
                        med.setBeforeSleep(dialogBinding.cbBeforeSleep.isChecked());
                        med.setInstructions(dialogBinding.etInstructions.getText().toString().trim());
                        
                        medicineList.add(med);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitPrescription() {
        String diagnosis = binding.etDiagnosis.getText().toString().trim();
        String notes = binding.etNotes.getText().toString().trim();

        if (TextUtils.isEmpty(diagnosis)) {
            binding.etDiagnosis.setError("Diagnosis is required");
            return;
        }

        if (medicineList.isEmpty()) {
            Toast.makeText(this, "Please add at least one medicine", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSubmitPrescription.setEnabled(false);
        binding.btnSubmitPrescription.setText("Processing...");

        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String doctorName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (TextUtils.isEmpty(doctorName)) doctorName = "Doctor";
        
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        DatabaseReference presRef = mDatabase.child("Prescriptions").push();
        String presId = presRef.getKey();
        
        Prescription prescription = new Prescription(presId, appointmentId, patientId, patientName, doctorId, doctorName, diagnosis, medicineList, notes, date);

        presRef.setValue(prescription).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mDatabase.child("Appointments").child(appointmentId).child("status").setValue("Completed");
                Toast.makeText(PrescriptionActivity.this, "Prescription saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                binding.btnSubmitPrescription.setEnabled(true);
                binding.btnSubmitPrescription.setText("Submit Prescription");
                Toast.makeText(this, "Failed to save: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}