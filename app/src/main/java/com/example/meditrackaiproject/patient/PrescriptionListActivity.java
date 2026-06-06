package com.example.meditrackaiproject.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meditrackaiproject.databinding.ActivityPrescriptionListBinding;
import com.example.meditrackaiproject.models.Prescription;
import com.example.meditrackaiproject.utils.PdfGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionListActivity extends AppCompatActivity {

    private ActivityPrescriptionListBinding binding;
    private DatabaseReference mDatabase;
    private String userId;
    private List<Prescription> prescriptionList;
    private PrescriptionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Prescriptions");

        prescriptionList = new ArrayList<>();
        adapter = new PrescriptionAdapter(prescriptionList, this::downloadPrescription);

        binding.rvPrescriptions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPrescriptions.setAdapter(adapter);

        loadPrescriptions();
    }

    private void loadPrescriptions() {
        mDatabase.orderByChild("patientId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        prescriptionList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Prescription prescription = postSnapshot.getValue(Prescription.class);
                            if (prescription != null) {
                                prescriptionList.add(prescription);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PrescriptionListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downloadPrescription(Prescription prescription) {
        try {
            File pdfFile = PdfGenerator.generatePrescriptionPdf(this, prescription);
            Toast.makeText(this, "PDF Generated: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            
            // Open the PDF
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            
        } catch (Exception e) {
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}