package com.example.meditrackaiproject.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.databinding.ActivityChangePasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnUpdatePassword.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            binding.etNewPassword.setError("Password required");
            return;
        }
        if (newPassword.length() < 6) {
            binding.etNewPassword.setError("Min 6 characters required");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnUpdatePassword.setEnabled(false);

            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnUpdatePassword.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}