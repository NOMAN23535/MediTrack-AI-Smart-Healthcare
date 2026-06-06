package com.example.meditrackaiproject.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meditrackaiproject.databinding.ActivityPhoneAuthBinding;
import com.example.meditrackaiproject.patient.PatientDashboardActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private ActivityPhoneAuthBinding binding;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnSendOtp.setOnClickListener(v -> {
            String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVerificationCode(phoneNumber);
        });

        binding.btnVerifyOtp.setOnClickListener(v -> {
            String code = binding.etOtp.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                Toast.makeText(this, "Enter valid OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSendOtp.setEnabled(false);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            binding.progressBar.setVisibility(View.GONE);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSendOtp.setEnabled(true);
            Toast.makeText(PhoneAuthActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            binding.progressBar.setVisibility(View.GONE);
            mVerificationId = verificationId;
            mResendToken = token;
            
            binding.layoutPhoneInput.setVisibility(View.GONE);
            binding.layoutOtpInput.setVisibility(View.VISIBLE);
            Toast.makeText(PhoneAuthActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        binding.progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Successfully signed in. Check if user exists in DB, else register as Patient.
                        Intent intent = new Intent(PhoneAuthActivity.this, LoginActivity.class);
                        intent.putExtra("checkRole", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PhoneAuthActivity.this, "Sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}