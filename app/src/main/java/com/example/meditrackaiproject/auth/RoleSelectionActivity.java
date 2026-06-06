package com.example.meditrackaiproject.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import com.example.meditrackaiproject.databinding.ActivityRoleSelectionBinding;

public class RoleSelectionActivity extends AppCompatActivity {

    private ActivityRoleSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Professional Touch: Edge-to-edge and light status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        
        binding = ActivityRoleSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAnimations();
        setupClickListeners();
    }

    private void setupAnimations() {
        // Initial state for flip animation: Rotate and hide
        binding.cardPatient.setRotationY(-90f);
        binding.cardDoctor.setRotationY(-90f);
        binding.cardAdmin.setRotationY(-90f);
        
        binding.cardPatient.setAlpha(0f);
        binding.cardDoctor.setAlpha(0f);
        binding.cardAdmin.setAlpha(0f);

        // Sequence flip animation (Patient -> Doctor -> Admin)
        binding.cardPatient.animate()
                .rotationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(200)
                .start();

        binding.cardDoctor.animate()
                .rotationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(400)
                .start();

        binding.cardAdmin.animate()
                .rotationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(600)
                .start();
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> finish());

        binding.cardPatient.setOnClickListener(v -> navigateToLogin("Patient"));
        binding.cardDoctor.setOnClickListener(v -> navigateToLogin("Doctor"));
        binding.cardAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("ROLE", role);
        startActivity(intent);
    }
}
