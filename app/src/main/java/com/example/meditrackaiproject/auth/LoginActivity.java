package com.example.meditrackaiproject.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.admin.AdminDashboardActivity;
import com.example.meditrackaiproject.databinding.ActivityLoginBinding;
import com.example.meditrackaiproject.doctor.DoctorDashboardActivity;
import com.example.meditrackaiproject.patient.PatientDashboardActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        selectedRole = getIntent().getStringExtra("ROLE");
        if (selectedRole != null) {
            binding.tvRoleSubtitle.setText("Login as " + selectedRole);
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID") // User needs to replace this
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnLogin.setOnClickListener(v -> loginUser());
        
        binding.tvRegister.setOnClickListener(v -> {
            if ("Doctor".equals(selectedRole)) {
                startActivity(new Intent(LoginActivity.this, RegisterDoctorActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, RegisterPatientActivity.class));
            }
        });

        binding.ivGoogleLogin.setOnClickListener(v -> signInWithGoogle());
        
        binding.ivPhoneLogin.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, PhoneAuthActivity.class));
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email to reset password", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.ivBack.setOnClickListener(v -> finish());
    }

    private void loginUser() {
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

        binding.btnLogin.setText("Logging in...");
        binding.btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkUserRole();
                    } else {
                        binding.btnLogin.setText("Login");
                        binding.btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInDatabase(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Firebase auth failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInDatabase(FirebaseUser user) {
        mDatabase.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    checkUserRole();
                } else {
                    if ("Doctor".equals(selectedRole)) {
                        Intent intent = new Intent(LoginActivity.this, RegisterDoctorActivity.class);
                        intent.putExtra("isGoogle", true);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, RegisterPatientActivity.class);
                        intent.putExtra("isGoogle", true);
                        startActivity(intent);
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserRole() {
        if (mAuth.getCurrentUser() == null) return;
        
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if ("Doctor".equals(role)) {
                        String status = snapshot.child("status").getValue(String.class);
                        if ("Approved".equals(status)) {
                            Toast.makeText(LoginActivity.this, "Welcome Doctor!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DoctorDashboardActivity.class));
                            finishAffinity();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(LoginActivity.this, "Your account is pending approval.", Toast.LENGTH_LONG).show();
                            binding.btnLogin.setText("Login");
                            binding.btnLogin.setEnabled(true);
                        }
                    } else if ("Patient".equals(role)) {
                        Toast.makeText(LoginActivity.this, "Welcome Patient!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, PatientDashboardActivity.class));
                        finishAffinity();
                    } else if ("Admin".equals(role)) {
                        Toast.makeText(LoginActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        finishAffinity();
                    } else {
                        // Default case
                        startActivity(new Intent(LoginActivity.this, PatientDashboardActivity.class));
                        finishAffinity();
                    }
                } else {
                    // If user exists in Auth but not in Database
                    startActivity(new Intent(LoginActivity.this, RoleSelectionActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.btnLogin.setText("Login");
                binding.btnLogin.setEnabled(true);
            }
        });
    }
}