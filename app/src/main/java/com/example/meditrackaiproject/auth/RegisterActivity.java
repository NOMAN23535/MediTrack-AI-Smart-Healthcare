package com.example.meditrackaiproject.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.MainActivity;
import com.example.meditrackaiproject.databinding.ActivityRegisterBinding;
import com.example.meditrackaiproject.models.Doctor;
import com.example.meditrackaiproject.models.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rbPatient.getId()) {
                binding.layoutPatient.setVisibility(View.VISIBLE);
                binding.layoutDoctor.setVisibility(View.GONE);
            } else {
                binding.layoutPatient.setVisibility(View.GONE);
                binding.layoutDoctor.setVisibility(View.VISIBLE);
            }
        });

        binding.btnRegister.setOnClickListener(v -> registerUser());
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.rbPatient.isChecked()) {
            registerPatient(fullName, email, password, phone);
        } else {
            registerDoctor(fullName, email, password, phone);
        }
    }

    private void registerPatient(String fullName, String email, String password, String phone) {
        String ageStr = binding.etAge.getText().toString().trim();
        String gender = binding.spGender.getSelectedItem().toString();

        if (TextUtils.isEmpty(ageStr) || gender.equals("Select Gender")) {
            Toast.makeText(this, "Please provide age and gender", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        Patient patient = new Patient(uid, fullName, email, phone, age, gender);
                        saveUserToDatabase(patient);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerDoctor(String fullName, String email, String password, String phone) {
        String specialization = binding.etSpecialization.getText().toString().trim();
        String qualifications = binding.etQualifications.getText().toString().trim();
        String experience = binding.etExperience.getText().toString().trim();

        if (TextUtils.isEmpty(specialization) || TextUtils.isEmpty(qualifications) || TextUtils.isEmpty(experience)) {
            Toast.makeText(this, "Please fill doctor details", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        Doctor doctor = new Doctor(uid, fullName, email, phone, specialization, qualifications, experience);
                        saveUserToDatabase(doctor);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(Object user) {
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(uid).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        if (user instanceof Patient) {
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        } else {
                            mAuth.signOut();
                            Toast.makeText(RegisterActivity.this, "Registration successful. Waiting for Admin approval.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
    }
}