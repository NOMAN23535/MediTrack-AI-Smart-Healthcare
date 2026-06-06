package com.example.meditrackaiproject.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.admin.AdminDashboardActivity;
import com.example.meditrackaiproject.databinding.ActivityAdminLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginActivity extends AppCompatActivity {

    private ActivityAdminLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                binding.etEmail.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                binding.etPassword.setError("Password is required");
                return;
            }

            // Requirement 3: Admin Fixed Credentials
            if (email.equals("admin@gmail.com") && password.equals("123456")) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finishAffinity();
            } else {
                // Also allow Firebase-based admin if configured, but prioritizing fixed credentials
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // You might want to check the role in DB here too, but for Admin activity we assume intent
                                startActivity(new Intent(this, AdminDashboardActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(this, "Invalid Admin Credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}