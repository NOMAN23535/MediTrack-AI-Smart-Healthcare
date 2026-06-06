package com.example.meditrackaiproject.auth;

import android.app.ProgressDialog;
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
import com.example.meditrackaiproject.databinding.ActivityRegisterDoctorBinding;
import com.example.meditrackaiproject.models.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterDoctorActivity extends AppCompatActivity {

    private ActivityRegisterDoctorBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait while we set up your professional profile...");
        progressDialog.setCancelable(false);

        // Set up specialization spinner
        String[] specializations = getResources().getStringArray(R.array.specializations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, specializations);
        binding.spinnerSpecialization.setAdapter(adapter);

        binding.ivBack.setOnClickListener(v -> finish());
        binding.ivProfile.setOnClickListener(v -> openImagePicker());
        binding.btnSubmit.setOnClickListener(v -> validateAndRegister());
        binding.tvLogin.setOnClickListener(v -> finish());
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
            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            binding.ivProfile.setPadding(0, 0, 0, 0);
            Glide.with(this).load(imageUri).centerCrop().into(binding.ivProfile);
        }
    }

    private void validateAndRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String specialization = binding.spinnerSpecialization.getText().toString().trim();
        String qualifications = binding.etQualifications.getText().toString().trim();
        String experience = binding.etExperience.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) { binding.etFullName.setError("Required"); return; }
        if (TextUtils.isEmpty(email)) { binding.etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(password) || password.length() < 6) { binding.etPassword.setError("Min 6 chars"); return; }
        if (TextUtils.isEmpty(phone)) { binding.etPhone.setError("Required"); return; }
        if (TextUtils.isEmpty(specialization)) { binding.spinnerSpecialization.setError("Required"); return; }
        if (imageUri == null) { Toast.makeText(this, "Please upload a profile picture", Toast.LENGTH_SHORT).show(); return; }

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        saveDoctorToDatabase(uid, fullName, email, phone, specialization, qualifications, experience, imageUri.toString());
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterDoctorActivity.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveDoctorToDatabase(String uid, String fullName, String email, String phone, String specialization, String qualifications, String experience, String imageUrl) {
        progressDialog.setMessage("Saving profile data...");
        Doctor doctor = new Doctor(uid, fullName, email, phone, specialization, qualifications, experience);
        doctor.setProfileImageUrl(imageUrl);
        doctor.setRole("Doctor");
        doctor.setStatus("Pending");

        mDatabase.child("Users").child(uid).setValue(doctor)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        mAuth.signOut(); // Doctor must wait for approval
                        Toast.makeText(RegisterDoctorActivity.this, "Registration successful! Pending Admin Approval.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterDoctorActivity.this, RegistrationSubmittedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterDoctorActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}