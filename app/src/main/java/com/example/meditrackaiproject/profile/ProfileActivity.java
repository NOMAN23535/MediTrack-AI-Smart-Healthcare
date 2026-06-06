package com.example.meditrackaiproject.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.auth.ChangePasswordActivity;
import com.example.meditrackaiproject.auth.RoleSelectionActivity;
import com.example.meditrackaiproject.databinding.ActivityProfileBinding;
import com.example.meditrackaiproject.notifications.NotificationsActivity;
import com.example.meditrackaiproject.patient.MedicineReportsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DataSnapshot userSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, RoleSelectionActivity.class));
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        loadProfileData();

        binding.ivBack.setOnClickListener(v -> finish());
        binding.ivProfile.setOnClickListener(v -> openImagePicker());
        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        binding.btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
        });

        binding.btnNotificationSettings.setOnClickListener(v -> {
            if (userSnapshot != null) {
                String role = userSnapshot.child("role").getValue(String.class);
                if ("Patient".equals(role)) {
                    startActivity(new Intent(ProfileActivity.this, MedicineReportsActivity.class));
                } else {
                    startActivity(new Intent(ProfileActivity.this, NotificationsActivity.class));
                }
            } else {
                startActivity(new Intent(ProfileActivity.this, NotificationsActivity.class));
            }
        });

        binding.btnHelpSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:msaad17060@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - MediTrack.AI");
            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (Exception e) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, RoleSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfileData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userSnapshot = snapshot;
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String imageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    
                    binding.tvUserName.setText(name != null ? name : "User Name");
                    binding.tvUserEmail.setText(email != null ? email : "Email Not Set");
                    
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(Uri.parse(imageUrl))
                                .placeholder(R.drawable.profile_1)
                                .into(binding.ivProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showEditProfileDialog() {
        if (userSnapshot == null) return;

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = view.findViewById(R.id.etFullName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etAddress = view.findViewById(R.id.etAddress);
        EditText etAge = view.findViewById(R.id.etAge);
        EditText etGender = view.findViewById(R.id.etGender);
        EditText etCity = view.findViewById(R.id.etCity);
        EditText etCountry = view.findViewById(R.id.etCountry);
        
        LinearLayout layoutDoctor = view.findViewById(R.id.layoutDoctorFields);
        EditText etQualification = view.findViewById(R.id.etQualification);
        EditText etExperience = view.findViewById(R.id.etExperience);
        EditText etSpecialization = view.findViewById(R.id.etSpecialization);

        // Populate fields
        etName.setText(userSnapshot.child("fullName").getValue(String.class));
        etPhone.setText(userSnapshot.child("phone").getValue(String.class));
        etAddress.setText(userSnapshot.child("address").getValue(String.class));
        Object ageObj = userSnapshot.child("age").getValue();
        etAge.setText(ageObj != null ? ageObj.toString() : "");
        etGender.setText(userSnapshot.child("gender").getValue(String.class));
        etCity.setText(userSnapshot.child("city").getValue(String.class));
        etCountry.setText(userSnapshot.child("country").getValue(String.class));

        String role = userSnapshot.child("role").getValue(String.class);
        if ("Doctor".equals(role)) {
            layoutDoctor.setVisibility(View.VISIBLE);
            etQualification.setText(userSnapshot.child("qualifications").getValue(String.class));
            etExperience.setText(userSnapshot.child("experience").getValue(String.class));
            etSpecialization.setText(userSnapshot.child("specialization").getValue(String.class));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                Map<String, Object> updates = new HashMap<>();
                updates.put("fullName", etName.getText().toString().trim());
                updates.put("phone", etPhone.getText().toString().trim());
                updates.put("address", etAddress.getText().toString().trim());
                updates.put("gender", etGender.getText().toString().trim());
                updates.put("city", etCity.getText().toString().trim());
                updates.put("country", etCountry.getText().toString().trim());
                
                String ageStr = etAge.getText().toString().trim();
                if (!ageStr.isEmpty()) {
                    try {
                        updates.put("age", Integer.parseInt(ageStr));
                    } catch (NumberFormatException e) {
                        updates.put("age", 0);
                    }
                }

                if ("Doctor".equals(role)) {
                    updates.put("qualifications", etQualification.getText().toString().trim());
                    updates.put("experience", etExperience.getText().toString().trim());
                    updates.put("specialization", etSpecialization.getText().toString().trim());
                }

                mDatabase.updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        dialog.show();
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
            Glide.with(this).load(imageUri).into(binding.ivProfile);
            updateProfileImageInDatabase();
        }
    }

    private void updateProfileImageInDatabase() {
        if (imageUri != null) {
            mDatabase.child("profileImageUrl").setValue(imageUri.toString())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update image", Toast.LENGTH_SHORT).show());
        }
    }
}