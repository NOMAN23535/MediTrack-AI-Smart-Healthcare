package com.example.meditrackaiproject.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.databinding.ActivityRegistrationSubmittedBinding;

public class RegistrationSubmittedActivity extends AppCompatActivity {

    private ActivityRegistrationSubmittedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationSubmittedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnOkay.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }
}