package com.example.meditrackaiproject.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.databinding.ActivityRegisterPatientBinding;
import com.example.meditrackaiproject.models.Patient;
import com.example.meditrackaiproject.patient.PatientDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPatientActivity extends AppCompatActivity {

    private ActivityRegisterPatientBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isExternalAuth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        isExternalAuth = getIntent().getBooleanExtra("isGoogle", false) || mAuth.getCurrentUser() != null;

        if (isExternalAuth && mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            binding.etEmail.setText(user.getEmail());
            binding.etEmail.setEnabled(false);
            binding.etPassword.setVisibility(View.GONE);
            binding.etConfirmPassword.setVisibility(View.GONE);
            binding.tvPasswordLabel.setVisibility(View.GONE);
            binding.tvConfirmPasswordLabel.setVisibility(View.GONE);
            binding.btnRegister.setText("Complete Profile");
            
            if (user.getPhotoUrl() != null) {
                imageUri = user.getPhotoUrl();
                Glide.with(this).load(imageUri).into(binding.ivProfile);
            }
        }

        binding.ivBack.setOnClickListener(v -> finish());
        binding.tvLogin.setOnClickListener(v -> finish());
        binding.ivProfile.setOnClickListener(v -> openImagePicker());

        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(adapter);

        binding.btnRegister.setOnClickListener(v -> registerPatient());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Take persistent permission for the URI
            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            binding.ivProfile.setPadding(0, 0, 0, 0);
            Glide.with(this).load(imageUri).into(binding.ivProfile);
        }
    }

    private void registerPatient() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String ageStr = binding.etAge.getText().toString().trim();
        String gender = binding.spGender.getSelectedItem().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || 
            TextUtils.isEmpty(phone) || TextUtils.isEmpty(ageStr) || gender.equals("Select Gender")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnRegister.setEnabled(false);
        binding.btnRegister.setText("Processing...");

        if (!isExternalAuth) {
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                binding.btnRegister.setEnabled(true);
                binding.btnRegister.setText("Create Patient Account");
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                binding.btnRegister.setEnabled(true);
                binding.btnRegister.setText("Create Patient Account");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            savePatientToDatabase(uid, fullName, email, phone, age, gender, imageUri != null ? imageUri.toString() : null);
                        } else {
                            binding.btnRegister.setEnabled(true);
                            binding.btnRegister.setText("Create Patient Account");
                            Toast.makeText(RegisterPatientActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            String uid = mAuth.getCurrentUser().getUid();
            savePatientToDatabase(uid, fullName, email, phone, age, gender, imageUri != null ? imageUri.toString() : null);
        }
    }

    private void savePatientToDatabase(String uid, String fullName, String email, String phone, int age, String gender, String imageUrl) {
        Patient patient = new Patient(uid, fullName, email, phone, age, gender);
        patient.setRole("Patient");
        if (imageUrl != null) patient.setProfileImageUrl(imageUrl);
        
        mDatabase.child("Users").child(uid).setValue(patient)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterPatientActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterPatientActivity.this, PatientDashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        binding.btnRegister.setEnabled(true);
                        binding.btnRegister.setText("Create Patient Account");
                        Toast.makeText(RegisterPatientActivity.this, "Database error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}