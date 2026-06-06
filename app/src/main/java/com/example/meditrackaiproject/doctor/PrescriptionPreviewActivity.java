package com.example.meditrackaiproject.doctor;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.databinding.ActivityPrescriptionPreviewBinding;

public class PrescriptionPreviewActivity extends AppCompatActivity {

    private ActivityPrescriptionPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Downloading Prescription PDF...", Toast.LENGTH_SHORT).show();
        });

        binding.btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Sharing Prescription...", Toast.LENGTH_SHORT).show();
        });
    }
}